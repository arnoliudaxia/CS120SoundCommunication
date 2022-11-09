package OSI.MAC;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.UserSettings;
import OSI.Link.BitPacker;
import OSI.Link.frameConfig;
import utils.CRC;
import utils.DebugHelper;
import utils.smartConvertor;

import java.util.*;


public class MACBufferController {
    BitPacker bitPacker = null;
    /**
     * 发包时候的序号
     */
    private int seq = 1;

    public final int payloadLength = MACFrame.SEGEMENT[3];

    MACBufferController() {
        if (MACLayer.macBufferController == null) {
            MACLayer.macBufferController = this;
            bitPacker = new BitPacker(48000);

        } else {
            System.out.println("MACBufferController is already created");
        }
    }

    /**
     * MAC层介绍到数据后规格化为MACFrames并加入这个队列，
     * 然后真正send的时候从这里拿数据
     */
    public final LinkedList<MACFrame> downStreamQueue = new LinkedList<>();

    /**
     * 发送之后的MACFrame会被放在这里，等待ACK，超时就重新加入downStreamQueue
     */
//    private final LinkedList<Pair<Long,MACFrame>> resendQueue=new LinkedList<>();
    /**
     * MAC层接受到的一个个frame加入这个队列
     */
    public final Queue<MACFrame> upStreamQueue = new LinkedList<>();

    /**
     * 记录在该阶段中收到了多少个包
     */
    public int receiveFramesCount = 0;

    public HashSet<Integer> receiveFramesSeq = new HashSet<>();

    public final LinkedList<MACFrame> LastSendFrames = new LinkedList<>();

    /**
     * 一个过于简单的效验算法,只看payload中1的数量对128取余(保证位数)
     *
     * @param input payload数据
     * @return 效验码
     */
    private Integer checkCode_NumberOfOnes(List<Integer> input) {
        return Math.toIntExact(input.stream().filter((x) -> {
            return x == 1;
        }).count() % 64);
    }

    /**
     * 从上层获取数据后，MAC层会尽快把包发给下层（依赖MAC状态机）
     *
     * @param data
     */
    public void trySend(ArrayList<Integer> data) {
        synchronized (downStreamQueue) {
            while (data.size() > 0) {
                var payload = data.subList(0, Math.min(payloadLength, data.size()));
                while (payload.size() != payloadLength) {
                    payload.add(0);
                    DebugHelper.log("填充数据!");
                }
                MACFrame frame = new MACFrame(seq, new ArrayList<>(payload), CRC.crc16(new ArrayList<>(payload)), 0, DeviceSettings.MACAddress);
                downStreamQueue.add(frame);
                seq++;
                data.subList(0, payloadLength).clear();
            }

        }
//        MACLayer.macStateMachine.TxPending=true;
    }

    private final Queue<Integer> ACKs = new LinkedList<>();

    public void sendACK() {
        ArrayList<Integer> payload = new ArrayList<>();
        if (ACKs.size() > UserSettings.Number_Frames_True) {
            DebugHelper.log("ACKs.size()>UserSettings.Number_Frames_True");
        }
        while (ACKs.size() > 0) {
            payload.addAll(smartConvertor.exactBitsOfNumber(ACKs.poll(), 10));
        }
        while (payload.size() != payloadLength-10) {
            payload.add(0);
        }
        for (int i = 0; i < 5; i++) {
            payload.add(1);
            payload.add(0);
        }
        MACFrame frame = new MACFrame(0, payload, CRC.crc16(payload), 1, DeviceSettings.MACAddress);

        synchronized (downStreamQueue) {
            downStreamQueue.add(0, frame);
        }
        DebugHelper.log("发送ACK,crc is" + frame.crc);
    }

    public int framesSendCount = 0;

    /**
     * 一次性发送UserSettings.Number_Frames_True个frame
     */
    public void __send() {

        if (ACKs.size() > 0) {
            sendACK();
        }
        MACFrame frame = downStreamQueue.poll();
        if (frame == null) {
            DebugHelper.log("发送队列里没有东西朋友!");
        }
        else {
            if (frame.frame_type == 0) {
                LastSendFrames.add(frame);
            }
            DebugHelper.log(String.format("发送序号为%d的包,效验码为%d", frame.seq, frame.crc));
            ArrayList<Integer> sendTemp = new ArrayList<>();
            sendTemp.addAll(smartConvertor.exactBitsOfNumber(frame.seq, 10));
            sendTemp.addAll(smartConvertor.exactBitsOfNumber(frame.frame_type, 2));
            sendTemp.addAll(smartConvertor.exactBitsOfNumber(frame.src_mac, 2));
            sendTemp.addAll(frame.payload);
            sendTemp.addAll(smartConvertor.exactBitsOfNumber(frame.crc, 16));
            assert sendTemp.size() == frameConfig.bitLength;
            bitPacker.AppendData(sendTemp);
            bitPacker.padding();
        }

        framesSendCount++;
        MACLayer.macStateMachine.TxDone = true;
        if (framesSendCount >= UserSettings.Number_Frames_True) {
            //你已经发得够多了别贪
            DebugHelper.log("我发完了等待接收");
            framesSendCount = 0;
        } else {
            MACLayer.macStateMachine.TxPending = true;
        }
    }
    public int dropCount = 0;
    public void __receive(ArrayList<Integer> data) {
        if(dropCount<UserSettings.Number_Frames_True){
            DebugHelper.log("应该是自己的包，我直接丢");
            dropCount++;
            MACLayer.macStateMachine.RxDone = true;
            return;
        }
        var receivedFrame = new MACFrame(data);
        //checkCode是包里的crc,checkCode_compute是这里根据payload算出来的crc
        if(receivedFrame.seq==0){
            //对ACK包忽略中间的内容
            for (int i = 60; i < 160; i++) {
                receivedFrame.payload.set(i,0);
            }
            receivedFrame.payload.set(160,1);
            receivedFrame.payload.set(161,0);
            receivedFrame.payload.set(162,1);
            receivedFrame.payload.set(163,0);
            receivedFrame.payload.set(164,1);
            receivedFrame.payload.set(165,0);
            receivedFrame.payload.set(166,1);
            receivedFrame.payload.set(167,0);
            receivedFrame.payload.set(168,1);
            receivedFrame.payload.set(169,0);

        }
        int checkCode_compute = CRC.crc16(receivedFrame.payload);
        DebugHelper.log(String.format("收到序号为%d包,效验码内容为%d,计算为%d", receivedFrame.seq, receivedFrame.crc, checkCode_compute));

        if (checkCode_compute != receivedFrame.crc) {
            DebugHelper.log(String.format("Warning: 包%d效验不通过,丢弃数据包!", receivedFrame.seq));
        } else {
            //如果是自己发的包不用管
            if (receivedFrame.src_mac == DeviceSettings.MACAddress) {
                DebugHelper.log("收到自己的包,不需要处理直接丢弃");
                receiveFramesCount--;
            } else {
                //如果是数据包，需要发送ACK
                if (receivedFrame.frame_type == 0) {
                    if(!ACKs.contains(receivedFrame.seq))
                    {
                        ACKs.add(receivedFrame.seq);

                    }

                    if (!receiveFramesSeq.contains(receivedFrame.seq) ) {
                        //包没有问题就存下来
                        synchronized (upStreamQueue) {
                            upStreamQueue.add(receivedFrame);
                        }
                        receiveFramesSeq.add(receivedFrame.seq);
                    }

                }
                if (receivedFrame.frame_type == 1) {
                    //如果是ACK包，需要从重发队列里删除对应的包
                    //先解析ACK里包含哪些frame，payload里每10位是一个seq
                    for (int i = 0; i < receivedFrame.payload.size() - 10; i += 10) {
                        int recieveSeq = smartConvertor.mergeBitsToInteger(new ArrayList<>(receivedFrame.payload.subList(i, i + 10)));
                        if (recieveSeq == 0) {
                            break;
                        }
                        DebugHelper.log("包" + recieveSeq + "发送成功");
                        LastSendFrames.removeIf(x -> x.seq == recieveSeq);
                    }
                }
                //通知其他人有frame进来了

                synchronized (GlobalEvent.Receive_Frame) {
                    GlobalEvent.Receive_Frame.notifyAll();
                }
            }


        }


        receiveFramesCount++;
        if (receiveFramesCount >= UserSettings.Number_Frames_True) {
            receiveFramesCount = 0;
            synchronized (GlobalEvent.ALL_DATA_Recieved) {
                GlobalEvent.ALL_DATA_Recieved.notifyAll();
            }
        }
        MACLayer.macStateMachine.RxDone = true;

    }

    public void resend() {
        synchronized (downStreamQueue) {
            while (!LastSendFrames.isEmpty()) {
                downStreamQueue.addFirst(LastSendFrames.poll());
            }
        }

    }
}

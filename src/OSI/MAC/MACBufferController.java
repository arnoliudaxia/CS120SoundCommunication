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
                if(payload.size()<payloadLength)
                {
                    DebugHelper.log("填充数据!");
                }
                while (payload.size() != payloadLength) {
                    payload.add(0);
                    payload.add(1);
                }
                MACFrame frame = new MACFrame(seq, new ArrayList<>(payload), -1, 0, DeviceSettings.MACAddress);
                frame.crc=CRC.crc16(frame);

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
        if (ACKs.size() > UserSettings.Number_Frames_Trun) {
            DebugHelper.log("ACKs.size()>UserSettings.Number_Frames_True");
        }
        while (ACKs.size() > 0) {
            payload.addAll(smartConvertor.exactBitsOfNumber(ACKs.poll(), 10));
        }
        while (payload.size() != payloadLength - 10) {
            payload.add(0);
        }
        for (int i = 0; i < 5; i++) {
            payload.add(1);
            payload.add(0);
        }
        var crcP=payload;

        MACFrame frame = new MACFrame(0, payload, -1, 1, DeviceSettings.MACAddress);
        frame.crc=CRC.crc16(frame);

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
            DebugHelper.log("没有要发送的东西了，发送终止包");
            frame = new MACFrame(666, new ArrayList<>(Collections.nCopies(170,0)), -1, 3, DeviceSettings.MACAddress);
        }
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
        MACLayer.macStateMachine.TxDone = true;
        if(!downStreamQueue.isEmpty()) {
            MACLayer.macStateMachine.TxPending = true;
        }
    }


    public void __receive(ArrayList<Integer> data) {
        var receivedFrame = new MACFrame(data);
        //checkCode是包里的crc,checkCode_compute是这里根据payload算出来的crc
        int checkCode_compute = CRC.crc16(receivedFrame);
        DebugHelper.log(String.format("收到序号为%d包,效验码内容为%d,计算为%d", receivedFrame.seq, receivedFrame.crc, checkCode_compute));

        if (receivedFrame.seq != 0 && checkCode_compute != receivedFrame.crc) {
            DebugHelper.log(String.format("Warning: 包%d效验不通过,丢弃数据包!", receivedFrame.seq));
        } else {
            //如果是自己发的包不用管
            //如果是数据包，需要发送ACK
            if(!(receivedFrame.src_mac==DeviceSettings.MACAddress))
            {
                if (receivedFrame.frame_type == 0) {
                    //数据包
                    if (!ACKs.contains(receivedFrame.seq)) {
                        ACKs.add(receivedFrame.seq);
                    }
                    if (!receiveFramesSeq.contains(receivedFrame.seq)) {
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
                if (receivedFrame.frame_type == 3) {
                    //终止包
                    DebugHelper.log("收到终止包");
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.notifyAll();
                    }
                }

                }

            //通知其他人有frame进来了

            synchronized (GlobalEvent.Receive_Frame) {
                GlobalEvent.Receive_Frame.notifyAll();
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
    public boolean isAllSent()
    {
        return downStreamQueue.isEmpty()&&LastSendFrames.isEmpty();
    }

    public ArrayList<MACFrame> getFramesReceive(){
        ArrayList<MACFrame> rFrames = new ArrayList<>();
        synchronized (MACLayer.macBufferController.upStreamQueue) {
            while (!MACLayer.macBufferController.upStreamQueue.isEmpty()) {
                var frame = MACLayer.macBufferController.upStreamQueue.poll();
                rFrames.add(frame);
            }
        }
        rFrames.sort(Comparator.comparingInt(o -> o.seq));
        return rFrames;
    }
}

package OSI.MAC;

import OSI.Application.GlobalEvent;
import OSI.Application.UserSettings;
import OSI.Link.BitPacker;
import OSI.Link.frameConfig;
import utils.DebugHelper;
import utils.smartConvertor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class MACBufferController {
    BitPacker bitPacker = null;
    /**
     * 发包时候的序号
     */
    private  int seq=0;
    /**
     * MAC包每一段配置
     */
    private final int seqLength=10;
    private final int crcLength=6;
    private final int payloadLength= frameConfig.bitLength-seqLength-crcLength;

    MACBufferController() {
        if (MACLayer.macBufferController == null) {
            MACLayer.macBufferController = this;
            bitPacker=new BitPacker(48000);

        } else {
            System.out.println("MACBufferController is already created");
        }
    }

    /**
     * MAC层介绍到数据后规格化为MACFrames并加入这个队列，
     * 然后真正send的时候从这里拿数据
     */
    private final Queue<MACFrame> downStreamQueue=new LinkedList<>();
    /**
     * MAC层接受到的一个个frame加入这个队列
     */
    public final Queue<MACFrame> upStreamQueue=new LinkedList<>();

    /**
     * 记录在该阶段中收到了多少个包
     */
    public int receiveFramesCount =0;

    /**
     * 一个过于简单的效验算法,只看payload中1的数量对128取余(保证位数)
     * @param input payload数据
     * @return 效验码
     */
    private Integer checkCode_NumberOfOnes(List<Integer> input){
        return Math.toIntExact(input.stream().filter((x) -> {
            return x == 1;
        }).count()%128);
    }
    /**
     * 从上层获取数据后，MAC层会尽快把包发给下层（依赖MAC状态机）
     * @param data
     */
    public void trySend(ArrayList<Integer> data) {
        synchronized (downStreamQueue) {
            while (data.size()>0)
            {
                var payload=data.subList(0, Math.min(payloadLength, data.size()));
                while(payload.size()!=payloadLength)
                {
                    payload.add(0);
                }
                MACFrame frame=new MACFrame(seq, new ArrayList<>(payload), checkCode_NumberOfOnes(payload));
                downStreamQueue.add(frame);
                seq++;
                data.subList(0,payloadLength).clear();
            }

        }
        MACLayer.macStateMachine.TxPending=true;
    }

    public void __send(){
        synchronized (downStreamQueue) {
            while(downStreamQueue.size()>0) {
                var frame=downStreamQueue.poll();
                //每一个字段都需要发出去
                bitPacker.AppendData(smartConvertor.exactBitsOfNumber(frame.seq, 10));
                bitPacker.AppendData(frame.payload);
                bitPacker.AppendData(smartConvertor.exactBitsOfNumber(frame.crc,6));
                DebugHelper.log(String.format("发送序号为%d的包,效验码为%d",frame.seq,frame.crc));

            }
        }
        bitPacker.padding();
        MACLayer.macStateMachine.TxDone=true;
    }

    public void __receive(ArrayList<Integer> data){
        //data的前10位是序号,需要把二进制转换回数字
        var seqS=data.stream().limit(10).toList();
        int seq=smartConvertor.mergeBitsToInteger(seqS);
        data.subList(0,10).clear();
        //提取字段
        ArrayList<Integer> payload=new ArrayList<>(data.subList(0,payloadLength));
        ArrayList<Integer> crc=new ArrayList<>(data.subList(payloadLength+1,data.size()));
        //checkCode是包里的crc,checkCode_compute是这里根据payload算出来的crc
        int checkCode=smartConvertor.mergeBitsToInteger(crc);
        int checkCode_compute= checkCode_NumberOfOnes(payload);
        DebugHelper.log(String.format("收到序号为%d包,效验码内容为%d,计算为%d",seq,checkCode,checkCode_compute));
        if(checkCode_compute!=checkCode)
        {
            DebugHelper.log(String.format("Warning: 包%d效验不通过,丢弃数据包!",seq));
        }
        else {
            //包没有问题就存下来
            synchronized (upStreamQueue) {
                upStreamQueue.add(new MACFrame(seq,payload,checkCode));
            }
            //通知其他人有frame进来了
            synchronized (GlobalEvent.Receive_Frame){
                GlobalEvent.Receive_Frame.notifyAll();
            }
        }

        receiveFramesCount ++;
        if(receiveFramesCount >= UserSettings.Number_Frames_True){
            receiveFramesCount =0;
            synchronized (GlobalEvent.ALL_DATA_Recieved) {
                GlobalEvent.ALL_DATA_Recieved.notifyAll();
            }
        }
        MACLayer.macStateMachine.RxDone=true;

    }
}

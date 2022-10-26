package OSI.MAC;

import OSI.Application.GlobalEvent;
import OSI.Application.UserSettings;
import OSI.Link.BitPacker;
import com.mathworks.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MACBufferController {
    BitPacker bitPacker = null;

    MACBufferController() {
        if (MACLayer.macBufferController == null) {
            MACLayer.macBufferController = this;
            bitPacker=new BitPacker(48000);

        } else {
            System.out.println("MACBufferController is already created");
        }
    }

    private final Queue<ArrayList<Integer>> downStreamQueue=new LinkedList<>();
    /**
     * MAC层接受到的一个个frame，MAP的第一个Integer是存储包的序号的
     */
    public final Queue<MACFrame> upStreamQueue=new LinkedList<>();

    public int receiveBitCount=0;

    /**
     * 从上层获取数据后，MAC层会尽快把包发给下层（依赖MAC状态机）
     * @param data
     */
    public void trySend(ArrayList<Integer> data) {
        synchronized (downStreamQueue) {
            downStreamQueue.add(data);
        }
        MACLayer.macStateMachine.TxPending=true;
    }

    public void __send(){
        synchronized (downStreamQueue) {
            if(downStreamQueue.size()>0) {
                bitPacker.AppendData(downStreamQueue.poll());
            }
        }
//        bitPacker.AppendData(Objects.requireNonNull(downStreamQueue.poll()));
        bitPacker.padding();
        MACLayer.macStateMachine.TxDone=true;
    }

    public void __receive(ArrayList<Integer> data){
        synchronized (upStreamQueue) {
            //data的前10位是序号
            var seqS=data.stream().limit(10).toList();
            int seq=0;
            seq+=0b1000000000*seqS.get(0);
            seq+=0b0100000000*seqS.get(1);
            seq+=0b0010000000*seqS.get(2);
            seq+=0b0001000000*seqS.get(3);
            seq+=0b0000100000*seqS.get(4);
            seq+=0b0000010000*seqS.get(5);
            seq+=0b0000001000*seqS.get(6);
            seq+=0b0000000100*seqS.get(7);
            seq+=0b0000000010*seqS.get(8);
            seq+=0b0000000001*seqS.get(9);
            data.subList(0,10).clear();
//            Pair<Integer,ArrayList<Integer>> entry= new Pair<>(seq,data);
            //TODO 需要测试
            //TODO 在这里加入CRC
            upStreamQueue.add(new MACFrame(seq,data,-1));
        }
        receiveBitCount+=data.size();
        if(receiveBitCount>= UserSettings.WHOLE_DATA_LENGTH){
            synchronized (GlobalEvent.ALL_DATA_Recieved) {
                GlobalEvent.ALL_DATA_Recieved.notifyAll();
            }
        }
        MACLayer.macStateMachine.RxDone=true;
    }
}

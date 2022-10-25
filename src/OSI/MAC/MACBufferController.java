package OSI.MAC;

import OSI.Application.GlobalEvent;
import OSI.Link.BitPacker;

import java.util.ArrayList;
import java.util.LinkedList;
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
    public final Queue<ArrayList<Integer>> upStreamQueue=new LinkedList<>();
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
            upStreamQueue.add(data);
        }
        receiveBitCount+=data.size();
        if(receiveBitCount>=50000){
            synchronized (GlobalEvent.ALL_DATA_Recieved) {
                GlobalEvent.ALL_DATA_Recieved.notifyAll();
            }
        }
        MACLayer.macStateMachine.RxDone=true;
    }
}

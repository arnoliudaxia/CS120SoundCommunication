package OSI.MAC;

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
}

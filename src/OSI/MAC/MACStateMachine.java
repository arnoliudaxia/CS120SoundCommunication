package OSI.MAC;


import OSI.Application.UserSettings;
import utils.DebugHelper;

public class MACStateMachine {
    enum MACState {
        FrameDetection, Tx, Rx,
        Tx_waiting//想要发送，但是先听一下频道
    }
    public MACState macState;
    public boolean SIG=false;
    //#region Events
    public boolean PacketDetected=false;
    public boolean RxDone=false;
    public boolean TxDone=false;
    public boolean TxPending=false;
    public int preSum;
    /**
     * MAC状态改变notify机制，下层notify这个object来让MAC层状态转换，
     * 注意要在notify之前把事件置为true
     */
    //#endregion
    MACStateMachine() {
        if(MACLayer.macStateMachine==null) {
            MACLayer.macStateMachine = this;
            MACLayer.macStateMachine.macState = MACState.FrameDetection;
            new Thread(this::mainloop).start();
        }
        else {
            System.out.println("MACStateMachine is already created");
        }
    }
    public void mainloop()
    {
        while (!SIG) {

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stateTransfer();
            processState();
            throughputTest();
        }

    }

    /**
     * Send and pray protocol
     */
    private void stateTransfer()
    {

        switch (MACLayer.macStateMachine.macState) {
            case FrameDetection:
                //优先接收，然后再发
                if(PacketDetected)
                {
                    if(UserSettings.printStateLog)
                        DebugHelper.log("FrameDetection->Rx");
                    PacketDetected=false;
                    MACLayer.macStateMachine.macState=MACState.Rx;
                    break;
                }
                if(TxPending)
                {
                    if(UserSettings.printStateLog)
                        DebugHelper.log("FrameDetection->Tx_waiting");
                    TxPending=false;
                    MACLayer.macStateMachine.macState=MACState.Tx_waiting;
                    break;
                }

                break;
            case Tx_waiting:
                if(MACLayer.isChannelReady)
                {
                    //如果频道空闲，就发
                    if(UserSettings.printStateLog)
                        DebugHelper.log("Tx_waiting->Tx");
                    MACLayer.macStateMachine.macState=MACState.Tx;
                }
                break;
            case Tx:
                if(TxDone)
                {
                    if(UserSettings.printStateLog)
                        DebugHelper.log("Tx->FrameDetection");
                    TxDone=false;
                    MACLayer.macStateMachine.macState=MACState.FrameDetection;
                }
                break;
            case Rx:
                if(RxDone)
                {
                    if(UserSettings.printStateLog)
                        DebugHelper.log("Rx->FrameDetection");
                    RxDone=false;
                    MACLayer.macStateMachine.macState=MACState.FrameDetection;
                    PacketDetected=false;
                }
                break;
        }
    }
    private void processState()
    {
        switch (MACLayer.macStateMachine.macState) {
            case FrameDetection:
                //当处于FrameDetection状态时，MAC层不需要做什么
                break;
            case Tx:
                //让MACBufferController发送数据
                MACLayer.macBufferController.__send();
                break;
            case Rx:
                break;
        }
    }

        long startTime = System.currentTimeMillis();
    private void throughputTest(){
        if(MACLayer.macBufferController.upStreamQueue.isEmpty())startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);
        if(usedTime>=1000){
            int throughput=(preSum-MACLayer.macBufferController.downStreamQueue.size())*MACFrame.SEGEMENT[3];
            DebugHelper.log("带宽为"+throughput+"bps");
            preSum=MACLayer.macBufferController.downStreamQueue.size();
            startTime=endTime;
        }

    }

}

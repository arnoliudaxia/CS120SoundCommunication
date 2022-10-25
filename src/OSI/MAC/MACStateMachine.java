package OSI.MAC;


public class MACStateMachine {
    enum MACState {
        FrameDetection, Tx, Rx
    }
    public MACState macState;
    public boolean SIG=false;
    //#region Events
    public boolean PacketDetected=false;
    public boolean RxDone=false;
    public boolean TxDone=false;
    public boolean TxPending=false;
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
            stateTransfer();
            processState();
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
                    PacketDetected=false;
                    MACLayer.macStateMachine.macState=MACState.Rx;
                }
                if(TxPending)
                {
                    TxPending=false;
                    MACLayer.macStateMachine.macState=MACState.Tx;
                }

                break;
            case Tx:
                if(TxDone)
                {
                    TxDone=false;
                    MACLayer.macStateMachine.macState=MACState.FrameDetection;
                }
                break;
            case Rx:
                if(RxDone)
                {
                    RxDone=false;
                    MACLayer.macStateMachine.macState=MACState.FrameDetection;
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




}

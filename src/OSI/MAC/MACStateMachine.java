package OSI.MAC;

enum MACState {
    FrameDetection, Tx, Rx;
}
public class MACStateMachine {
    public static MACStateMachine macStateMachine;
    public MACState macState;
    public boolean SIG=false;
    //#region Events
    public boolean PacketDetected=false;
    public boolean RxDone=false;
    public boolean TxDone=false;
    public boolean TxPending=false;
    //#endregion
    MACStateMachine() {
        if(macStateMachine==null) {
            macStateMachine = this;
            macStateMachine.macState = MACState.FrameDetection;
            new Thread(new Runnable() {
                @Override
                public void run() {
                   mainloop();
                }
            }).start();
        }
        else {
            System.out.println("MACStateMachine is already created");
        }
    }
    public void mainloop()
    {
        while (!SIG) {
            stateTransfer();
        }

    }

    /**
     * Send and pray protocol
     */
    private void stateTransfer()
    {
        switch (macStateMachine.macState) {
            case FrameDetection:
                if(TxPending)
                {
                    TxPending=false;
                    macStateMachine.macState=MACState.Tx;
                }
                if(PacketDetected)
                {
                    PacketDetected=false;
                    macStateMachine.macState=MACState.Rx;
                }
                break;
            case Tx:
                if(TxDone)
                {
                    TxDone=false;
                    macStateMachine.macState=MACState.FrameDetection;
                }
                break;
            case Rx:
                if(RxDone)
                {
                    RxDone=false;
                    macStateMachine.macState=MACState.FrameDetection;
                }
                break;
        }
    }


}

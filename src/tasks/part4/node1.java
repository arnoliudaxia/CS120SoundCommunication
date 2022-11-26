package tasks.part4;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;

public class node1 {
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef=0.1f;
        MessageSender messager = new MessageSender();
        String message = "";
        while(true)
        {
            synchronized (GlobalEvent.ALL_DATA_Recieved)
            {
                try {
                    DebugHelper.logColorful("等待Node2", DebugHelper.printColor.GREEN);
                    GlobalEvent.ALL_DATA_Recieved.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if(message.equals("")){
                message=MACLayer.macBufferController.getMessage();
            }
            DebugHelper.logColorful("收到ICMP来自"+message, DebugHelper.printColor.BLUE);
            DebugHelper.logColorful("Reply INCMP"+message, DebugHelper.printColor.BLUE);
            messager.sendMessage("192.168.1.2");
            MACLayer.macStateMachine.TxPending = true;
        }

    }

}

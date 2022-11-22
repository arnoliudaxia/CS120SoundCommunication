package tasks.part2.check2;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.UserSettings;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.smartConvertor;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class n1 {
    public static void main(String[] args) throws IOException {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef=0.1f;
        DeviceSettings.MACAddress = 1;
        DeviceSettings.isSendEndPackage=false;
        UserSettings.Number_Frames_Trun=1;
        while(true){
            synchronized (GlobalEvent.ALL_DATA_Recieved){
                try{
                    GlobalEvent.ALL_DATA_Recieved.wait();
                }catch(InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
            while(!MACLayer.macBufferController.upStreamQueue.isEmpty()){
                var frames = MACLayer.macBufferController.getFramesReceive();
                byte[] data=new byte[frames.size()*170/8];
                ArrayList<Integer> information = new ArrayList<>();
                int read=0;
                for (var frame : frames) {
                    information.addAll(frame.payload);
                }
                for (int i = 0; i < information.size() - 8; i += 8) {
                    data[i / 8] = (byte) smartConvertor.mergeBitsToInteger(information.subList(i, i + 8));
                    read=i/8;
                }
                String s=new String(data, 0, read, Charset.defaultCharset());
                int endindex;
                if((endindex=s.lastIndexOf('ç'))!=-1)
                {
                    s=s.substring(0,endindex);
                    MACLayer.macBufferController.resend();
                    MACLayer.macStateMachine.TxPending = true;
                    System.out.println(s);
                    break;
                }
                System.out.println(s);
            }
            MACLayer.macBufferController.resend();
            MACLayer.macStateMachine.TxPending = true;
        }
    }
}

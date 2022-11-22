package tasks.part2.check2;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.smartConvertor;

import java.io.IOException;
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
        ArrayList<Integer> information = new ArrayList<>();
        int sentSentences=0;
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
                for (var frame : frames) {
                    information.addAll(frame.payload);
                }
                //先检查是否有ç
                boolean isContainEnd=false;
                for (int i = 0; i < information.size()-16; i+=4) {
                    byte[] charbyte=new byte[2];
                    charbyte[0]=(byte) smartConvertor.mergeBitsToInteger(information.subList(i, i + 8));
                    charbyte[1]=(byte) smartConvertor.mergeBitsToInteger(information.subList(i+8, i + 16));
                    var aa="ç".getBytes(Charset.defaultCharset());
                    if(new String(charbyte).equals("ç"))
                    {
                        isContainEnd=true;
                        break;
                    }
                }
                if(isContainEnd) {
                    sentSentences++;
                    byte[] data = new byte[information.size()/8+1];
                    for (int i = 0; i < information.size() - 8; i += 8) {
                        data[i / 8] = (byte) smartConvertor.mergeBitsToInteger(information.subList(i, i + 8));
                    }
                    information.clear();
                    String message=new String(data, Charset.defaultCharset());
                    DebugHelper.log("Message: " +message.substring(0,message.indexOf("ç")));
                }
            }
            MACLayer.macBufferController.resend();
            MACLayer.macStateMachine.TxPending = true;
        }
    }
}

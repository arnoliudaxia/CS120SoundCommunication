package tasks.part2.check1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.SystemController;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.smartConvertor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class node2 {

    public static void main(final String[] args) {
        //我的任务是要接受node1发来的数据然后发给node3
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef=0.16f;
        DeviceSettings.MACAddress = 1;
        DeviceSettings.isSendEndPackage=false;
        ArrayList<Integer> information = new ArrayList<>();

        //socket监听
        try(ServerSocket serverSocket = new ServerSocket(1111)) {
            System.out.println("等待连接");
            Socket client = serverSocket.accept();
            System.out.println("连接成功！");
            int sentSentences=0;

            while(true) {
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    try {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //吧接收到的每个frame通过socket发送出去
                if(true) {

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
                    DebugHelper.log("Size of information is "+information.size());
                    if(isContainEnd) {
                        DebugHelper.log("发socket");
                        sentSentences++;
                        byte[] data = new byte[2048];
                        for (int i = 0; i < information.size() - 8; i += 8) {
                            data[i / 8] = (byte) smartConvertor.mergeBitsToInteger(information.subList(i, i + 8));
                        }
                        client.getOutputStream().write(data);
                        information.clear();
                    }
                    if(sentSentences==30){
                        MACLayer.macBufferController.framesSendCount = 0;
                        MACLayer.macBufferController.resend();
                        MACLayer.macStateMachine.TxPending = true;
                        break;
                    }

                }
                //发ACK
                MACLayer.macBufferController.framesSendCount = 0;
                MACLayer.macBufferController.resend();
                MACLayer.macStateMachine.TxPending = true;


            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SystemController.threadBlockTime(5000);
        SystemController.shutdown();



    }
}

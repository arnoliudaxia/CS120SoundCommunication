package tasks.part2.check1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.SystemController;
import OSI.Application.UserSettings;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.smartConvertor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class node2 {

    public static void main(final String[] args) {
        //我的任务是要接受node1发来的数据然后发给node3
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef=0.2f;
        DeviceSettings.MACAddress = 1;
        UserSettings.Number_Frames_Trun=1;
        //socket监听
        try(ServerSocket serverSocket = new ServerSocket(1111)) {
            System.out.println("等待连接");
            Socket client = serverSocket.accept();
            System.out.println("连接成功！");

            while(true) {
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    try {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //吧接收到的每个frame通过socket发送出去
                if(MACLayer.macBufferController.isALLRecieve) {
                    while (!MACLayer.macBufferController.upStreamQueue.isEmpty()) {
                        var frames = MACLayer.macBufferController.getFramesReceive();
                        byte[] data = new byte[frames.size() * 170 / 8];
                        ArrayList<Integer> information = new ArrayList<>();
                        for (var frame : frames) {
                            information.addAll(frame.payload);
                        }
                        for (int i = 0; i < information.size() - 8; i += 8) {
                            data[i / 8] = (byte) smartConvertor.mergeBitsToInteger(information.subList(i, i + 8));
                        }
                        client.getOutputStream().write(data);
                    }
                    break;

                }
                MACLayer.macBufferController.isALLRecieve=true;
                //发ACK
                MACLayer.macBufferController.framesSendCount = 0;
                MACLayer.macBufferController.resend();
                MACLayer.macStateMachine.TxPending = true;


            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SystemController.shutdown();



    }
}

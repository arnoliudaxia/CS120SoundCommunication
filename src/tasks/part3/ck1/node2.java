package tasks.part3.ck1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static utils.smartConvertor.mergeBitsToInteger;

public class node2 {
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef = 0.16f;
        DeviceSettings.MACAddress = 0;
        try (ServerSocket serverSocket = new ServerSocket(1111)) {
            System.out.println("等待连接");
            Socket client = serverSocket.accept();
            System.out.println("连接成功！");
            while (true) {
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    try {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                var frames = MACLayer.macBufferController.getFramesReceive();
                ArrayList<String> IPs = new ArrayList<>();
                for (int i = 0; i < frames.size(); i++) {
                    List<Integer> rawData = frames.get(i).payload.subList(0, 32);
                    ArrayList<Integer> IP = new ArrayList<>();
                    for (int j = 0; j < 4; j++) {
                        IP.add(mergeBitsToInteger(rawData.subList(i * 8, (i + 1) * 8)));
                    }
                    String str = IP.stream().map(integer -> integer.toString()).collect(Collectors.joining("."));
                    IPs.add(str);
                }
                for(int i=0;i< IPs.size();i++){
                    client.getOutputStream().write(IPs.get(i).getBytes(Charset.defaultCharset()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

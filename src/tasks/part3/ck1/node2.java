package tasks.part3.ck1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static utils.smartConvertor.mergeBitsToInteger;

public class node2 {
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef = 0.16f;
        DeviceSettings.MACAddress = 0;
        MessageSender messager = new MessageSender();
        try (ServerSocket serverSocket = new ServerSocket(46569)) {
            System.out.println("等待连接");
            Socket client = serverSocket.accept();
            System .out.println("连接成功！");
            AudioHw.audioHwG.isRecording = true;
            while (true) {
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    try {
                        DebugHelper.logColorful("等待中", DebugHelper.printColor.GREEN);
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                var frame = MACLayer.macBufferController.upStreamQueue.poll();
                assert frame != null;
                List<Integer> rawData = frame.payload.subList(0, 32);
                ArrayList<Integer> IP = new ArrayList<>();
                for (int j = 0; j < 4; j++) {
                    IP.add(mergeBitsToInteger(rawData.subList(j * 8, (j + 1) * 8)));
                }
                String ip = IP.stream().map(Object::toString).collect(Collectors.joining("."));
                DebugHelper.logColorful("告诉python", DebugHelper.printColor.BLUE);
                client.getOutputStream().write(ip.getBytes(Charset.defaultCharset()));

                byte[] bytes = new byte[1024];
                int read = client.getInputStream().read(bytes);
                messager.sendBytes(bytes);
                DebugHelper.logColorful("收到python", DebugHelper.printColor.GREEN);
                MACLayer.macStateMachine.TxPending = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

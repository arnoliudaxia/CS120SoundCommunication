package tasks.part4;

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

public class node2 {
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef = 0.16f;
        DeviceSettings.MACAddress = 1;
        AudioHw.audioHwG.isRecording = true;
        MessageSender messager = new MessageSender();
        DebugHelper.logColorful("AudioNet is running", DebugHelper.printColor.GREEN);
        try (ServerSocket serverSocket = new ServerSocket(46569)) {
            Socket client = serverSocket.accept();
            DebugHelper.logColorful("InterNet is running", DebugHelper.printColor.GREEN);
            while(true) {
                //wait for ICMP
                byte[] bytes = new byte[1024];
                DebugHelper.logColorful("等待外部ICMP", DebugHelper.printColor.CYAN);
                int read = client.getInputStream().read(bytes);
                DebugHelper.logColorful("收到外部ICMP", DebugHelper.printColor.CYAN);
                //notify node1
                String sendMessage = new String(bytes, 0, read, Charset.defaultCharset());
                sendMessage += "ç";
                messager.sendMessage(sendMessage);
                MACLayer.macStateMachine.TxPending = true;
                //wait for node1
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    try {
                        DebugHelper.logColorful("等待Node1中", DebugHelper.printColor.GREEN);
                        GlobalEvent.ALL_DATA_Recieved.wait(1300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
//                client.getOutputStream().write("Pong".getBytes());
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

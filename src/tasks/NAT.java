package tasks;

import OSI.Application.DeviceSettings;
import OSI.Application.MessageSender;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NAT {
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef = 0.16f;
        DeviceSettings.MACAddress = 0;
        AudioHw.audioHwG.isRecording = true;
        MessageSender messager = new MessageSender();
        DebugHelper.logColorful("AudioNet is running", DebugHelper.printColor.GREEN);
        try (ServerSocket serverSocket = new ServerSocket(46569)) {
            Socket client = serverSocket.accept();
            DebugHelper.logColorful("InterNet is running", DebugHelper.printColor.GREEN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

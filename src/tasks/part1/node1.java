package tasks.part1;

import OSI.Application.DeviceSettings;
import OSI.Application.MessageSender;
import OSI.IP.IPv4;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;

import java.util.Scanner;

public class node1 {
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef = 0.1f;
        MessageSender messager = new MessageSender();
        new Thread(() -> {
            while (true) {
                Scanner scanner=new Scanner(System.in);
                DebugHelper.log("输入IP地址");
                String nextLine = scanner.nextLine();
                IPv4 ip = new IPv4(nextLine.substring(nextLine.indexOf("-")+2));
                messager.sendMessage(ip+"ç");
                MACLayer.macStateMachine.TxPending = true;
                DebugHelper.logColorful("收到ICMP来自"+ip, DebugHelper.printColor.BLUE);
            }
        }).start();
    }
}

package tasks.part3.ck1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.IP.IPv4;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.smartConvertor;

import java.util.ArrayList;
import java.util.Scanner;

public class node1 {
    public static void main(String[] args) {
        int sendTimes=0;
        IPv4 ip = new IPv4("192.168.123.139");
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef=0.1f;
        MessageSender messager = new MessageSender();
        ArrayList<Integer> information = new ArrayList<>();
        messager.sendMessage("99999");
        MACLayer.macStateMachine.TxPending = true;
        Scanner scanner=new Scanner(System.in);
        String nextLine = scanner.nextLine();
        var timer = System.currentTimeMillis();
        while(sendTimes<10) {
            for (var ipp : ip.ipsegment) {
                information.addAll(smartConvertor.exactBitsOfNumber(ipp, 8));
            }
            messager.sendBinary(information);
            MACLayer.macStateMachine.TxPending = true;
            information.clear();timer = System.currentTimeMillis();
            var sendTime = System.currentTimeMillis();
            synchronized (GlobalEvent.ALL_DATA_Recieved)
            {
                try {
                        GlobalEvent.ALL_DATA_Recieved.wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            DebugHelper.logColorful(System.currentTimeMillis() - sendTime, DebugHelper.printColor.RED);
            sendTimes++;
            while(System.currentTimeMillis()-timer<1000){};
            DebugHelper.log("发送下一个ICMP");
        }

    }
}

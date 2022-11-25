package tasks.part3.ck1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.IP.IPv4;
import OSI.MAC.MACFrame;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.smartConvertor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import static utils.smartConvertor.mergeBitsToInteger;
import static utils.smartConvertor.receivePayload;

public class node1 {
    public static void main(String[] args) {
        int sendTimes=0;
        String baidu = "182.61.200.7";
        String auto = "10.19.133.250";
        String lshtemp="10.19.73.187";
        IPv4 ip = new IPv4(auto);
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        String frame="macframe";
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
//            var data=MACLayer.macBufferController.upStreamQueue.poll().payload;
//            var rawdata=data.subList(0,32);
//            ArrayList<Integer> IP = new ArrayList<>();
//            for (int j = 0; j < 4; j++) {
//                IP.add(mergeBitsToInteger(rawdata.subList(j * 8, (j + 1) * 8)));
//            }
//            String receiveIP = IP.stream().map(Object::toString).collect(Collectors.joining("."));
//            var payload=data.subList(32,data.size());
//            byte[] bytes = new byte[2048];
//            for (int i = 0; i < data.size() - 8; i += 8) {
//                bytes[i / 8] = (byte) smartConvertor.mergeBitsToInteger(data.subList(i, i + 8));
//            }
//            String s=new String(bytes, 0, bytes.length, Charset.defaultCharset());
//            int endindex;
//            if((endindex=s.lastIndexOf('ç'))!=-1)
//            {
//                s=s.substring(0,endindex);
//            }

            DebugHelper.logColorful(String.format("ip is %s,payload is %s,latency is %d",ip,receivePayload(frame),System.currentTimeMillis() - sendTime), DebugHelper.printColor.RED);

            sendTimes++;
            while(System.currentTimeMillis()-timer<1000){};
            DebugHelper.log("发送下一个ICMP");
        }

    }
}

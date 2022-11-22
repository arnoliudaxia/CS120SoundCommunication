package tasks.part3.ck1;

import OSI.Application.MessageSender;
import OSI.IP.IPv4;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import utils.smartConvertor;

import java.util.ArrayList;

public class node1 {
    public static void main(String[] args) {
        var timer = System.currentTimeMillis();
        int sendTimes=0;
        IPv4 ip = new IPv4("192.168.123.47");
        AudioHw.initAudioHw();
        MACLayer.initMACLayer();
        MessageSender messager = new MessageSender();
        ArrayList<Integer> information = new ArrayList<>();
        while(sendTimes<10) {
            for (var ipp : ip.ipsegment) {
                information.addAll(smartConvertor.exactBitsOfNumber(ipp, 8));
            }
            messager.sendBinary(information);
            sendTimes++;
            information.clear();
            while(System.currentTimeMillis()-timer<1000){
                timer=System.currentTimeMillis();
            }
        }

    }
}

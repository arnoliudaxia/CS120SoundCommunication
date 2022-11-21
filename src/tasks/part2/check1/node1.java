package tasks.part2.check1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.IP.IPv4;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.ReadTxt;
import utils.smartConvertor;

import java.io.IOException;
import java.util.ArrayList;

public class node1 {
    private static void shutdown() {
        System.out.println("系统关闭");
        if (AudioHw.audioHwG != null) {
            AudioHw.audioHwG.stop();
        }
        if (MACLayer.macBufferController != null) {
            MACLayer.macStateMachine.SIG = true;
        }

    }

    public static void main(final String[] args) {
        long programStartTime = System.currentTimeMillis();

        //设置ip
        DeviceSettings.IP = new IPv4("192.168.1.2");
        DebugHelper.log("My IP is " + DeviceSettings.IP.toString());
        DeviceSettings.wakeupRef = 0.1f;
        DeviceSettings.MACAddress = 0;
        //拿取INPUT数据
        ArrayList<Integer> information = new ArrayList<>();
        try {
            byte[] inputdata = ReadTxt.readTxtBytes("res\\INPUT.txt");
            //每一个byte转换为8个bit
            for (byte b : inputdata) {
                information.addAll(smartConvertor.exactBitsOfNumber(b, 8));
            }
            //终止符
            for (byte b : "ç".getBytes()) {
                information.addAll(smartConvertor.exactBitsOfNumber(b, 8));
            }

        } catch (IOException e) {
            DebugHelper.log("读取INPUT失败");
            shutdown();
            throw new RuntimeException(e);
        }
        DebugHelper.log("INPUT数据读取成功");
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        MessageSender messager = new MessageSender();
        messager.sendBinary(information);//数据填充
        while (true) {
            MACLayer.macBufferController.resend();
            if(MACLayer.macBufferController.isAllSent())
            {
                //全部发送成功
                DebugHelper.log("全部发送成功");
                break;
            }
            MACLayer.macStateMachine.TxPending = true;
            synchronized (GlobalEvent.Receive_Frame) {
                try {
                    GlobalEvent.Receive_Frame.wait(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            DebugHelper.log("我收到了对方发的ACK");
            MACLayer.macBufferController.framesSendCount = 0;

        }


    }
}

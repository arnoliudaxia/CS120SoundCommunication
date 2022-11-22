import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.Application.UserSettings;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.csvFileHelper;
import utils.smartConvertor;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static void threadBlockTime(int timems) {
        try {
            Thread.sleep(timems);
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }


    public static void main(final String[] args) throws IOException {

        csvFileHelper csv = new csvFileHelper();

        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();

        long programStartTime = System.currentTimeMillis();

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";

        DeviceSettings.wakeupRef=0.2f;
        DeviceSettings.MACAddress = 0;

        try {
            ArrayList<Integer> information = new ArrayList<>();

            if (2 == 2) {
                DeviceSettings.wakeupRef=0.2f;
                DeviceSettings.MACAddress = 1;

                //我是Node2
                var inputData = smartConvertor.binInFile("res\\part3\\INPUT_5000.bin");
                MessageSender messager = new MessageSender();
                messager.sendBinary(inputData);//数据填充
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    GlobalEvent.ALL_DATA_Recieved.wait();


                }
                DebugHelper.log("收到了对方的第一轮包");
                MACLayer.macBufferController.framesSendCount = 0;

                while (true) {
                    MACLayer.macBufferController.resend();
                    if(MACLayer.macBufferController.downStreamQueue.isEmpty())
                    {
                        DebugHelper.log("切换到4");
//                        taskchoice=4;
                        break;
                    }
                    MACLayer.macStateMachine.TxPending = true;
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                        //threadBlockTime(220);
                    }
                    DebugHelper.log("收到了对方的一轮包");
                    MACLayer.macBufferController.framesSendCount = 0;
                }


            }
            if(3==3) {
                //我只需要发送并且接受ACK
//                UserSettings.Number_Frames_ShouldReceive =1;
                while (true) {
                    MACLayer.macBufferController.resend();
                    if(MACLayer.macBufferController.downStreamQueue.isEmpty())
                    {
                        //现在连我也发完数据了
                        DebugHelper.log("数据发送全部完成");
                        break;
                    }
                    MACLayer.macStateMachine.TxPending = true;
//                    synchronized (GlobalEvent.Receive_Frame) {
//                        GlobalEvent.Receive_Frame.wait(4000);
//                    }
                }
            }
            if(4==4)
            {
                UserSettings.Number_Frames_Trun =1;

                //我只需要听然后发送ACK
                while(true){
                    MACLayer.macStateMachine.TxPending = true;
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait(3000);
                    }

                }
            }


//
//            for (int i = 0; i < rFrames.size(); i++) {
//                if (rFrames.get(i).seq != i+1) {
//                    DebugHelper.log("在" + (int)(i+1) + "处断开");
//                    rFrames.subList(i, rFrames.size()).clear();
//                    break;
//                }
//            }
//            rFrames.forEach(x -> information.addAll(x.payload));
//            information.subList(UserSettings.Number_bits, information.size()).clear();
            smartConvertor.binToFile("res\\OUTPUT.bin", information);
//            try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
//                for (var bit : information) {
//                    input.write(bit.toString().getBytes());
//                }
//            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private static void shutdown() {
        System.out.println("系统关闭");
        if (AudioHw.audioHwG != null) {
            AudioHw.audioHwG.stop();
        }
        if (MACLayer.macBufferController != null) {
            MACLayer.macStateMachine.SIG = true;
        }

    }
}



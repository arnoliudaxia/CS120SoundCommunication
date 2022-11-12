import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.Application.UserSettings;
import OSI.MAC.MACFrame;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import com.github.psambit9791.wavfile.WavFileException;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.csvFileHelper;
import utils.smartConvertor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    static void threadBlockTime(int timems) {
        try {
            Thread.sleep(timems);
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }


    public static void main(final String[] args) throws IOException, WavFileException {

        csvFileHelper csv = new csvFileHelper();

        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();

        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
        var command=scanner.nextLine();
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
//        int taskchoice = 1;
        //#endregion
        long programStartTime = System.currentTimeMillis();

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";
        try {
            ArrayList<Integer> information = new ArrayList<>();
            if(command.contains("macperf")){
                while (true) {
                    //一直发就可以
                    DeviceSettings.MACAddress = 0;
                    DeviceSettings.wakeupRef=0.2f;
//                    MACLayer.macBufferController.resend();
                    MACLayer.macBufferController.dropCount=0;
                    MACLayer.macStateMachine.TxPending = true;
                    synchronized (GlobalEvent.Receive_Frame) {
                        GlobalEvent.Receive_Frame.wait(4000);
                    }
                }
            }
            if(command.contains("macping")) {
                DeviceSettings.wakeupRef=0.2f;
                DeviceSettings.MACAddress = 1;
                MACLayer.macBufferController.dropCount=50;
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    GlobalEvent.ALL_DATA_Recieved.wait();
                }
                DebugHelper.log("收到了对方的第一轮包");
                MACLayer.macBufferController.framesSendCount = 0;
                UserSettings.Number_Frames_Trun =1;


                while(true){
                    MACLayer.macBufferController.dropCount=0;
                    MACLayer.macStateMachine.TxPending = true;
                    var lastTime=System.currentTimeMillis();
                    synchronized (GlobalEvent.Receive_Frame) {
                        GlobalEvent.Receive_Frame.wait();
                    }
                    DebugHelper.log("RTT:+ "+(System.currentTimeMillis()-lastTime));
                    lastTime=System.currentTimeMillis();
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait(3000);
                    }
                }
            }

            if(command.contains("macp213erf")){
                int taskchoice = -1;
                if(command.contains("1")) {
                    taskchoice = 0;
                }else if(command.contains("1")) {
                    taskchoice = 1;
                }
                if (taskchoice == 1) {
                    UserSettings.Number_bits=40000;
                    DeviceSettings.wakeupRef=0.2f;
                    DeviceSettings.MACAddress = 0;
                    //Node 1
                    var inputData = smartConvertor.binInFile("res\\INPUT_6250.bin");
                    MessageSender messager = new MessageSender();
                    messager.sendBinary(inputData);//数据填充
                    //我先发
                    while (true) {
                        MACLayer.macBufferController.resend();
                        MACLayer.macBufferController.dropCount=0;
                        MACLayer.macStateMachine.TxPending = true;
                        synchronized (GlobalEvent.ALL_DATA_Recieved) {
                            GlobalEvent.ALL_DATA_Recieved.wait(2000);
                        }
                        if(MACLayer.macBufferController.upStreamQueue.size()>=236)
                        {
                            DebugHelper.log("切换到3");
                            taskchoice=3;
                            break;
                        }
                        DebugHelper.log("我收到了对方发的一轮包");
                        MACLayer.macBufferController.framesSendCount = 0;
//                    break;
                    }


                }

                if(taskchoice==4)
                {

                    //我只需要听然后发送ACK

                }
                ArrayList<MACFrame> rFrames = new ArrayList<>();
                synchronized (MACLayer.macBufferController.upStreamQueue) {
                    while (!MACLayer.macBufferController.upStreamQueue.isEmpty()) {
                        var frame = MACLayer.macBufferController.upStreamQueue.poll();
                        rFrames.add(frame);
                    }
                }
                rFrames.sort(Comparator.comparingInt(o -> o.seq));
                for (int i = 0; i < rFrames.size(); i++) {
                    if (rFrames.get(i).seq != i+1) {
                        DebugHelper.log("在" + (int)(i+1) + "处断开");
                        rFrames.subList(i, rFrames.size()).clear();
                        break;
                    }
                }
                rFrames.forEach(x -> information.addAll(x.payload));
                information.subList(UserSettings.Number_bits, information.size()).clear();
                smartConvertor.binToFile("res\\OUTPUT.bin", information);
            }



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



import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.MAC.MACFrame;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import com.github.psambit9791.wavfile.WavFileException;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.csvFileHelper;
import utils.smartConvertor;

import java.io.FileOutputStream;
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
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
        int taskchoice = 2;
        //#endregion

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";
        var startTime = System.currentTimeMillis();
        try {
            ArrayList<Integer> information = new ArrayList<>();

            if (taskchoice == 1) {
                DeviceSettings.MACAddress = 0;
                //Node 1
                var inputData = smartConvertor.binInTextFile("res\\INPUT.txt");
                MessageSender messager = new MessageSender();
                messager.sendBinary(inputData);//数据填充
                //我先发
                while (true) {
                    MACLayer.macBufferController.resend();
                    MACLayer.macBufferController.dropCount=0;
                    MACLayer.macStateMachine.TxPending = true;
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    }
                    if(MACLayer.macBufferController.upStreamQueue.size()>=52)
                    {
                        DebugHelper.log("切换到3");
                        taskchoice=3;
                        break;
                    }
                    DebugHelper.log("我收到了对方发的一轮包");
                    MACLayer.macBufferController.framesSendCount = 0;
                }


            }
            if (taskchoice == 2) {
                DeviceSettings.MACAddress = 1;

                //我是Node2
                var inputData = smartConvertor.binInTextFile("res\\INPUT2.txt");
                MessageSender messager = new MessageSender();
                messager.sendBinary(inputData);//数据填充
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    GlobalEvent.ALL_DATA_Recieved.wait();
                }
                DebugHelper.log("收到了对方的第一轮包");
                MACLayer.macBufferController.framesSendCount = 0;

                while (true) {
                    MACLayer.macBufferController.resend();
                    if(MACLayer.macBufferController.downStreamQueue.size()<=1)
                    {
                        DebugHelper.log("切换到4");
                        taskchoice=4;
                        break;
                    }
                    MACLayer.macBufferController.dropCount=0;
                    MACLayer.macStateMachine.TxPending = true;
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    }
                    DebugHelper.log("收到了对方的一轮包");
                    MACLayer.macBufferController.framesSendCount = 0;
                }


            }
            if(taskchoice==3) {
                //我只需要发送并且接受ACK
                while (true) {
                    MACLayer.macBufferController.resend();
                    if(MACLayer.macBufferController.downStreamQueue.isEmpty())
                    {
                        //现在连我也发完数据了
                        DebugHelper.log("数据发送全部完成");
                        break;
                    }
                    MACLayer.macStateMachine.TxPending = true;
                    synchronized (GlobalEvent.Receive_Frame) {
                        GlobalEvent.Receive_Frame.wait();
                    }
                }
            }
            if(taskchoice==4)
            {
                //我只需要听然后发送ACK
                while(true){
                    MACLayer.macStateMachine.TxPending = true;
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait(4000);
                    }
                    if(MACLayer.macBufferController.upStreamQueue.size()>=103)
                    {
                        DebugHelper.log("数据接收全部完成");
                        break;
                    }

                }
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
            try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
                for (var bit : information) {
                    input.write(bit.toString().getBytes());
                }
            }
//                csv.saveToCsv(lyfHPURL+"wave.csv",((FrameDetector)AudioHw.audioHwG.dataagent).wave);


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



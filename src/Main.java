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
        int taskchoice = 1;
        //#endregion

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";

        try {
            ArrayList<Integer> information=new ArrayList<>();

            if(taskchoice==1)
            {
                //Node 1
                var inputData = smartConvertor.binInTextFile("res\\INPUT.txt");
                MessageSender messager = new MessageSender();
                messager.sendBinary(inputData);//数据填充
                //我先发
                MACLayer.macStateMachine.TxPending=true;
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    GlobalEvent.ALL_DATA_Recieved.wait();
                }
                //我收到了我自己发的一轮包，而且收到了对方发的一轮包
                DebugHelper.log("我收到了我自己发的一轮包，而且收到了对方发的一轮包");

            }
            //交替机制：node1先发20个data frame，然后node1再发1个ACK frame和20个frame，接下来都和前面一样
            if(taskchoice==2){
                //我是Node2
                int framesCount=0;
                //在% 20为奇数的时候我发
                var inputData = smartConvertor.binInTextFile("res\\INPUT2.txt");
                MessageSender messager = new MessageSender();
                messager.sendBinary(inputData);//数据填充
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    GlobalEvent.ALL_DATA_Recieved.wait();
                }
                while (true) {
                    MACLayer.macStateMachine.TxPending=true;
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    }
                    //我收到了我自己发的一轮包，而且收到了对方发的一轮包
                    DebugHelper.log("我收到了我自己发的一轮包，而且收到了对方发的一轮包");


                }


            }

            if(taskchoice==7)
            {
                //纯收听，测试用
                threadBlockTime(15000);
                ArrayList<MACFrame> rFrames=new ArrayList<>();
                synchronized (MACLayer.macBufferController.upStreamQueue)
                {
                    while(!MACLayer.macBufferController.upStreamQueue.isEmpty()){
                        var frame=MACLayer.macBufferController.upStreamQueue.poll();
                        rFrames.add(frame);
                    }
                }
                rFrames.sort(Comparator.comparingInt(o -> o.seq));
                rFrames.forEach(x->information.addAll(x.payload));
                try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
                    for (var bit : information) {
                        input.write(bit.toString().getBytes());
                    }
                }
            }


            if (taskchoice == 8) {
                //var inputData=smartConvertor.binInFile("res\\INPUT.bin");
                //在发送完一小段数据后，检查收到的（自己的）frame是不是正确的，然后再发下一段
                //首先把数据分成frames,
                var inputData = smartConvertor.binInTextFile("res\\INPUT.txt");
                MessageSender messager = new MessageSender();
                messager.sendBinary(inputData);
                MACLayer.macStateMachine.TxPending=true;
                threadBlockTime(10000);

//                threadBlockTime((int) (UserSettings.LoopBackDelay*1000*(1+Math.random())));
//                    synchronized (GlobalEvent.ALL_DATA_Recieved)
//                    {
//                        GlobalEvent.ALL_DATA_Recieved.wait((int) (UserSettings.LoopBackDelay*1000*(1+Math.random())));
//                    }
                }
//                while(MACLayer.macBufferController.hasDataLeft())
//                {
//                    MACLayer.macBufferController.__send();
//                    threadBlockTime((int) (UserSettings.LoopBackDelay*1000*(.3+Math.random())));
//                }
//
//                ArrayList<MACFrame> rFrames=new ArrayList<>();
//                synchronized (MACLayer.macBufferController.upStreamQueue)
//                {
//                    while(!MACLayer.macBufferController.upStreamQueue.isEmpty()){
//                        var frame=MACLayer.macBufferController.upStreamQueue.poll();
//                        rFrames.add(frame);
//                    }
//                }
//                rFrames.sort(Comparator.comparingInt(o -> o.seq));
//                rFrames.forEach(x->information.addAll(x.payload));
//
//                try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
//                    for (var bit : information) {
//                        input.write(bit.toString().getBytes());
//                    }
//                }
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



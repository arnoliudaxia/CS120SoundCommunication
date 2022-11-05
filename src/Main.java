import OSI.Application.MessageSender;
import OSI.Application.UserSettings;
import OSI.Link.FrameDetector;
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
import java.util.LinkedList;
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
        int taskchoice = 8;
        //#endregion

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";

        try {
            ArrayList<Integer> information=new ArrayList<>();

            if(taskchoice==2){

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
                //Node 1
                //var inputData=smartConvertor.binInFile("res\\INPUT.bin");
                var inputData = smartConvertor.binInTextFile("res\\INPUT.txt");
                //在发送完一小段数据后，检查收到的（自己的）frame是不是正确的，然后再发下一段
                //首先把数据分成frames,
                LinkedList<ArrayList<Integer>> frames=new LinkedList<>();
                for (int i = 0; i < inputData.size(); i += 84) {
                    frames.add(new ArrayList<>(inputData.subList(i,i+84>= inputData.size() ? inputData.size() - 1 :i+84)));
                }
                frames.remove(frames.size()-1); //TODO 先去掉最后一个不完整的，待会在处理
                MessageSender messager = new MessageSender();
                for (int i = 0; i < frames.size(); i += UserSettings.Number_Frames_True) {
                    //取出4个frame然后发送
                    ArrayList<Integer> sendData=new ArrayList<>();
                    for (int j = 0; j < UserSettings.Number_Frames_True; j++) {
                        if(i+j>= frames.size())break;
                        sendData.addAll(frames.get(i+j));
                    }

                    DebugHelper.log("发送数据包"+i+"~"+(i+4));
                    messager.sendBinary(sendData);

                    threadBlockTime((int) (UserSettings.LoopBackDelay*1000*(1+Math.random())));
//                    synchronized (GlobalEvent.ALL_DATA_Recieved)
//                    {
//                        GlobalEvent.ALL_DATA_Recieved.wait((int) (UserSettings.LoopBackDelay*1000*(1+Math.random())));
//                    }
                }
                while(MACLayer.macBufferController.hasDataLeft())
                {
                    MACLayer.macBufferController.__send();
                    threadBlockTime((int) (UserSettings.LoopBackDelay*1000*(.3+Math.random())));
                }
//                threadBlockTime(5000);

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
                for (int i = 0; i < information.size(); i++) {
                    if(information.get(i)!=inputData.get(i))
                    {
                        DebugHelper.log("位"+i+"错误");
                        break;
                    }
                }

                try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
                    for (var bit : information) {
                        input.write(bit.toString().getBytes());
                    }
                }
                csv.saveToCsv(lyfHPURL+"wave.csv",((FrameDetector)AudioHw.audioHwG.dataagent).wave);

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



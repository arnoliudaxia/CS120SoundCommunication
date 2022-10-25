import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.Link.FrameDetector;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import OSI.Physic.PlayOverCallback;
import com.github.psambit9791.wavfile.WavFileException;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.csvFileHelper;
import utils.smartConvertor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        MACLayer.initMACLayer();


//        threadBlockTime(20000);
        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
        int taskchoice = 7;
        //#endregion

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";

        try {
            if (taskchoice == 4) {
                new MessageSender();
                MessageSender.messageSender.sendBinary(smartConvertor.binInTextFile("res\\INPUT.txt"));
//            csv.saveToCsv(lyfdellURL+"send.csv",bitPacker.onepackage);
                threadBlockTime(3000);
                AudioHw.audioHwG.isPlay = false;
            }
            AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
            AudioHw.audioHwG.isRecording = true;

            if (taskchoice == 6) {
                //实现MAC层的协议
//                var s=new FrameDetector();
//                AudioHw.audioHwG.dataagent = s;
//            new Thread(()->{
//                Float[] debugWave = new Float[0];
//                try {
//                    debugWave = csv.readCsv(lyfHPURL + "wave.csv");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                for (int i = 0; i < debugWave.length - 512; i += 512) {
//                    float[] debugFragment = new float[512];
//                    for (int j = 0; j < 512; j++) {
//                        debugFragment[j] = debugWave[i + j];
//                    }
//                    s.storeData(debugFragment);
//                }
//            }).start();

//                threadBlockTime(10000);
                DebugHelper.log("接收信号中....");
                synchronized (GlobalEvent.CONNECTED)
                {
                    try {
                        GlobalEvent.CONNECTED.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DebugHelper.log("有信号接入!");
                //需要马上发一个frame回复一下
                new MessageSender();
                MessageSender.messageSender.sendMessage("213");
                //等接收完成的信号
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    try {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DebugHelper.log("全部接收完成");
                AudioHw.audioHwG.isRecording = false;


                List<Integer> information = new ArrayList<>();
                while (MACLayer.macBufferController.upStreamQueue.size() > 0) {
                    information.addAll(MACLayer.macBufferController.upStreamQueue.poll());
                }
                information.subList(50000, information.size()).clear();
                try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
                    for (var bit : information) {
                        input.write(bit.toString().getBytes());
                    }
                }

            }

            if(taskchoice==7){
                //发送完要求有一个回复
                new MessageSender();
                class waitForReply implements PlayOverCallback{
                    @Override
                    public void playOverCallback() {
                        AudioHw.audioHwG.isRecording = false;
                        if(MACLayer.macBufferController.upStreamQueue.size()==0){
                            DebugHelper.log("link error");
                        }
                        else{
                            DebugHelper.log("发送成功");
                        }
                    }
                }
                AudioHw.audioHwG.playOverCallback=new waitForReply();
                MessageSender.messageSender.sendBinary(smartConvertor.binInTextFile("res\\INPUT.txt"));
                threadBlockTime(12000);
            }
            if(taskchoice==8){
                //接收完发一个回复
                var s=new FrameDetector();
                AudioHw.audioHwG.dataagent = s;
                AudioHw.audioHwG.isRecording = true;

                AudioHw.audioHwG.isRecording = false;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            shutdown();
        }
    }
    private static void shutdown() {
        System.out.println("系统关闭");
        if(AudioHw.audioHwG!=null) {
            AudioHw.audioHwG.stop();
        }
        if(MACLayer.macBufferController!=null) {
            MACLayer.macStateMachine.SIG=true;
        }

    }
}



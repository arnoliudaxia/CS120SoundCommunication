import OSI.Application.MessageSender;
import OSI.Link.FrameDetector;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import com.github.psambit9791.wavfile.WavFileException;
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
        int taskchoice = 4;
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

            if (taskchoice == 6) {
                //实现MAC层的协议
                var s=new FrameDetector();
                AudioHw.audioHwG.dataagent = new FrameDetector();
                AudioHw.audioHwG.isRecording = true;

//            Float[] debugWave = csv.readCsv(lyfHPURL + "wave.csv");
//            for (int i = 0; i < debugWave.length - 512; i += 512) {
//                float[] debugFragment = new float[512];
//                for (int j = 0; j < 512; j++) {
//                    debugFragment[j] = debugWave[i + j];
//                }
//                s.storeData(debugFragment);
//            }
                threadBlockTime(10000);
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



import OSI.Application.MessageSender;
import OSI.Link.FrameDetector;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import com.github.psambit9791.wavfile.WavFileException;
import utils.csvFileHelper;

import java.io.File;
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

//        AudioHw.initAudioHw();
        MACLayer.initMACLayer();


//        threadBlockTime(20000);
        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
        int taskchoice = 6;
        //#endregion

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";

        if (taskchoice == 4) {
            //一台机子发送数据另一台接受
            //首先采取调频的方式传送
            ArrayList<Integer> rawdata = new ArrayList<>();
            File f = new File("res\\INPUT.txt");
            Scanner sc = new Scanner(f);
            var rawstring = sc.nextLine();
            for (int i = 0; i < rawstring.length(); i++) {
                {
                    rawdata.add(Integer.parseInt(String.valueOf(rawstring.charAt(i))));
                }
            }
            new MessageSender();
            MessageSender.messageSender.sendBinary(rawdata);
//            csv.saveToCsv(lyfdellURL+"send.csv",bitPacker.onepackage);
            threadBlockTime(6000);
            AudioHw.audioHwG.isPlay = false;
        }

        if (taskchoice == 6) {
            //实现MAC层的协议
            var s = new FrameDetector();
//            AudioHw.audioHwG.dataagent=s ;

            Float[] debugWave = csv.readCsv(lyfHPURL + "wave.csv");
            for (int i = 0; i < debugWave.length - 512; i += 512) {
                float[] debugFragment = new float[512];
                for (int j = 0; j < 512; j++) {
                    debugFragment[j] = debugWave[i + j];
                }
                s.storeData(debugFragment);
            }
            threadBlockTime(2000);
            List<Integer> information = new ArrayList<>();
            while(MACLayer.macBufferController.upStreamQueue.size()>0){
                information.addAll(MACLayer.macBufferController.upStreamQueue.poll());
            }
            information.subList(50000, information.size()).clear();
            try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
                for (var bit : information) {
                    input.write(bit.toString().getBytes());
                }
            }

        }
        shutdown();
    }
    private static void shutdown() {
        if(AudioHw.audioHwG!=null) {
            AudioHw.audioHwG.stop();
        }
        if(MACLayer.macBufferController!=null) {
            MACLayer.macStateMachine.SIG=true;
        }

    }
}



import OSI.Link.BitPacker;
import OSI.Link.StoreData;
import OSI.Physic.AudioHw;
import com.github.psambit9791.wavfile.WavFileException;
import dataAgent.StorgePolicy;
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


        AudioHw.audioHwG=new AudioHw();

        AudioHw.audioHwG.init(Config.PHY_TX_SAMPLING_RATE);
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FILE);
        AudioHw.audioHwG.start();
//        threadBlockTime(20000);
        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
        int taskchoice = 4;

        //#endregion

        String lyfdellURL="C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL="C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL="D:\\桌面\\project1_sample\\";

        if (taskchoice == 4) {
            //一台机子发送数据另一台接受
            //首先采取调频的方式传送
            System.out.println("发送数据任务");
            BitPacker bitPacker = new BitPacker(Config.PHY_TX_SAMPLING_RATE);
            List<Integer> rawdata = new ArrayList<>();
            File f = new File("res\\INPUT.txt");
            Scanner sc = new Scanner(f);
            var rawstring = sc.nextLine();
            for (int i = 0; i < rawstring.length(); i++) {
                {
                    rawdata.add(Integer.parseInt(String.valueOf(rawstring.charAt(i))));
                }
            }
            bitPacker.AppendData(rawdata);
            bitPacker.padding();
            csv.saveToCsv(lyfdellURL+"send.csv",bitPacker.onepackage);
            threadBlockTime(3000);
            AudioHw.audioHwG.isPlay = false;
        }
        if (taskchoice==5){
            Float[] debugWave = csv.readCsv(lyfHPURL+"wave.csv");
            var s=new StoreData(Config.PHY_TX_SAMPLING_RATE);

//            for (int i = 0; i < debugWave.length - 512; i += 512) {
//                float[] debugFragment = new float[512];
//                for (int j = 0; j < 512; j++) {
//                    debugFragment[j] = debugWave[i + j];
//                }
//                s.storeData(debugFragment);
//            }

            AudioHw.audioHwG.dataagent=s ;
            final int recordTime = 8;
            AudioHw.audioHwG.isRecording = true;
            threadBlockTime(recordTime * 1000);
            AudioHw.audioHwG.isRecording = false;
            csv.saveToCsv(lyfHPURL+"wave.csv",s.alldata);

            s.processAllData(50000);

            try (FileOutputStream input = new FileOutputStream("res\\OUTPUT.txt")) {
                for(var bit:s.information)
                {
                    input.write(bit.toString().getBytes());
                }
            }


        }
        AudioHw.audioHwG.stop();


    }
}

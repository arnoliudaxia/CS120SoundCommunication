import OSI.Link.BitPacker;
import OSI.Physic.AudioHw;
import com.github.psambit9791.jdsp.signal.Generate;
import com.github.psambit9791.wavfile.WavFileException;
import dataAgent.SoundUtil;
import dataAgent.StorgePolicy;

import java.io.File;
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


        AudioHw.audioHwG=new AudioHw();

        AudioHw.audioHwG.init(Config.PHY_TX_SAMPLING_RATE);
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FILE);
        AudioHw.audioHwG.start();
        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
        int taskchoice = 4;

        //#endregion
        if (taskchoice == 1) {
            //record 10s，然后回放
            final int recordTime = 10;
            System.out.println("Recording 10s...");
            AudioHw.audioHwG.isRecording = true;
            threadBlockTime(recordTime * 1000);
            AudioHw.audioHwG.isRecording = false;
            System.out.println("播放录音");
            AudioHw.audioHwG.playSound(AudioHw.audioHwG.dataagent.retriveData(Config.HW_BUFFER_SIZE));
            AudioHw.audioHwG.isPlay = true;
            threadBlockTime(recordTime * 1000);
            AudioHw.audioHwG.isPlay = false;

        }
        if (taskchoice == 2) {
            //同时放声音和录音10s，然后回放
            final int recordTime = 10;
            AudioHw.audioHwG.playSound(SoundUtil.playsoundFile("res\\cai.dat", Config.HW_BUFFER_SIZE));
            AudioHw.audioHwG.isPlay = true;
            System.out.println("放音乐中...");
            System.out.println("Recording 10s...");
            AudioHw.audioHwG.isRecording = true;
            threadBlockTime(recordTime * 1000);
            AudioHw.audioHwG.isPlay = false;
            AudioHw.audioHwG.isRecording = false;
            System.out.println("播放录音");
            AudioHw.audioHwG.playSound(AudioHw.audioHwG.dataagent.retriveData(Config.HW_BUFFER_SIZE));
            AudioHw.audioHwG.isPlay = true;
            threadBlockTime(recordTime * 1000);
            AudioHw.audioHwG.isPlay = false;


        }
        if (taskchoice == 3) {
            //播放两个正弦波1000hz and 10000hz

            var sin1K = SoundUtil.generateSinwave(1000, 2.1f, Config.PHY_TX_SAMPLING_RATE);
//            var sin10K=SoundUtil.generateSinwave(10000,4,Config.PHY_TX_SAMPLING_RATE);
            Generate gp = new Generate(0, 4, 4 * Config.PHY_TX_SAMPLING_RATE);
            var sin10K = gp.generateSineWave(10000);
            for (var i = 0; i < sin1K.length; i++) {
                sin1K[i] += (float) sin10K[i];
            }

            AudioHw.audioHwG.playRawData(sin1K);

            AudioHw.audioHwG.isPlay = true;
            threadBlockTime(2000);
            AudioHw.audioHwG.isPlay = false;
        }
        if (taskchoice == 4) {
            //一台机子发送数据另一台接受
            //首先采取调频的方式传送
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
            threadBlockTime(5000);
            AudioHw.audioHwG.isPlay = false;


        }
        AudioHw.audioHwG.stop();


    }
}

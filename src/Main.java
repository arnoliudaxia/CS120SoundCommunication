import com.github.psambit9791.jdsp.signal.Generate;
import com.github.psambit9791.wavfile.WavFile;
import com.github.psambit9791.wavfile.WavFileException;
import dataAgent.SoundUtil;
import dataAgent.StorgePolicy;
import utils.smartConvertor;

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


//        double[] signal={2, 8, 0, 4, 1, 9, 9, 0};
//        double[] kernel={8,0,4,1};
//        String mode = "valid"; //Can be "valid", "same"
//        CrossCorrelation cc = new CrossCorrelation(signal, kernel);
//        double[] outarray = cc.crossCorrelate(mode);
//        System.out.println(outarray);
//        System.out.println(argmax(outarray,false));

        final AudioHw audiohw = new AudioHw();


        audiohw.init();
        audiohw.changeStorgePolicy(StorgePolicy.FILE);
        audiohw.start();
        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
        int taskchoice = 4;

        //#endregion
        if (taskchoice == 1) {
            //record 10s，然后回放
            final int recordTime = 10;
            System.out.println("Recording 10s...");
            audiohw.isRecording = true;
            threadBlockTime(recordTime * 1000);
            audiohw.isRecording = false;
            System.out.println("播放录音");
            audiohw.playQueue = audiohw.dataagent.retriveData(Config.HW_BUFFER_SIZE);
            audiohw.isPlay = true;
            threadBlockTime(recordTime * 1000);
            audiohw.isPlay = false;

        }
        if (taskchoice == 2) {
            //同时放声音和录音10s，然后回放
            final int recordTime = 10;
            audiohw.playSound(SoundUtil.playsoundFile("res\\cai.dat", Config.HW_BUFFER_SIZE));
            audiohw.isPlay = true;
            System.out.println("放音乐中...");
            System.out.println("Recording 10s...");
            audiohw.isRecording = true;
            threadBlockTime(recordTime * 1000);
            audiohw.isPlay = false;
            audiohw.isRecording = false;
            System.out.println("播放录音");
            audiohw.playSound(audiohw.dataagent.retriveData(Config.HW_BUFFER_SIZE));
            audiohw.isPlay = true;
            threadBlockTime(recordTime * 1000);
            audiohw.isPlay = false;


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

            audiohw.playRawData(sin1K);

            audiohw.isPlay = true;
            threadBlockTime(2000);
            audiohw.isPlay = false;
        }
        if (taskchoice == 4) {
            //一台机子发送数据另一台接受
            //首先采取调频的方式传送
            final int bitLength=100;
            float fragmentTime = 0.05f;
            int fragmentLength = (int) (fragmentTime * Config.PHY_TX_SAMPLING_RATE);
            var oneSignal = (SoundUtil.generateSinwave(12000, fragmentTime, Config.PHY_TX_SAMPLING_RATE));
            var zeroSignal = SoundUtil.generateSinwave(8000, fragmentTime, Config.PHY_TX_SAMPLING_RATE);
            final int headerLength = 15;
            final List<Integer> headerFrame = List.of(1,0,1,1,0,1,0,1,1,0,1,1,0,1,0);
            float[] signal = new float[(headerLength+bitLength) * fragmentLength];
            List<Integer> rawdata = new ArrayList<>();
            rawdata.addAll(headerFrame);
            File f = new File("res\\INPUT.txt");
            Scanner sc = new Scanner(f);
            var rawstring = sc.nextLine();
            for (int i = 0; i < rawstring.length(); i++) {
                {
                    rawdata.add(Integer.parseInt(String.valueOf(rawstring.charAt(i))));
                }
            }
            for (int i = 0; i < rawdata.size(); i++) {
                System.arraycopy(rawdata.get(i) == 1 ? oneSignal : zeroSignal, 0, signal, i * fragmentLength, fragmentLength);
            }
            WavFile objRead1 = WavFile.newWavFile(new File("res\\my.wav"),1,1000,16,Config.PHY_TX_SAMPLING_RATE);
            objRead1.writeFrames(smartConvertor.floatToDoubleArray(signal),1000);
            audiohw.playRawData(signal);
            audiohw.isPlay = true;
            threadBlockTime(2000);
            audiohw.isPlay = false;


        }
        audiohw.stop();


    }
}

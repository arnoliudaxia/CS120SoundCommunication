
import dataAgent.SoundUtil;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static void threadBlockTime(int timems) {
        try {
            Thread.sleep(timems);
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

    public static void main(final String[] args) throws IOException {

        final AudioHw audiohw = new AudioHw();


        audiohw.init();
//        audiohw.changeStorgePolicy(StorgePolicy.FILE);
        audiohw.start();
        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
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
            //生成一个频率为1000Hz的正弦波，采样率是44100Hz


//            audiohw.playRawData(SoundUtil.generateSinwave(1000,2,Config.PHY_TX_SAMPLING_RATE));
            float phase = 0;
            float freq = 5000f;  // Hz
            float sampleRate = 48000f;
            float dphase = (2 * (float) Math.PI * freq) / sampleRate;
            float[] soundTemp = new float[Config.HW_BUFFER_SIZE];
            for (int i = 0; i < Config.HW_BUFFER_SIZE; i++) {
                phase = phase + dphase;
                soundTemp[i] = (float) (Math.sin((double) phase));  // sine wave
            }
            audiohw.playRawData(soundTemp);

            audiohw.isPlay = true;
            audiohw.isLoop = true;
            threadBlockTime(1000);
            audiohw.isPlay = false;
        }
        audiohw.stop();


    }
}

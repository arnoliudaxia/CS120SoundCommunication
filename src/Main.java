
import dataAgent.SoundUtil;
import dataAgent.StorgePolicy;

import java.io.*;
import java.util.Scanner;

public class Main {
    static void threadBlockTime(int timems)
    {
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
            final int recordTime=10;
            System.out.println("Recording 10s...");
            audiohw.isRecording = true;
            threadBlockTime(recordTime*1000);
            audiohw.isRecording = false;
            System.out.println("播放录音");
            audiohw.playQueue=audiohw.dataagent.retriveData(Config.HW_BUFFER_SIZE);
            audiohw.isPlay = true;
            threadBlockTime(recordTime*1000);
            audiohw.isPlay = false;

        }
        if(taskchoice==2)
        {
            //同时放声音和录音10s，然后回放
            //TODO 放歌
            final int recordTime=10;
            audiohw.playQueue=SoundUtil.playsoundFile("res\\cai.dat",Config.HW_BUFFER_SIZE);
            audiohw.isPlay=true;
            System.out.println("放音乐中...");
            System.out.println("Recording 10s...");
            audiohw.isRecording = true;
            threadBlockTime(recordTime*1000);
            audiohw.isPlay= false;
            audiohw.isRecording = false;
            System.out.println("播放录音");
            audiohw.playQueue=audiohw.dataagent.retriveData(Config.HW_BUFFER_SIZE);
            audiohw.isPlay = true;
            threadBlockTime(recordTime*1000);
            audiohw.isPlay = false;


        }
        audiohw.stop();


    }
}

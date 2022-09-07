import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

public class Main {
    static void threadBlockTime(int timems)
    {
        try {
            Thread.sleep(timems);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(final String[] args) {

        final AudioHw audiohw = new AudioHw();
        audiohw.init();
        ByteArrayOutputStream tempBufferInMemory = new ByteArrayOutputStream();
        audiohw.storeStream = tempBufferInMemory;
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
            System.out.println("播放录音");
            audiohw.isRecording = false;
            byte[] tempBuffer = tempBufferInMemory.toByteArray();

            audiohw.loadStream = new ByteArrayInputStream(tempBuffer);
            audiohw.isPlay = true;
            threadBlockTime(recordTime*1000);
            audiohw.isPlay = false;

        }
        if(taskchoice==2)
        {
            //同时放声音和录音10s，然后回放
            //TODO 放歌
            final int recordTime=10;
            System.out.println("Recording 10s...");
            audiohw.isRecording = true;
            threadBlockTime(recordTime*1000);
            System.out.println("播放录音");
            audiohw.isRecording = false;
            byte[] tempBuffer = tempBufferInMemory.toByteArray();
            audiohw.loadStream = new ByteArrayInputStream(tempBuffer);
            audiohw.isPlay = true;
            threadBlockTime(recordTime*1000);
            audiohw.isPlay = false;


        }
        audiohw.stop();


    }
}

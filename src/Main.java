import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import com.github.psambit9791.wavfile.WavFileException;
import dataAgent.StorgePolicy;
import utils.csvFileHelper;
import utils.smartConvertor;

import java.io.IOException;
import java.util.ArrayList;
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

        //#region 选择Task
        Scanner scanner = new Scanner(System.in); // 创建Scanner对象
//        int taskchoice = scanner.nextInt(); // 读取一行输入并获取字符串
        int taskchoice = 8;
        //#endregion

        String lyfdellURL = "C:\\Users\\Arno\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
        String lshURL = "D:\\桌面\\project1_sample\\";

        try {
            AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
            AudioHw.audioHwG.isRecording = true;

            if (taskchoice == 8) {
                //在发送完一小段数据后，检查收到的（自己的）frame是不是正确的，然后再发下一段
                //一小段数据指284*4 bits，也即4个frame
                int fragSize=284*4;
                MessageSender messager = new MessageSender();
//                var inputData=smartConvertor.binInFile("res\\INPUT.bin");
                var inputData = smartConvertor.binInTextFile("res\\INPUT.txt");
                for (int i = 0; i < inputData.size(); i += fragSize) {
                    var fragment = new ArrayList<>(inputData.subList(i, i+fragSize >= inputData.size() ? inputData.size() - 1 : i+fragSize));
                    messager.sendBinary(fragment);
                    synchronized (GlobalEvent.ALL_DATA_Recieved) {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    }
                    synchronized (MACLayer.macBufferController.upStreamQueue)
                    {
                        while(!MACLayer.macBufferController.upStreamQueue.isEmpty()){
                            var frame=MACLayer.macBufferController.upStreamQueue.poll();
                        }
                    }
                    threadBlockTime(100);
                }

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



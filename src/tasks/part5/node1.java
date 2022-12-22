package tasks.part5;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.IP.IPv4;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.smartConvertor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static utils.Lcs.LcsLength;
import static utils.Lcs.s;

public class node1 {
    private static String[] commands={"USER", "PASS", "PWD", "CWD", "PASV", "LIST", "RETR"};
    private static String ss="";
    private static boolean isWriteFile=false;
    private static String fileName="";
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef = 0.06f;
        MessageSender messager = new MessageSender();
        new Thread(() -> {
            while (true) {
                Scanner scanner=new Scanner(System.in);
                DebugHelper.log("输入命令");
                String nextLine = scanner.nextLine();
                String cc=nextLine.substring(0,nextLine.indexOf(" "));
                String content=nextLine.substring(nextLine.indexOf(" ")+1);
                String[] subsequence = {};
                for (int i = 0; i < commands.length; i++) {
                    LcsLength(commands[i].toCharArray(), cc.toCharArray());
                    subsequence = smartConvertor.insert(subsequence, s);
                }
                int len=0;
                int cnt=0;
                ArrayList<Integer> Index=new ArrayList<>();
                for(int j=0;j<subsequence.length;j++){
                    if(subsequence[j].length()>len){
                        len=subsequence[j].length();
                        cnt=1;
                        Index.clear();
                        Index.add(j);
                    }else if(subsequence[j].length()==len&&subsequence[j].length()!=0){
                        cnt++;
                        Index.add(j);
                    }
                }
                String command="";
                if(cnt==1){
                    command=commands[Index.get(0)]+" "+content;
                } else if (cnt>1){
                    DebugHelper.logColorful("请选择", DebugHelper.printColor.BLUE);
                    for(int j=0;j<cnt;j++){
                        System.out.print(commands[Index.get(j)]+"  ");
                    }
                    DebugHelper.log("重新输入命令");
                    command = scanner.nextLine();
                }else{
                    DebugHelper.logColorful("invalid command",DebugHelper.printColor.RED);
                }
                if(command.contains("RETR")){
                    isWriteFile=true;
                    fileName=command.substring(command.indexOf(" ")+1);
                }
                messager.sendMessage(command);
                MACLayer.macStateMachine.TxPending = true;
            }
        }).start();
        new Thread(() -> {
            while (true) {
                synchronized (GlobalEvent.ALL_DATA_Recieved) {
                    try {
                        GlobalEvent.ALL_DATA_Recieved.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                while(!MACLayer.macBufferController.upStreamQueue.isEmpty()) {
                    String message = MACLayer.macBufferController.getMessage();
//                    if(message.contains("end==="))
//                    {
//                        continue;
//                    }
                    DebugHelper.logColorful(message, DebugHelper.printColor.BLUE);
                    if(isWriteFile)
                    {
                        isWriteFile=false;
                        try {
                            FileWriter writer = new FileWriter("./"+fileName);
                            writer.write(message);
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }).start();
    }
}

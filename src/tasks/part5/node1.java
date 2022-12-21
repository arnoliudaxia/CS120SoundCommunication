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

import java.util.ArrayList;
import java.util.Scanner;

import static utils.Lcs.LcsLength;
import static utils.Lcs.s;

public class node1 {
    private static String[] commands={"USER", "PASS", "PWD", "CWD", "PASV", "LIST", "RETR"};
    public static void main(String[] args) {
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef = 0.1f;
        MessageSender messager = new MessageSender();
        new Thread(() -> {
            while (true) {
                Scanner scanner=new Scanner(System.in);
                DebugHelper.log("输入命令");
                String nextLine = scanner.nextLine();
                String[] subsequence = {};
                for (int i = 0; i < commands.length; i++) {
                    LcsLength(commands[i].toCharArray(), nextLine.toCharArray());
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
                    command=commands[Index.get(0)];
                } else if (cnt>1){
                    DebugHelper.logColorful("请选择", DebugHelper.printColor.BLUE);
                    for(int j=0;j<cnt;j++){
                        System.out.print(commands[Index.get(j)]);
                    }
                }else{
                    DebugHelper.logColorful("invalid command",DebugHelper.printColor.RED);
                }
                DebugHelper.log("重新输入命令");
                command = scanner.nextLine();
                messager.sendMessage(command+"ç");
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
                String message=MACLayer.macBufferController.getMessage();
                DebugHelper.logColorful("收到ICMP来自"+message, DebugHelper.printColor.BLUE);
                DebugHelper.logColorful("Reply"+message, DebugHelper.printColor.BLUE);
            }
        }).start();
    }
}

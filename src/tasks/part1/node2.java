package tasks.part1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.Application.SystemController;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class node2 {
    private static boolean isNeedCallBack(String command)
    {
        return command.contains("PWD")||command.contains("LIST")||command.contains("RETR");
    }
    public static void main(String[] args) {
        AudioHw.initAudioHw();
            AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
            MACLayer.initMACLayer();
            DeviceSettings.wakeupRef = 0.16f;
            DeviceSettings.MACAddress = 1;
            DeviceSettings.stopPackageJudge=(seq,crc,frame_type)->{
                return frame_type == 3;
            };
            AudioHw.audioHwG.isRecording = true;
            MessageSender messager = new MessageSender();
            DebugHelper.logColorful("AudioNet is running", DebugHelper.printColor.GREEN);
            try (ServerSocket serverSocket = new ServerSocket(46569)) {
                Socket client = serverSocket.accept();
                DebugHelper.logColorful("InterNet is running", DebugHelper.printColor.GREEN);
                //线程1，帮Node1发出去
                new Thread(() -> {
                    DebugHelper.logColorful("线程1启动", DebugHelper.printColor.GREEN);
                    while(true) {
                        synchronized (GlobalEvent.ALL_DATA_Recieved) {
                            try {
                                DebugHelper.logColorful("等待中", DebugHelper.printColor.GREEN);
                                GlobalEvent.ALL_DATA_Recieved.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        String iptoping = MACLayer.macBufferController.getMessage();
                        DebugHelper.logColorful("收到数据：" + iptoping, DebugHelper.printColor.GREEN);
                        if(iptoping.equals(""))
                        {
                            continue;
                        }
                        DebugHelper.logColorful("告诉python", DebugHelper.printColor.BLUE);
                        try {
                            client.getOutputStream().write(iptoping.getBytes(Charset.defaultCharset()));
                            //处理结果

                            if(isNeedCallBack(iptoping)) {
                                boolean isMultiLines=false;
                                do {
                                    byte[] bytes = new byte[1024];
                                    int read = client.getInputStream().read(bytes);
                                    DebugHelper.logColorful("收到回送", DebugHelper.printColor.CYAN);
                                    String sendMessage = new String(bytes, 0, read, Charset.defaultCharset());
                                    DebugHelper.logColorful("发送" + sendMessage, DebugHelper.printColor.CYAN);
                                    messager.sendMessage(sendMessage);
                                    if (iptoping.contains("LIST")||iptoping.contains("RETR")) {
                                        isMultiLines= !sendMessage.contains("end===");
                                    }
                                    MACLayer.macStateMachine.TxPending = true;
                                    SystemController.threadBlockTime(5);

                                } while (isMultiLines);
                            }
                            DebugHelper.logColorful("回送处理完成", DebugHelper.printColor.BLUE);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                        MACLayer.macStateMachine.TxPending = true;
                    }

                }).start();

            SystemController.threadBlockTime(999999999);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

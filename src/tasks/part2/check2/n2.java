package tasks.part2.check2;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;
import utils.DebugHelper;
import utils.smartConvertor;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class n2 {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef=0.16f;
        DeviceSettings.MACAddress = 0;

        // 服务端监听 1111 端口
        try (ServerSocket serverSocket = new ServerSocket(1111)) {
            System.out.println("等待连接");
            Socket client = serverSocket.accept();
            System.out.println("连接成功！");
            while (true) {
                // 获取客户端输入流
                InputStream inputStream = client.getInputStream();
                byte[] bytes = new byte[4096];
                int read = inputStream.read(bytes);
                String s = new String(bytes, StandardCharsets.UTF_8);
                ArrayList<String> readLines = new ArrayList<>();
                MessageSender m = new MessageSender();
                while (!s.isEmpty()) {
                    int index = s.indexOf("ç");
                    if(index==-1) {
                        break;
                    }
                    readLines.add(s.substring(0, index));
                    s = s.substring(index + 1);
                }
                for (String line : readLines) {
                    ArrayList<Integer> information = new ArrayList<>();
                    for (byte b : line.getBytes(Charset.defaultCharset())) {
                        information.addAll(smartConvertor.exactBitsOfNumber(b, 8));
                    }
                    for (byte b : "ç".getBytes()) {
                        information.addAll(smartConvertor.exactBitsOfNumber(b, 8));
                    }
                    m.sendBinary(information);
                    while (true) {
                        MACLayer.macBufferController.resend();
                        MACLayer.macStateMachine.TxPending = true;
                        if (MACLayer.macBufferController.isAllSent()) {
                            //全部发送成功
                            DebugHelper.log("全部发送成功");
                            break;
                        }
                        synchronized (GlobalEvent.Recieved_Frame) {
                            try {
                                GlobalEvent.Recieved_Frame.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        DebugHelper.log("我收到了对方发的ACK");
                        MACLayer.macBufferController.framesSendCount = 0;
                    }
                }

            }
//            Files.writeString(Paths.get(FileName),s.substring(0,index));
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            BufferedInputStream in = new BufferedInputStream(inputStream);
//            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(FileName));
//            int len=-1;
//            byte[] b=new byte[1024];
//            while((len=in.read(b))!=-1){
//                out.write(b,0,len);
//            }
//            in.close();
//            out.flush();
//            out.close();
        }
    }
}

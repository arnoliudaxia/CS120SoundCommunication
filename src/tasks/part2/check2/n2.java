package tasks.part2.check2;

import OSI.Application.GlobalEvent;
import OSI.Application.MessageSender;
import OSI.MAC.MACLayer;
import utils.DebugHelper;
import utils.smartConvertor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class n2 {
    public static void main(String[] args)throws IOException {
        Scanner scanner = new Scanner(System.in);

        // 服务端监听 1111 端口
        ServerSocket serverSocket = new ServerSocket(1111);
        System.out.println("等待连接");
        Socket client = serverSocket.accept();
        System.out.println("连接成功！");
        while (true) {
            // 获取客户端输入流
            InputStream inputStream = client.getInputStream();
            String FileName="res\\server.txt";
            File file = new File(FileName);
            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            String s = new String(bytes,"utf-8");
            ArrayList<String> readLines=new ArrayList<>();
            MessageSender m=new MessageSender();
            while(!s.isEmpty()){
                int index=s.indexOf("ç");
                readLines.add(s.substring(0,index));
                s=s.substring(index+1,s.length());
            }
            for(String line:readLines){
                ArrayList<Integer> information=new ArrayList<>();
                for(byte b:line.getBytes(Charset.defaultCharset())){
                    information.addAll(smartConvertor.exactBitsOfNumber(b, 8));
                }
                for (byte b : "ç".getBytes()) {
                    information.addAll(smartConvertor.exactBitsOfNumber(b, 8));
                }
                m.sendBinary(information);
            }
            while(true){
                MACLayer.macBufferController.resend();
                MACLayer.macStateMachine.TxPending = true;
                if (MACLayer.macBufferController.isAllSent()) {
                    //全部发送成功
                    DebugHelper.log("全部发送成功");
                    break;
                }
                synchronized (GlobalEvent.Recieved_Frame) {
                    try {
                        GlobalEvent.Recieved_Frame.wait(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                DebugHelper.log("我收到了对方发的ACK");
                MACLayer.macBufferController.framesSendCount = 0;
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

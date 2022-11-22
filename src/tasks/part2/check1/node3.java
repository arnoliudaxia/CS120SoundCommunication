package tasks.part2.check1;

import utils.DebugHelper;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class node3 {
    public static void main(String[] args) {
        //我的任务是要接受node2发来的数据然后展示出来
        System.out.println("等待连接服务端！");
        int remotePort = 1111;
        ArrayList<Byte> lastConnection = new ArrayList<>();
        boolean isNeedConnect=false;
        try(Socket socket = new Socket("localhost", remotePort);) {
            DebugHelper.log("连接服务端成功！");
            while(true) {
                byte[] bytes = new byte[2048];
                int read;
                try {
                    read = socket.getInputStream().read(bytes);
                } catch (java.net.SocketException e) {
                    DebugHelper.log("服务端断开连接！");
                    break;
                }
                String s;
                if(isNeedConnect)
                {
                    byte[] cbytes = new byte[4096];
                    for (int i = 0; i < lastConnection.size(); i++) {
                        cbytes[i]=lastConnection.get(i);
                    }
                    for (int i = 0; i < read; i++) {
                        cbytes[i+lastConnection.size()]=bytes[i];
                    }
                    s=new String(cbytes, 0, read, Charset.defaultCharset());
                    isNeedConnect=false;
                }
                else{
                    s=new String(bytes, 0, read, Charset.defaultCharset());
                }
                int endindex;
                if((endindex=s.lastIndexOf('ç'))!=-1)
                {
                    s=s.substring(0,endindex);
                }
                else {
                    //没有找到ç，说明应该还有数据
                    for (int i = 0; i < read; i++) {
                        lastConnection.add(bytes[i]);
                    }
                    isNeedConnect=true;
                    continue;
                }
                DebugHelper.log("收到来自"+socket.getInetAddress().getHostAddress()+"的"+socket.getPort()+"端口消息：");
                System.out.println(s);


//                while ((read = socket.getInputStream().read(bytes)) != -1) {
//                    DebugHelper.log("收到来自"+socket.getInetAddress().getHostAddress()+"的"+socket.getPort()+"端口消息：");
//                    String s=new String(bytes, 0, read, Charset.defaultCharset());
//                    int endindex;
//                    if((endindex=s.lastIndexOf('ç'))!=-1)
//                    {
//                        s=s.substring(0,endindex);
//                    }
//                    System.out.println(s);
//                    if(!socket.isConnected())
//                    {
//                        DebugHelper.log("Socket连接结束");
//                        return;
//                    }
//                }
            }

        } catch (IOException e) {
            DebugHelper.log("无法连接Socket");
            throw new RuntimeException(e);
        }
    }
}


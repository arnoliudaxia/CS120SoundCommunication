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
                String s=new String(bytes, 0, read, Charset.defaultCharset());
                int endindex;
                if((endindex=s.lastIndexOf('ç'))!=-1)
                {
                    s=s.substring(0,endindex);
                }
                DebugHelper.log("收到来自"+socket.getInetAddress().getHostAddress()+"的"+socket.getPort()+"端口消息：");
                System.out.println(s);

            }

        } catch (IOException e) {
            DebugHelper.log("无法连接Socket");
            throw new RuntimeException(e);
        }
    }
}


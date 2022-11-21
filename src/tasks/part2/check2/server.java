package tasks.part2.check2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class server {
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
            int index=s.indexOf("ç");
            Files.writeString(Paths.get(FileName),s.substring(0,index));
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

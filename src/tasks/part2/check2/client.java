package tasks.part2.check2;

import java.io.File;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import utils.ReadTxt;

import static utils.ReadTxt.readTxt;
import static utils.ReadTxt.readTxtBytes;

public class client{
    public static void main (String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("等待连接服务端！");
        Socket socket = new Socket("localhost", 1111);
        System.out.println("连接服务端成功！");
        while (true) {
            OutputStream outputStream = socket.getOutputStream();
            String s=readTxt("res\\INPUT.txt")+"ç";
            byte[] b=s.getBytes("utf-8");
            outputStream.write(b);
        }
    }
}

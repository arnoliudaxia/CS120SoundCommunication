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

public class client{
    public static void main (String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("等待连接服务端！");
        Socket socket = new Socket("10.20.101.61", 1111);
        System.out.println("连接服务端成功！");
        File file = new File("res\\INPUT.txt");
        while (true) {
            // 给服务端发信息
            System.out.print("请输入：");
            String s = scanner.next();
            if ("out".equals(s)) {
                break;
            }
        }
    }
}

import OSI.Application.SystemController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Tester {
    public static void main(final String[] args) throws IOException {
//        System.out.print("\33[31m 文字"+"\33[m\n");
        try(ServerSocket socket = new ServerSocket(46569))
        {
            Socket client = socket.accept();
            System.out.println("有连接！");
            while(true)
            {
                OutputStream outputStream = client.getOutputStream();
                outputStream.write("Hello World".getBytes(StandardCharsets.UTF_8));
                SystemController.threadBlockTime(1000);
            }

        }
    }
}
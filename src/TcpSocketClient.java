import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TcpSocketClient {

    /**
     * 客户端程序
     */
    public void client() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("等待连接服务端！");
        Socket socket = new Socket("127.0.0.1", 2111);
        System.out.println("连接服务端成功！");
        while (true) {
            // 给服务端发信息
            System.out.print("请输入：");
            String s = scanner.next();
            if ("out".equals(s)) {
                break;
            }
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(s.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = new byte[1024];

            // 读一下服务端发来的信息
            InputStream inputStream = socket.getInputStream();
            int read = inputStream.read(bytes);
            System.out.println("服务端：" + new String(bytes, 0, read, Charset.defaultCharset()));
        }
    }

    public static void main(String[] args) throws IOException {
        TcpSocketClient tcpSocketServer = new TcpSocketClient();
        tcpSocketServer.client();
    }
}
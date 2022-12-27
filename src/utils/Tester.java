package utils;

import OSI.Application.GlobalEvent;
import org.apache.commons.net.ftp.FTPClient;
import utils.DebugHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static tasks.part5.node1.isWrite;

public class Tester {
    public static FTPClient ftpClient = new FTPClient();
    public static String username="";
    public static Object filewrite=new Object();
    private static int flag=0;
    public static void testlog (String path) {
        ftpClient.setControlEncoding("UTF-8");
        try {
            DebugHelper.log("正在连接");
            ftpClient.connect(path);
            ftpClient.enterLocalPassiveMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void testother(String allcommand) throws IOException {
        ftpClient.setControlEncoding("UTF-8");
        String command=allcommand.substring(0,allcommand.indexOf(" "));
        String content=allcommand.substring(allcommand.indexOf(" ")+1);
        if(command.equals("USER")&&content.equals("anonymous")){
            //DebugHelper.log("正在登录");
            ftpClient.login("anonymous", "");
            //DebugHelper.log("登录成功");
        } else if (command.equals("USER")) {
            username=content;
            //DebugHelper.log("设置用户名成功");
        }
        if(command.equals("PASS")){
            //DebugHelper.log("正在登录");
            ftpClient.login(username, content);
            //DebugHelper.log("登录成功");
        }
        if(command.equals("CWD")){
            ftpClient.changeWorkingDirectory(content);
            //DebugHelper.log("切换目录");
        }
        if(command.equals("RETR")){
            while(flag==0){
                FileOutputStream fos = new FileOutputStream("res/"+content);
                if (ftpClient.retrieveFile(content, fos)) {
                    DebugHelper.log("下载成功");
                    flag++;
                } else {
                    DebugHelper.log("下载失败");
                }
                fos.close();
            }
            synchronized (filewrite){
                try {
                    filewrite.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            DebugHelper.logColorful("dog",DebugHelper.printColor.RED);
            Path s= Paths.get("res/"+content);
            Path dest=Paths.get(content);
            Files.move(s,dest, StandardCopyOption.REPLACE_EXISTING);
            flag=0;
        }
    }
}

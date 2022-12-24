package utils;

import org.apache.commons.net.ftp.FTPClient;
import utils.DebugHelper;

import java.io.FileOutputStream;
import java.io.IOException;

import static tasks.part5.node1.isWrite;

public class Tester {
    public static FTPClient ftpClient = new FTPClient();
    public static String username="";
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
        if(command=="USER"&&content=="anonymous"){
            DebugHelper.log("正在登录");
            ftpClient.login("anonymous", "");
            DebugHelper.log("登录成功");
        } else if (command=="USER") {
            username=content;
            DebugHelper.log("设置用户名成功");
        }
        if(command=="PASS"){
            DebugHelper.log("正在登录");
            ftpClient.login(username, content);
            DebugHelper.log("登录成功");
        }
        if(command=="CWD"){
            ftpClient.changeWorkingDirectory(content);
            DebugHelper.log("切换目录");
        }
        if(command=="RETR"){
            while(true) {
                if(isWrite) {
                    FileOutputStream fos = new FileOutputStream(content);
                    if (ftpClient.retrieveFile(content, fos)) {
                        DebugHelper.log("下载成功");
                    } else {
                        DebugHelper.log("下载失败");
                    }
                    fos.close();
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            }
        }
    }
}

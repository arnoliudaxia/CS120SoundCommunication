import org.apache.commons.net.ftp.FTPClient;
import utils.DebugHelper;

import java.io.FileOutputStream;
import java.io.IOException;

public class Tester {
    public static void main(String[] args) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
        try {
            DebugHelper.log("正在连接");
            ftpClient.connect("ftp.gnu.org");
            ftpClient.enterLocalPassiveMode();
            DebugHelper.log("正在登录");
            ftpClient.login("anonymous", "");
            DebugHelper.log("登录成功");
            ftpClient.changeWorkingDirectory("/video");
            DebugHelper.log("切换目录");
            String downFilename="README";
            downFilename="fry720.jpg";
            FileOutputStream fos = new FileOutputStream(downFilename);
            if(ftpClient.retrieveFile(downFilename, fos))
            {
                DebugHelper.log("下载成功");
            }
            else {
                DebugHelper.log("下载失败");
            }
            fos.close();
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package OSI.Application;

import OSI.MAC.MACLayer;
import utils.DebugHelper;
import utils.smartConvertor;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MessageSender {
    
    public void sendMessage(String message) {
        //TODO
        DebugHelper.log("随便发一点什么东西");
        ArrayList<Integer> data=new ArrayList<>();
        data.add(1);
        data.add(1);
        data.add(1);
        MACLayer.macBufferController.trySend(data);
    }
    public void sendBinary(ArrayList<Integer> input) {
        System.out.println("发送二进制数据包");
        MACLayer.macBufferController.trySend(input);
    }
    public void sendFile(String path) throws FileNotFoundException {
        DebugHelper.log("发送文件: "+path);
        MACLayer.macBufferController.trySend(smartConvertor.binInFile(path));
    }

}

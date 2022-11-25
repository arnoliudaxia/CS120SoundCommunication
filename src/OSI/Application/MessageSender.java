package OSI.Application;

import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import utils.DebugHelper;
import utils.smartConvertor;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MessageSender {
    public void sendMessage(String message) {
        DebugHelper.log("发送字符串");
        sendBytes(message.getBytes(Charset.defaultCharset()));
//        var dataInBytes=message.getBytes(Charset.defaultCharset());
//        ArrayList<Integer> data=new ArrayList<>();
//        for(byte b:dataInBytes){
//            data.addAll(smartConvertor.exactBitsOfNumber(b,8));
//        }
//        for (byte b : "ç".getBytes()) {
//            data.addAll(smartConvertor.exactBitsOfNumber(b, 8));
//        }
//        MACLayer.macBufferController.trySend(data);
    }
    public void sendBytes(byte[] message) {
        DebugHelper.log("发送bytes");
        ArrayList<Integer> data=new ArrayList<>();
        for(byte b:message){
            data.addAll(smartConvertor.exactBitsOfNumber(b,8));
        }
        for (byte b : "ç".getBytes()) {
            data.addAll(smartConvertor.exactBitsOfNumber(b, 8));
        }
        MACLayer.macBufferController.trySend(data);
    }
    public void sendBinary(ArrayList<Integer> input) {
        DebugHelper.log("发送二进制数据包");
        AudioHw.audioHwG.isPlay=true;
        MACLayer.macBufferController.trySend(input);

    }
    public void sendFile(String path) throws FileNotFoundException {
        DebugHelper.log("发送文件: "+path);
        AudioHw.audioHwG.isPlay=true;
        MACLayer.macBufferController.trySend(smartConvertor.binInFile(path));
    }

}

package OSI.Application;

import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
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
        DebugHelper.log("发送二进制数据包");
        AudioHw.audioHwG.isPlay=true;
        MACLayer.macBufferController.trySend(input);
        MACLayer.macStateMachine.preSum=MACLayer.macBufferController.downStreamQueue.size();
    }
    public void sendFile(String path) throws FileNotFoundException {
        DebugHelper.log("发送文件: "+path);
        AudioHw.audioHwG.isPlay=true;
        MACLayer.macBufferController.trySend(smartConvertor.binInFile(path));
    }

}

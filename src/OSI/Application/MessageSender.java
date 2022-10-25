package OSI.Application;

import OSI.MAC.MACLayer;

import java.util.ArrayList;

public class MessageSender {
    public static MessageSender messageSender;

    public MessageSender() {
        if(messageSender==null) {
            messageSender = this;
        }
        else {
            System.out.println("MessageSender is already created");
        }
    }
    public void sendMessage(String message) {
        //TODO
        System.out.println("随便发一点什么东西");
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

}

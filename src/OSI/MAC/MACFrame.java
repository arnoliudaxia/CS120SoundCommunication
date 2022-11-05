package OSI.MAC;

import java.util.ArrayList;

public class MACFrame {
    MACFrame(int iseq, ArrayList<Integer> idata,int icrc,int iframe_type) {
        seq = iseq;
        payload = idata;
        crc=icrc;
        frame_type=iframe_type;
    }


    public int seq;
    public int frame_type; //0是data，1是ack
    public ArrayList<Integer> payload;
    public int crc;
}

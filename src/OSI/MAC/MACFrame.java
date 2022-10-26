package OSI.MAC;

import java.util.ArrayList;

public class MACFrame {
    MACFrame(int iseq, ArrayList<Integer> idata,int icrc) {
        seq = iseq;
        payload = idata;
        crc=icrc;
    }

    public int seq;
    public ArrayList<Integer> payload;
    public int crc;
}

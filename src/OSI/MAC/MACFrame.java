package OSI.MAC;

import OSI.Link.frameConfig;
import utils.smartConvertor;

import java.util.ArrayList;
import java.util.Comparator;


public class MACFrame {
    static Comparator<MACFrame> cmp = new Comparator<MACFrame>() {
        @Override
        public int compare(MACFrame o1, MACFrame o2) {
            return ((MACFrame)o1).seq-((MACFrame)o2).seq;
        }
    };
    MACFrame(int iseq, ArrayList<Integer> idata,int icrc,int iframe_type,int srcMac) {
        seq = iseq;
        payload = idata;
        crc=icrc;
        frame_type=iframe_type;
        src_mac=srcMac;
    }

    /**
     * 直接通过一帧的原始数据（数字）构建MACFrame
     * @param rawframe
     */
    MACFrame(ArrayList<Integer> rawframe){
        assert rawframe.size()== frameConfig.bitLength;
        //data的前10位是序号,需要把二进制转换回数字
        seq= smartConvertor.mergeBitsToInteger(rawframe.subList(0,10));
        frame_type=smartConvertor.mergeBitsToInteger(new ArrayList<>(rawframe.subList(10,12)));
        src_mac =smartConvertor.mergeBitsToInteger(new ArrayList<>(rawframe.subList(12,14)));
        payload= new ArrayList<>(rawframe.subList(14,14+SEGEMENT[3]));
        crc=smartConvertor.mergeBitsToInteger(new ArrayList<>(rawframe.subList(14+SEGEMENT[3],14+SEGEMENT[3]+16)));
        assert 14+SEGEMENT[3]+16== frameConfig.bitLength;

    }



    public int seq;//10位
    public int frame_type; //2位，0是data，1是ack,3是终止包
    public int src_mac;//2位，发送方mac地址
    public ArrayList<Integer> payload;
    public int crc;//crc16
    public static final int[] SEGEMENT= {10,2,2,frameConfig.bitLength-10-2-2-16,16};

}

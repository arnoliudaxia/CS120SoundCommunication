package utils;


import java.util.ArrayList;

public class CRC {

    /**
     * 一个字节包含位的数量 8
     */
    private static final int BITS_OF_BYTE = 8;

    /**
     * 多项式
     */
    private static final int POLYNOMIAL = 0xA001;

    /**
     * 初始值
     */
    private static final int INITIAL_VALUE = 0xFFFF;

    /**
     * CRC16 编码
     * @param Data 编码内容
     * @return 编码结果
     */
    public static int crc16(ArrayList<Integer> inputData) {
        ArrayList<Integer>Data=new ArrayList<>();
        Data.addAll(inputData);
        ArrayList<Integer> bytes=new ArrayList<>();
        while(Data.size()>0){
            int len=Math.min(BITS_OF_BYTE, Data.size());
            String b="";
            for(int i=0;i<len;i++){
                b+=String.valueOf(Data.get(i));
            }
            bytes.add(Integer.parseInt(b,2));
            Data.subList(0,len).clear();
        }
        int res = INITIAL_VALUE;
        for (int data : bytes) {
            res = res ^ data;
            for (int i = 0; i < BITS_OF_BYTE; i++) {
                res = (res & 0x0001) == 1 ? (res >> 1) ^ POLYNOMIAL : res >> 1;
            }
        }
        return revert(res);
    }

    /**
     * 将高八位和低八位变换位置
     * @param src 翻转数字
     * @return 翻转结果
     */
    private static int revert(int src) {
        int lowByte = (src & 0xFF00) >> 8;
        int highByte = (src & 0x00FF) << 8;
        return lowByte | highByte;
    }
}



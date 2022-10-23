package dataAgent;

import utils.SoundUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class MemoryData implements CallBackStoreData {

    private final ByteArrayOutputStream tempBufferInMemory = new ByteArrayOutputStream();

    ObjectOutputStream outputS;

    private Object readOrWriteLock = new Object();

    public MemoryData() {
        try {
            outputS = new ObjectOutputStream(tempBufferInMemory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeData(float[] data)  {
        for (float datum : data) {
            try{
                synchronized (readOrWriteLock)
                {
                    outputS.writeFloat(datum);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public LinkedList<float[]> retriveData(int fragmentSize) {
        try {
            outputS.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] data;
        synchronized (readOrWriteLock){
            data=tempBufferInMemory.toByteArray();
            tempBufferInMemory.reset();
        }
        return SoundUtil.getSoundFromSerializedByte(data,fragmentSize);//将输出流中的数据转化为Byte数组

    }

}

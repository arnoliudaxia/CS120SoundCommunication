package dataAgent;

import java.io.*;
import java.util.LinkedList;

public class MemoryData implements CallBackStoreData {

    private ByteArrayOutputStream tempBufferInMemory = new ByteArrayOutputStream();

    ObjectOutputStream outputS;

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
            outputS.writeFloat(datum);
            }catch (IOException e){
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
        return SoundUtil.getSoundFromSerializedByte(tempBufferInMemory.toByteArray(),fragmentSize);

    }

}

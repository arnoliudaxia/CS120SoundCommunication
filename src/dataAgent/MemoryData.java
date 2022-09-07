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
        LinkedList<float[]> result = new LinkedList<>();
        try {
            outputS.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayInputStream deSerializedStream = new ByteArrayInputStream(tempBufferInMemory.toByteArray());
        tempBufferInMemory.reset();
        if (!(deSerializedStream.available() > 0))//没有数据提取个鬼给爷爬
        {
            return null;
        }
        try (ObjectInputStream input = new ObjectInputStream(deSerializedStream)) {
            float MaxPower = 0;
//TODO 这里为了调试忽略了尾部
            while (deSerializedStream.available() > fragmentSize) {
                float[] readBuffer=new float[fragmentSize];
                for (int i = 0; i < fragmentSize; i++) {
                    float temp = input.readFloat();
                    MaxPower = Math.max(MaxPower, Math.abs(temp));
                    readBuffer[i]=temp;
                }
                result.add(readBuffer);

            }
            //到这里所有记录的数据都已经分好包了
            //现在将所有要输出的声音进行放大增益
            System.out.println("MaxPower:" + MaxPower);
            for (float[] floatArray : result) {
                for (int i = 0; i < fragmentSize; i++) {
                    floatArray[i] *= 0.7 / MaxPower;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return result;
    }

}

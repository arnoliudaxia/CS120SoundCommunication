package dataAgent;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;

public class SoundUtil {
    public static void amplify(LinkedList<float[]> data) {
        float MaxPower = 0;

        for (float[] datum : data) {
            for (float d : datum) {
                MaxPower = Math.max(MaxPower, Math.abs(d));
            }
        }
        for (float[] datum : data) {
            for (int i = 0; i < datum.length; i++) {
                datum[i] *= 1.0f / MaxPower;
            }
        }
    }

    public static LinkedList<float[]> getSoundFromSerializedByte(byte[] data, int fragmentSize) {
        LinkedList<float[]> result = new LinkedList<>();
        ByteArrayInputStream deSerializedStream = new ByteArrayInputStream(data);

        if (!(deSerializedStream.available() > 0))//没有数据提取个鬼给爷爬
        {
            return null;
        }
        try (ObjectInputStream input = new ObjectInputStream(deSerializedStream)) {
            while (deSerializedStream.available() > 0) {
                float[] readBuffer=new float[fragmentSize];
                for (int i = 0; i < fragmentSize; i++) {
                    readBuffer[i]=input.readFloat();
                }
                result.add(readBuffer);
            }
            //到这里所有记录的数据都已经分好包了
            //现在将所有要输出的声音进行放大增益
            SoundUtil.amplify(result);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;

    }
    public static LinkedList<float[]> playsoundFile(String path,int fragmentSize) throws IOException {
        try(FileInputStream fileInputStream = new FileInputStream(path)) {
            return SoundUtil.getSoundFromSerializedByte(fileInputStream.readAllBytes(), fragmentSize);
        }catch (Exception e)
        {
            System.out.println("读取整活音频失败!!");
        }
        return null;
    }

    public static float[] generateSinwave(int hz,int duration,int sampleRate){
        float[] result=new float[duration*sampleRate];
        float phase=0;
        float dphase = (2 * (float) Math.PI * 1000) / sampleRate;
        Arrays.fill(result, (float) (Math.sin((double) phase)));
        return result;
    }

}

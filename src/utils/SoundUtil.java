package utils;

import com.github.psambit9791.jdsp.filter.Butterworth;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;

public class SoundUtil {
    //调整声音的大小  maxRef是系数
    public static float[] amplify(float[] data, float maxRef) {
        float MaxPower = 0;

        for (float d : data) {
            MaxPower = Math.max(MaxPower, Math.abs(d));
        }
        for (int i = 0; i < data.length; i++) {
            data[i] *= maxRef / MaxPower;
        }
        return data;
    }
    public static void simpleAmplify(float[] data, float ratio) {
        for (int i = 0; i < data.length; i++) {
            data[i] *= ratio;
        }
    }
    public static void amplify(LinkedList<float[]> data,float maxRef) {
        for (float[] datum : data) {
           amplify(datum,maxRef);
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
    static float phase=0;

    //duration 区间长度
    public static float[] generateSinwave(int hz,float duration,int sampleRate){
        int arrayLength=(int)(duration*sampleRate);
        float[] result=new float[arrayLength];
        float dphase = (2 * (float) Math.PI * hz) / sampleRate;
        for (int i = 0; i < arrayLength; i++) {
            phase += dphase;
            result[i] = (float) (Math.sin((double) phase));  // sine wave
        }
        return result;
    }

    /**
     * 持续（模拟）高电平或低电平
     * @param bit 如果1则高电平，0则低电平
     * @param duration 信号持续时间，单位s
     * @param sampleRate 采样率
     * @return 信号数组
     */
    public static float[] generateDigitalSignal(int bit,float duration,int sampleRate) {
        int arrayLength=(int)(duration*sampleRate);
        float[] result=new float[arrayLength];
        Arrays.fill(result, bit);
        return result;
    }
    public static double[] bandPassFilter(double[] data,int samplingRate,int lowCut,int hightCut)
    {
        int order = 4; //order of the filter
        Butterworth flt = new Butterworth(samplingRate); //signal is of type double[]
        return flt.bandPassFilter(data, order, lowCut, hightCut);
    }

}

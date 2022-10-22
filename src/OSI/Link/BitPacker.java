package OSI.Link;

import OSI.Physic.AudioHw;
import utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

public class BitPacker {
    public BitPacker(int sampleFre) {
        //定义好信号的频率
        this.zeroSignal = SoundUtil.generateDigitalSignal(0, fragmentTime, sampleFre);
        this.oneSignal = SoundUtil.generateDigitalSignal(1, fragmentTime, sampleFre);
        //每一个数据包的长度
        frameConfig.fragmentLength= fragmentLength = (int) (fragmentTime * sampleFre);
        //丢弃原来的header，直接选用20采样点的高电平表示1
        signal = new float[headerLength+bitLength * fragmentLength];
        //float数组默认初始化0，赋值1
        for (int i = 0; i < headerLength; i++) {
            signal[i] = 1;
        }
        rawDataIndex = headerLength;
    }


    public void AppendData(List<Integer> data) {
        for (Integer datum : data) {
            if(rawDataIndex>=headerLength+(bitLength) * fragmentLength)
            {
                send();
            }
            System.arraycopy(datum == 1 ? oneSignal : zeroSignal, 0, signal,
                    (rawDataIndex) , fragmentLength);
            rawDataIndex+=fragmentLength;
        }

    }

    /**
     * 如果数据不够一帧，补0
     */
    public void padding()
    {
        int paddingsize=(headerLength+(bitLength) * fragmentLength-rawDataIndex)/fragmentLength;
        List<Integer> data=new ArrayList<>();
        for (int i = 0; i < paddingsize; i++) {
            data.add(0);
        }
        AppendData(data);
    }


    public void send() {
        if(rawDataIndex<=headerLength)return;
        if(onepackage.size()==0)
        {
            for (float v : signal) {
                onepackage.add(v);
            }
        }
        System.out.println("发送包数量"+AudioHw.audioHwG.playRawData(signal));
        AudioHw.audioHwG.isPlay = true;
        rawDataIndex = headerLength;
    }

    private final float[] oneSignal;
    private final float[] zeroSignal;
//    private final float[] header=frameConfig.header;
    final int headerLength=20;
    private final int bitLength = frameConfig.bitLength;
    private int rawDataIndex;

    final float fragmentTime = frameConfig.fragmentTime;
    final int fragmentLength;

    //singal就是最终的信号
    float[] signal = null;

    public ArrayList<Float> onepackage = new ArrayList<>();


}

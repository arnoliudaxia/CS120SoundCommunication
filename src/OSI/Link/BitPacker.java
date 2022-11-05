package OSI.Link;

import OSI.Application.UserSettings;
import OSI.Physic.AudioHw;
import utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 该层接收到上层传过来的二进制数据，将其编码为frames然后传递给AudioHw层
 */
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
        //直接填一针帧，确保挤到下一个frame
        List<Integer> data=new ArrayList<>();
        for (int i = 0; i < bitLength; i++) {
            data.add(0);
        }
        AppendData(data);
        //然后把多出来的清掉就行
        rawDataIndex = headerLength;

    }


    public void send() {
        if(rawDataIndex<=headerLength)return;
        if(UserSettings.isDebug&&onepackage.size()==0)
        {
            for (float v : signal) {
                onepackage.add(v);
            }
        }
        AudioHw.audioHwG.playRawData(signal);
//        System.out.println("发送包数量"+AudioHw.audioHwG.playRawData(signal));
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
    /**
     * 当UserSettings里的isdebug为true时，会把第一帧的数据存到这里
     */
    public ArrayList<Float> onepackage = new ArrayList<>();


}

package OSI.Link;

import OSI.Physic.AudioHw;
import utils.SoundUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BitPacker {
    public BitPacker(int sampleFre) {
        //定义好信号的频率
        this.zeroSignal = SoundUtil.generateDigitalSignal(0, fragmentTime, sampleFre);
        this.oneSignal = SoundUtil.generateDigitalSignal(1, fragmentTime, sampleFre);
        //每一个数据包的长度
        frameConfig.fragmentLength= fragmentLength = (int) (fragmentTime * sampleFre);
        signal = new float[headerLength+bitLength * fragmentLength];
        //把headr放到singal开头
        System.arraycopy(header, 0, signal, 0, headerLength);
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
    private final float[] header=frameConfig.header;
    final int headerLength=header.length;
    private final int bitLength = frameConfig.bitLength;
    private int rawDataIndex;

    final float fragmentTime = frameConfig.fragmentTime;
    final int fragmentLength;

    //singal就是最终的信号
    float[] signal = null;

    public ArrayList<Float> onepackage = new ArrayList<>();


}

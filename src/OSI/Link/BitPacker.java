package OSI.Link;

import OSI.Physic.AudioHw;
import dataAgent.SoundUtil;

import java.util.List;

public class BitPacker {
    public BitPacker(int sampleFre) {
        this.oneSignal = SoundUtil.generateSinwave(12000, fragmentTime, sampleFre);
        this.zeroSignal = SoundUtil.generateSinwave(8000, fragmentTime, sampleFre);
        headerLength = headerFrame.size();
        fragmentLength = (int) (fragmentTime * sampleFre);
        signal = new float[(headerLength + bitLength) * fragmentLength];
        for (int headindex = 0; headindex < headerLength; headindex++) {
            System.arraycopy(headerFrame.get(headindex) == 1 ? this.oneSignal : this.zeroSignal, 0, signal, headindex * fragmentLength, fragmentLength);
//            System.out.println(headerFrame.get(headindex));
        }
        rawDataIndex = headerLength;
    }


    public void AppendData(List<Integer> data) {
        for (Integer datum : data) {
            if(rawDataIndex>=bitLength+headerLength)
            {
                send();
            }

            System.arraycopy(datum == 1 ? oneSignal : zeroSignal, 0, signal,
                    (rawDataIndex) * fragmentLength, fragmentLength);
            rawDataIndex++;
        }

    }

    public void send() {
        if(rawDataIndex<=headerLength)return;
        System.out.println(AudioHw.audioHwG.playRawData(signal));
        AudioHw.audioHwG.isPlay = true;
        rawDataIndex = headerLength;
    }

    private final float[] oneSignal;
    private final float[] zeroSignal;
    final int headerLength;
    final List<Integer> headerFrame = List.of(1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0);
    private final int bitLength = 50;
    private int rawDataIndex;

    final float fragmentTime = 0.05f;
    final int fragmentLength;

    float[] signal = null;

}

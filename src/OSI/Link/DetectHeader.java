package OSI.Link;

import dataAgent.MemoryData;

import java.util.LinkedList;

public class DetectHeader implements FrameDetection {
    private LinkedList<float[]> detectData = new LinkedList<>();
    private int detectLength = frameConfig.digitalHeaderLength*2;
    @Override
    public boolean detectPossibleFrame(float[] data) {
        var memoryData = new MemoryData();
        detectData = memoryData.retriveData(detectLength);
        float judgeRef = 0.f;
        int last = -10;
        int bitCounter=0;
        for (int i = 0; i < detectData.size(); i++) {
            float[] temp = detectData.get(i);
            int j = 0;
            if(last!=-10)bitCounter=detectLength-j;
            while (j < temp.length) {
                if (temp.length - j >= frameConfig.digitalHeaderLength) {
                    if (temp[j] > judgeRef) {
                        bitCounter++;
                        j++;
                    }
                    else {
                        j++;
                        continue;
                    }
                    while (j < temp.length) {
                        if (bitCounter == frameConfig.digitalHeaderLength) return true;
                        if (temp[j] > judgeRef) {
                            bitCounter++;
                            j++;
                        } else {
                            bitCounter=0;
                            j++;
                            break;
                        }
                    }
                } else {
                    if (temp[j] > judgeRef && last == -10) {
                        last = j;
                    } else if (temp[j] < judgeRef) last = -10;
                }
            }
        }
        return false;
    }
}

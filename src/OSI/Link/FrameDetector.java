package OSI.Link;

import dataAgent.CallBackStoreData;
import utils.SoundUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static OSI.Link.frameConfig.fragmentTime;

public class FrameDetector implements CallBackStoreData {
    public final ArrayList<Float> alldata = new ArrayList<>();
    private final float judgeRef = 0.2f;
    private int readDatabitCount = 0;
    private int headerJudgeCount = 0;
    public boolean stopDetectSignal = false;

    public List<ArrayList<Float>> frames = new LinkedList<>();

    enum DetectState {
        lookingForHead, HeadWholeJudge, DataRetrive;
    }

    DetectState detectState = DetectState.lookingForHead;

    public FrameDetector() {
        new Thread(() -> {
            while (!stopDetectSignal) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (alldata.size() == 0) {
                    continue;
                }
                float[] bufferTri;
                synchronized (alldata) {
                    bufferTri = new float[alldata.size()];
                    for (int i = 0; i < alldata.size(); i++) {
                        bufferTri[i] = alldata.get(i);
                    }
                    alldata.clear();
                }
                for (var sampleP : bufferTri) {
                    boolean isHigh = sampleP > judgeRef;
                    switch (detectState) {
                        case lookingForHead:
                            if (isHigh) {
                                detectState = DetectState.HeadWholeJudge;
                                headerJudgeCount = 1;
                            }
                            break;
                        case HeadWholeJudge:
                            if (isHigh) {
                                headerJudgeCount++;
                                if (headerJudgeCount >= 20) {
                                    //找到头了
                                    System.out.println("Header Found");
                                    detectState = DetectState.DataRetrive;
                                    synchronized (frames) {
                                        frames.add(new ArrayList<>());
                                    }
                                }
                            } else {
                                detectState = DetectState.lookingForHead;
                                headerJudgeCount = 0;
                            }
                            break;
                        case DataRetrive:
                            synchronized (frames) {
                                frames.get(frames.size() - 1).add(sampleP);
                            }
                            readDatabitCount++;
                            if (readDatabitCount >= frameConfig.bitLength) {
                                //一个frame完整读取完毕
                                System.out.println("Frame Read Complete");
                                detectState = DetectState.lookingForHead;
                                readDatabitCount=0;
                            }
                            break;
                    }
                }
            }
        }).start();
    }

    @Override
    public void storeData(float[] data) {
        for (float datum : data) {
            synchronized (alldata) {
                alldata.add(datum);
            }
        }
    }

    @Override
    public LinkedList<float[]> retriveData(int fragmentSize) {

        float[] result;
        synchronized (frames) {
            if (frames.size() == 0) {
                return new LinkedList<>();
            }
            result = new float[frames.get(0).size()];
            for (int i = 0; i < frames.get(0).size(); i++) {
                result[i] = frames.get(0).get(i);
            }
            frames.remove(0);
        }
        return new LinkedList<>(List.of(result));

    }

    public List<Integer> decodeOneFrame() {
        List<Integer> result=new ArrayList<>();
        ArrayList<Float> frame = new ArrayList<>();
        LinkedList<float[]> oneframe= retriveData(-1);
        if(oneframe.size()==0){
            return result;
        }
        for(var sample:oneframe.get(0)){
            frame.add(sample);
        }

        //必须先调用findOneHeader找头
        //找到头后，bitData中存储了一个frame的数据
        //现在要做的是将bitData中的数据转换成bit
        float judgeDataRef = 0.f;
        //首先解析第一个数据点，接下来就是一个二元状态机
        int state = frame.get(0) > judgeDataRef ? 1 : 0;
        int bitCounter = 0;
        while (frame.size() > 0) {
            while ((frame.get(0) > judgeDataRef ? 1 : 0) == state) {
                bitCounter++;
                frame.remove(0);
                if (frame.size() == 0) {
                    break;
                }
            }
//            System.out.println("bitCounter: "+bitCounter);
            for (int i = 0; i < SoundUtil.neareatRatio(bitCounter, (int) (fragmentTime * 48000)) / 5; i++) {
                result.add(state);
            }
            state = 1 - state;
            bitCounter = 0;
        }
        System.out.println("Frame处理完毕");
        return result;
    }


}

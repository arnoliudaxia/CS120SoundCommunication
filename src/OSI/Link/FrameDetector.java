package OSI.Link;

import OSI.MAC.MACLayer;
import dataAgent.CallBackStoreData;
import utils.SoundUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static OSI.Link.frameConfig.fragmentTime;

public class FrameDetector implements CallBackStoreData {
    private int readDatabitCount = 0;
    private int headerJudgeCount = 0;
    private float headerEngery = 0;
    //用来判断是不是有其他的干扰源，原理是收集附近几个采样点的能量（绝对值）
    public float localEnergy = 0;
    private float quietRef=0.01f;//认为localEnergy小于这个值就是没有干扰状态

    public final Queue<ArrayList<Float>> frames = new LinkedList<>();
    private ArrayList<Float> writeFrameBuffer = new ArrayList<>();

    enum DetectState {
        lookingForHead, HeadWholeJudge, DataRetrive;
    }
    public ArrayList<Float> wave=new ArrayList<>();

    DetectState detectState = DetectState.lookingForHead;

    @Override
    public void storeData(float[] data) {
        for (var sampleP : data) {
//            wave.add(sampleP);//用来给matlab分析
            localEnergy*=19.f/20.f;
            localEnergy+=Math.abs(sampleP);
//            wave.add(localEnergy);
            float wakeupRef = 0.09f;//header的触发电平
            switch (detectState) {
                case lookingForHead:
                    if (sampleP > wakeupRef) {
                        detectState = DetectState.HeadWholeJudge;
                        headerJudgeCount = 1;
                        headerEngery=sampleP;
                    }
                    break;
                case HeadWholeJudge:
                    headerJudgeCount++;
                    headerEngery+=Math.abs(sampleP);
                    if (headerJudgeCount >= 20) {
                        System.out.println("Header Energy: " + headerEngery);
                        if(headerEngery>1.f&&headerEngery<10.f) {
                            //找到头了
                            MACLayer.macStateMachine.PacketDetected=true;
                            detectState = DetectState.DataRetrive;
                            writeFrameBuffer = new ArrayList<>();
                        }
                        else{
                            detectState = DetectState.lookingForHead;
                            headerJudgeCount = 0;
                        }
                    }
                    break;
                case DataRetrive:
                    writeFrameBuffer.add(sampleP);
                    readDatabitCount++;
                    if (readDatabitCount >= frameConfig.bitLength * frameConfig.bitSamples) {
                        //一个frame完整读取完毕
                        System.out.println("Frame Read Complete");
                        detectState = DetectState.lookingForHead;
                        readDatabitCount = 0;
                        synchronized (frames) {
                            frames.add(writeFrameBuffer);
                            frames.notifyAll();
                        }
                        //拿到一帧直接解析吧
                        decodeOneFrame();

                    }
                    break;
            }
        }

    }

    @Override
    public LinkedList<float[]> retriveData(int fragmentSize) {
        return null;
    }

    /**
     * 从队列里拿一个找到的frame出来
     * @return 一个frame的原始数据
     */
    public ArrayList<Float> retriveFrame() {
        synchronized (frames) {
            if (frames.size() != 0) {
                return frames.poll();
            }
        }
        return null;

    }
    public void deCoder()
    {
        while (true){
            synchronized (frames) {
                try {
                    frames.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 解析收到的一个frame的数据（如果有找到frame）
     * @return 二进制的原始数据，如果没有找到frame则返回一个空的List
     */
    public void decodeOneFrame() {
        ArrayList<Integer> result = new ArrayList<>();


        class decodeThread extends Thread {
            ArrayList<Float> frame = retriveFrame();

            decodeThread(ArrayList<Float> input) {
                frame =input;
            }

            @Override
            public void run() {
                //下面是直接用之前的
                //现在要做的是将bitData中的数据转换成bit
                float judgeDataRef = 0.f;
                //首先解析第一个数据点，接下来就是一个二元状态机
                int state = frame.get(0) > judgeDataRef ? 1 : 0;
                int bitCounter = 0;
                while(frame.size()>0)

                {
                    while ((frame.get(0) > judgeDataRef ? 1 : 0) == state) {
                        bitCounter++;
                        frame.remove(0);
                        if (frame.size() == 0) {
                            break;
                        }
                    }
                    for (int i = 0; i < SoundUtil.neareatRatio(bitCounter, (int) (fragmentTime * 48000)) / frameConfig.bitSamples; i++) {
                        result.add(state);
                    }
                    state = 1 - state;
                    bitCounter = 0;
                }
                MACLayer.macBufferController.__receive(result);
            }
        }

        decodeThread dt=new decodeThread(retriveFrame());
        dt.start();


    }

    public boolean isChannelQuiet(){
        return localEnergy<quietRef;
    }
}

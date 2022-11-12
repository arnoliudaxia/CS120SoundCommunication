package OSI.Link;

import OSI.Application.DeviceSettings;
import OSI.MAC.MACLayer;
import dataAgent.CallBackStoreData;
import utils.DebugHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class FrameDetector implements CallBackStoreData {
    private int readDatabitCount = 0;
    private int headerJudgeCount = 0;
    private float headerEngery = 0;
    //用来判断是不是有其他的干扰源，原理是收集附近几个采样点的能量（绝对值）
    public float localEnergy = 10;
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
//            MACLayer.isChannelReady= localEnergy < quietRef;
            MACLayer.isChannelReady=true;
//            wave.add(localEnergy);
            float wakeupRef = DeviceSettings.wakeupRef;//header的触发电平
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
                    headerEngery+=sampleP;
                    if (headerJudgeCount > 20) {
                        if(headerEngery>1.f&&headerEngery<20.f) {
//                        DebugHelper.log("Header Energy: " + headerEngery);
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
//                        csvFileHelper csv = new csvFileHelper();
//                        String lyfHPURL = "C:\\Users\\Arnoliu\\Desktop\\快速临时处理文件夹\\计网pro\\";
//                        try {
//                            csv.saveToCsv(lyfHPURL+"wave.csv",writeFrameBuffer);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
                        //一个frame完整读取完毕
//                        System.out.println("Frame Read Complete");
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


        class decodeThread extends Thread {
            final ArrayList<Float> frame;

            decodeThread(ArrayList<Float> input) {
                frame =input;
            }

            @Override
            public void run() {
                ArrayList<Integer> result = new ArrayList<>();
                //下面是直接用之前的
                //现在要做的是将bitData中的数据转换成bit
                float judgeDataRef = 0.03f;
                //首先解析第一个数据点，接下来就是一个二元状态机
                int state = frame.get(0) > judgeDataRef ? 1 : 0;
                float judgeEnerge = 0.12f;
                float energeSum=0;
                for (int i = 0; i < frame.size(); i+=5) {
//                    energeSum+=frame.get(i);
                    energeSum+=frame.get(i+1);
                    energeSum+=frame.get(i+2);
                    energeSum+=frame.get(i+3);
//                    energeSum+=frame.get(i+4);
                    result.add(energeSum>judgeEnerge? 1:0);
                    energeSum=0;

                }
                assert result.size()==100;
                MACLayer.macBufferController.__receive(result);
            }
        }

        decodeThread dt=new decodeThread(retriveFrame());
        dt.start();
//        try {
//            dt.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


    }

    public boolean isChannelQuiet(){
        return localEnergy<quietRef;
    }
}

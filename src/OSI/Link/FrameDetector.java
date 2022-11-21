package OSI.Link;

import OSI.Application.DeviceSettings;
import OSI.MAC.MACLayer;
import dataAgent.CallBackStoreData;
import utils.DebugHelper;

import java.util.ArrayList;
import java.util.LinkedList;

public class FrameDetector implements CallBackStoreData {
    private int readDatabitCount = 0;
    private int headerJudgeCount = 0;
    private ArrayList<Float> headerEngery = new ArrayList<>();
    //用来判断是不是有其他的干扰源，原理是收集附近几个采样点的能量（绝对值）

    //    private ArrayList<Float> writeFrameBuffer = new ArrayList<>();
    private float[] writeFrameBuffer = new float[frameConfig.bitLength * frameConfig.bitSamples];


    enum DetectState {
        lookingForHead, HeadWholeJudge, DataRetrive;
    }

    public ArrayList<Float> wave = new ArrayList<>();

    DetectState detectState = DetectState.lookingForHead;

    @Override
    public void storeData(float[] data) {
        for (var sampleP : data) {
            float wakeupRef = DeviceSettings.wakeupRef;//header的触发电平
            switch (detectState) {
                case lookingForHead:
                    if (sampleP > wakeupRef) {
                        detectState = DetectState.HeadWholeJudge;
                        headerJudgeCount = 1;
                        headerEngery.clear();
                        ;
                        headerEngery.add(sampleP);
                    }
                    break;
                case HeadWholeJudge:
                    headerJudgeCount++;
                    headerEngery.add(sampleP);
                    if (headerJudgeCount > 20) {
                        //1010法检验包头
                        float HeaderScore = 0;
                        HeaderScore += headerEngery.subList(0, 5).stream().mapToDouble(d -> (1 - d)).sum();
                        HeaderScore += headerEngery.subList(5, 10).stream().mapToDouble(d -> d).sum();
                        HeaderScore += headerEngery.subList(10, 15).stream().mapToDouble(d -> (1 - d)).sum();
                        HeaderScore += headerEngery.subList(15, 20).stream().mapToDouble(d -> d).sum();
                        DebugHelper.log("Found Header Score: " + HeaderScore);
                        if (HeaderScore < 10) {
                            //找到头了
                            MACLayer.macStateMachine.PacketDetected = true;
                            detectState = DetectState.DataRetrive;
//                            writeFrameBuffer = new ArrayList<>(frameConfig.bitLength * frameConfig.bitSamples);
                        } else {
                            detectState = DetectState.lookingForHead;
                            headerJudgeCount = 0;
                        }
                    }
                    break;
                case DataRetrive:
                    writeFrameBuffer[readDatabitCount++] = sampleP;
                    if (readDatabitCount >= frameConfig.bitLength * frameConfig.bitSamples) {
                        detectState = DetectState.lookingForHead;
                        readDatabitCount = 0;
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
     * 解析收到的一个frame的数据（如果有找到frame）
     *
     * @return 二进制的原始数据，如果没有找到frame则返回一个空的List
     */
    public void decodeOneFrame() {


        class decodeThread extends Thread {
            final float[] frame;

            decodeThread(float[] input) {
                frame = input;
            }

            @Override
            public void run() {
                ArrayList<Integer> result = new ArrayList<>();
                //下面是直接用之前的
                //现在要做的是将bitData中的数据转换成bit
                float judgeDataRef = 0.03f;
                //首先解析第一个数据点，接下来就是一个二元状态机
                float judgeEnerge = 0.13f;
                float energeSum = 0;
                for (int i = 0; i < frame.length; i+=5) {
//                    energeSum+=frame.get(i);
                    energeSum += frame[i + 1];
                    energeSum += frame[i + 2];
                    energeSum += frame[i + 3];
//                    energeSum+=frame[i+4);
                    result.add(energeSum > judgeEnerge ? 1 : 0);
                    energeSum = 0;

                }

                assert result.size() == 100;
                MACLayer.macBufferController.__receive(result);
            }
        }

        decodeThread dt = new decodeThread(writeFrameBuffer);
        dt.start();
//        try {
//            dt.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


    }

}

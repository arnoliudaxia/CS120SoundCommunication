package OSI.Link;

import OSI.Physic.AudioHw;
import dataAgent.MemoryData;
import utils.SoundUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static OSI.Link.frameConfig.fragmentTime;

public class DetectHeader{
    private LinkedList<float[]> detectData = new LinkedList<>();
    private int detectLength = frameConfig.digitalHeaderLength*3;
    private ArrayList<Integer> dataIndex= new ArrayList<>();
    private ArrayList<Integer> tempIndex= new ArrayList<>();
    public ArrayList<Double> bitData=new ArrayList<>();
    private ArrayList<Float> bufferTri = new ArrayList<>();
    public Queue<ArrayList<Float>> frames =  new LinkedList<>();
    private ArrayList<Float> writeFrameBuffer = new ArrayList<>();
    public List<Integer> information=new ArrayList<>();
    private int dataLength=frameConfig.fragmentLength*frameConfig.bitLength;
    private int remain=0;
    public boolean detecting = true;
    private final float judgeRef = 0.2f;
    private int headerCounter=0;
    private int dataCounter=0;
    private float headerEngery = 0;

    enum DetectState {
        lookingForHead, HeadWholeJudge, DataRetrive;
    }

    DetectState detectState = DetectState.lookingForHead;
    public DetectHeader(){
        new Thread(()->{
            while(detecting){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(AudioHw.audioHwG.dataagent==null)continue;
                var memoryData = AudioHw.audioHwG.dataagent;
                detectData = memoryData.retriveData(detectLength);
                if (detectData.size() == 0) continue;
                for (int i = 0; i < detectData.size(); i++) {
                    for (int j = 0; j < detectData.get(i).length; j++) {
                        bufferTri.add(detectData.get(i)[j]);
                    }
                }
                for (var sample : bufferTri) {
                    boolean isHigh = sample > judgeRef;
                    switch (detectState) {
                        case lookingForHead:
                            if (isHigh) {
                                detectState = DetectState.HeadWholeJudge;
                                headerCounter = 1;
                                headerEngery=sample;
                            }
                            break;
                        case HeadWholeJudge:
                            headerCounter++;
                            headerEngery=sample;
                            if (headerCounter >= 20) {
                                //找到头了
                                System.out.println("Header Found");
                                System.out.println("Header Energy: " + headerEngery);
                                if(headerEngery>3.f&&headerEngery<10.f) {
                                    detectState = DetectState.DataRetrive;
                                    writeFrameBuffer = new ArrayList<>();
                                }
                                else{
                                    detectState = DetectState.lookingForHead;
                                    headerCounter = 0;
                                }
                            }
                            break;
                        case DataRetrive:
                            writeFrameBuffer.add(sample);
                            dataCounter++;
                            if (dataCounter >= frameConfig.bitLength * frameConfig.bitSamples) {
                                //一个frame完整读取完毕
                                System.out.println("Frame Read Complete");
                                detectState = DetectState.lookingForHead;
                                dataCounter = 0;
                                synchronized (frames) {
                                    frames.add(writeFrameBuffer);
                                }
                            }
                            break;
                    }
                }
                while(true){
                    var frameresult=decodeOneFrame();
                    if(frameresult.size()==0) break;
                    information.addAll(frameresult);
                }
            }
        }).start();
    }

    public ArrayList<Float> retriveFrame() {

        synchronized (frames) {
            if (frames.size() != 0) {
                return frames.poll();
            }
        }
        return null;

    }
    public List<Integer> decodeOneFrame() {
        List<Integer> result = new ArrayList<>();
        ArrayList<Float> frame = retriveFrame();
        if(frame==null)
        {
            return new ArrayList<>();
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
//    @Override
//    public boolean detectPossibleFrame() {
//        var memoryData = new MemoryData();
//        detectData = memoryData.retriveData(detectLength);
//        float judgeRef = 0.f;
//        int last = -10;
//        int bitCounter=0;
//        for (int i = 0; i < detectData.size(); i++) {
//            float[] temp = detectData.get(i);
//            int j = 0;
//            if(last!=-10)bitCounter=detectLength-j;
//            while (j < temp.length) {
//                if (temp.length - j >= frameConfig.digitalHeaderLength) {
//                    if (temp[j] > judgeRef) {
//                        bitCounter++;
//                        j++;
//                    }
//                    else {
//                        j++;
//                        continue;
//                    }
//                    while (j < temp.length) {
//                        if (bitCounter == frameConfig.digitalHeaderLength) {
//                            tempIndex.add(i);
//                            dataIndex.add(j);
//                        }
//                        if (temp[j] > judgeRef) {
//                            bitCounter++;
//                            j++;
//                        } else {
//                            bitCounter=0;
//                            j++;
//                            break;
//                        }
//                    }
//                } else {
//                    if (temp[j] > judgeRef && last == -10) {
//                        last = j;
//                    } else if (temp[j] < judgeRef) last = -10;
//                }
//            }
//        }
//        if(!dataIndex.isEmpty()){
//            stored();
//            return true;
//        }
//        return false;
//    }
//
//    public void stored(){
//        if(remain!=0){
//            for(int n=0;n<dataLength-remain;n++)
//            {
//                bitData.add(Double.valueOf(detectData.get(0)[n]));
//            }
//        }
//        for(int i=0;i<tempIndex.size();i++){
//            float[] ttemp=detectData.get(tempIndex.get(i));
//            for(int j=0;j<dataIndex.size();j++){
//                int begin=dataIndex.get(j);
//                int cnt=0;
//                if(detectLength-dataIndex.get(j)>=dataLength){
//                    while(cnt<dataLength){
//                        bitData.add(Double.valueOf(ttemp[begin]));
//                        begin++;
//                        cnt++;
//                    }
//                }
//                else {
//                    while(begin< ttemp.length){
//                        bitData.add(Double.valueOf(ttemp[begin]));
//                        begin++;
//                        cnt++;
//                    }
//                    if(i!= tempIndex.size()-1){
//                        float[] tempp=detectData.get(tempIndex.get(i+1));
//                        begin=0;
//                        while(cnt<dataLength){
//                            bitData.add(Double.valueOf(tempp[begin]));
//                            begin++;
//                            cnt++;
//                        }
//                    }
//                    else remain=cnt;
//                }
//            }
//        }
//    }
//}

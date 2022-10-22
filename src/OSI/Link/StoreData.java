package OSI.Link;

import dataAgent.CallBackStoreData;
import utils.SoundUtil;

import java.util.ArrayList;
import java.util.LinkedList;

import static OSI.Link.frameConfig.fragmentTime;

public class StoreData implements CallBackStoreData {
    public ArrayList<Float> alldata = new ArrayList<>();

    private int samplingRate;

    private final int bitLength=frameConfig.bitLength;
    private final int fragmentLength=frameConfig.fragmentLength;
    public ArrayList<Integer> information=new ArrayList<>();

    public ArrayList<Double> bitData=new ArrayList<>();

    public StoreData(int sampleFre){
        this.samplingRate=sampleFre;
    }
    @Override
    public void storeData(float[] data){
        for (float datum : data) {
            alldata.add(datum);
        }
    }

    /**
     * 交替调用findOneHeader()和decodeFrame(),处理alldata里的所有内容，完成后alldata清空
     */
    public void processAllData(int expectLength)
    {
        while(findOneHeader()==1)
        {
            decodeFrame();
        }
        information.subList(expectLength,information.size()).clear();
    }
    /**
     * 寻找高电平位置，然后提取一个frame到bitData
     */
    public int findOneHeader()
    {
        int i=0;bitData.clear();
        while(alldata.get(0)<0.2f)
        {
            //不是header
            alldata.remove(0);
            if(alldata.size()==0)
            {
                //剩余的数据里面没有头了
                return 0;
            }
            i++;
        }
        //找到开始头
        System.out.println("Header Begin at: "+i);
        //去除头
        alldata.subList(0,frameConfig.digitalHeaderLength).clear();

        //数据点开始于headerlength
        for (int j = 0; j < bitLength * fragmentLength; j++) {
            bitData.add(Double.valueOf(alldata.get(j)));
        }
        //写入后删掉这个frame
        alldata.subList(0,bitLength * fragmentLength).clear();
        return 1;
    }

    public void decodeFrame()
    {
        //必须先调用findOneHeader找头
        //找到头后，bitData中存储了一个frame的数据
        //现在要做的是将bitData中的数据转换成bit
        float judgeRef=0.f;
        //首先解析第一个数据点，接下来就是一个二元状态机
        int state=bitData.get(0)>judgeRef?1:0;
        int bitCounter=0;
        while(bitData.size()>0) {
            while ((bitData.get(0) > judgeRef ? 1 : 0) == state) {
                bitCounter++;
                bitData.remove(0);
                if(bitData.size()==0){
                    break;
                }
            }
//            System.out.println("bitCounter: "+bitCounter);
            for (int i = 0; i <SoundUtil.neareatRatio(bitCounter,(int)(fragmentTime*48000))/5; i++) {
                information.add(state);
            }
            state=1-state;
            bitCounter=0;
        }
        System.out.println("Frame处理完毕");
    }
    @Override
    public LinkedList<float[]> retriveData(int fragment){
        return null;
    }

}

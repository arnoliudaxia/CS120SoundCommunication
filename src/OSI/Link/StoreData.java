package OSI.Link;

import com.github.psambit9791.jdsp.misc.UtilMethods;
import com.github.psambit9791.jdsp.signal.CrossCorrelation;
import com.github.psambit9791.jdsp.transform.FastFourier;
import com.github.psambit9791.jdsp.transform._Fourier;
import dataAgent.CallBackStoreData;
import utils.SoundUtil;
import utils.smartConvertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static OSI.Link.frameConfig.fragmentTime;

public class StoreData implements CallBackStoreData {
    public ArrayList<Float> alldata = new ArrayList<>();
    public ArrayList<Float> signal = new ArrayList<>();
    public ArrayList<Float> check = new ArrayList<>();
    private double[] allDataD;
    private final float[] header=frameConfig.header;
    private double[] headerD;
    private int samplingRate;
    private ArrayList<Integer> beginIndex=new ArrayList<>();

    private int bitLength=frameConfig.bitLength;
    private int fragmentLength=frameConfig.fragmentLength;
    public ArrayList<Integer> information=new ArrayList<>();

    public ArrayList<Double> bitData=new ArrayList<>();

    public StoreData(int sampleFre){
        this.samplingRate=sampleFre;
        headerD= smartConvertor.floatToDoubleArray(header);
    }
    @Override
    public void storeData(float[] data){
        for (float datum : data) {
            alldata.add(datum);
        }
    }
    public void convert(){
        int len=alldata.size();
        allDataD=new double[len];
        double[] temp=new double[len];
        for(int i=0;i<len;i++){
            temp[i]=Double.parseDouble(String.valueOf(alldata.get(i)));
        }
        System.arraycopy(temp,0,allDataD,0,len);
        Arrays.stream(allDataD).forEach(x->signal.add((float)x));
    }

    /**
     * 寻找高电平位置，然后提取一个frame到bitData
     */
    public void findOneHeader()
    {
        int i=0;bitData.clear();
        while(alldata.get(0)<0.2f)
        {
            //不是header
            alldata.remove(0);
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
    }

    public void decodeFrame()
    {
        //必须先调用findOneHeader找头
        //找到头后，bitData中存储了一个frame的数据
        //现在要做的是将bitData中的数据转换成bit
        float judgeRef=0.1f;
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
            System.out.println("bitCounter: "+bitCounter);
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

    public void correlation(){
        CrossCorrelation cc = new CrossCorrelation(allDataD, headerD);
        double[] out = cc.crossCorrelate("valid");
        double maxx = Arrays.stream(out).max().getAsDouble();
        Arrays.stream(out).forEach(x-> check.add((float)x));
        int j=0;
        int flag=0;
        for(int i=0;i<out.length;i++){
            if(out[i]>=maxx*0.7){
                if(Math.abs(flag-i)<3000)continue;
                flag=i+440;
                beginIndex.add(i+440);
                System.out.println(i+440);
            }
        }
        fft();
    }

    private boolean isFreqPoint(float freq,float realFreq)
    {
        return Math.abs(freq-realFreq)<300;
    }
    public void fft(){
        double[] temp=new double[fragmentLength];
        for(int i=0;i<beginIndex.size();i++){
            for(int j=0;j<bitLength;j++){
                for(int m=0;m<fragmentLength;m++) {
                    temp[m] = allDataD[beginIndex.get(i) + j*fragmentLength+m];
                }
                double[] tt= SoundUtil.bandPassFilter(temp,samplingRate,6000,14000);
                _Fourier dft = new FastFourier(tt); //Works well for longer signals (>200 points)
                dft.transform();
                boolean onlyPositive = true;
                double[] ff = dft.getMagnitude(onlyPositive);
                int maxIndex= UtilMethods.argmax(ff,true);
                float realFreq=maxIndex*samplingRate/ff.length/2;
                System.out.println("realFreq"+realFreq);

                if(isFreqPoint(realFreq,8000))
                {
                    System.out.println("0");
                    information.add(0);
                }
                else if(isFreqPoint(realFreq,12000))
                {
                    System.out.println("1");
                    information.add(1);

                }
                else{
                    System.out.println("error:没能找到匹配的频率！");

                    information.add(realFreq>10000?1:0);
                }
            }

        }
    }
}

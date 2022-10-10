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

public class StoreData implements CallBackStoreData {
    private ArrayList<Float> alldata = new ArrayList<>();
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

    public StoreData(int sampleFre){
        this.samplingRate=sampleFre;
        headerD= smartConvertor.floatToDoubleArray(header);
    }
    @Override
    public void storeData(float[] data){
        for(int i=0;i<data.length;i++){
            alldata.add(data[i]);
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
        for(int i=0;i<out.length;i++){
            if(out[i]>=maxx){
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
                    System.out.println("realFreq"+realFreq);

                    information.add(realFreq>10000?1:0);
                }
            }

        }
    }
}

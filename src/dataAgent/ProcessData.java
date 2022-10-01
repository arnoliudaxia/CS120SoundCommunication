package dataAgent;

import OSI.Link.frameConfig;
import com.github.psambit9791.jdsp.filter.Butterworth;
import com.github.psambit9791.jdsp.misc.UtilMethods;
import com.github.psambit9791.jdsp.signal.CrossCorrelation;
import com.github.psambit9791.jdsp.transform.FastFourier;
import com.github.psambit9791.jdsp.transform._Fourier;
import utils.smartConvertor;

import java.util.ArrayList;
import java.util.LinkedList;


public class ProcessData implements CallBackStoreData {

    private double[] kernel;
    private final float[] header=frameConfig.header;
    private double[] signals;
    private double isHeader=10;
    private int dataBegin;
    private final int bitLength = frameConfig.bitLength;
    private final int headerLength = 440;
    private int timelength;
    private  float fragmentTime=frameConfig.fragmentTime;
    private float samplingRate;
    private int fragmentLength;
    public ArrayList<Float> check=new ArrayList<>();
    public ArrayList<Float> allData = new ArrayList<>();
    public ArrayList<Float> timeDomain=new ArrayList<>();

    public ArrayList<Float> fft=new ArrayList<>();

    private int howmanyBitsRead=0;

    private int flag=0;


    private double[] outt=new double[512];
    public ProcessData(int sampleFre){
        this.samplingRate=sampleFre;
        this.fragmentLength= (int)(samplingRate*fragmentTime);
    }
    @Override
    public void storeData(float[] data){
        double[] s = new double[data.length];
        for(int i=0;i<data.length;i++){
            s[i]=Double.parseDouble(String.valueOf(data[i]));
        }
        int order = 4; //order of the filter
        int lowCutOff = 2000; //Lower Cut-off Frequency
        int highCutOff = 12000; //Higher Cut-off Frequency
        Butterworth flt = new Butterworth(samplingRate); //signal is of type double[]
        double[] signals = flt.bandPassFilter(s, order, lowCutOff, highCutOff);

//        _Fourier dft = new FastFourier(signals); //Works well for longer signals (>200 points)
//        dft.transform();
//        boolean onlyPositive = true;
//        double[] ff = dft.getMagnitude(onlyPositive);
//        float[] fff=smartConvertor.doubleToFloatArray(ff);
//        for(int i=0;i<fff.length;i++)fft.add(fff[i]);

        double[] dataa= UtilMethods.concatenateArray(outt,signals);
        for(int i=0;i<signals.length;i++)outt[i]=signals[i];
        if(flag==0) {
            kernel = smartConvertor.floatToDoubleArray(header);
            String mode = "valid";//数目相同对应乘积
            CrossCorrelation cc = new CrossCorrelation(dataa, kernel);
            double[] out = cc.crossCorrelate(mode);
            double maxx = -9999;
            double sum = 0;
            for (double v : out) {
                maxx = Math.max(maxx, v);
                sum += v;
            }
            check.add((float)(maxx));
            double mean = (sum - maxx) / (out.length - 1);
            for (int i = 0; i < out.length; i++) {
                if ((out[i]) >= isHeader) {
                    dataBegin = i + headerLength;
                    flag = 1;
                    break;
                }
            }
        }
        else {
            while(true)
            {
                if(howmanyBitsRead>=10)
                {
                    flag=0;
                    break;
                }

                for(int i=0;i<fragmentLength;i++)timeDomain.add((float)dataa[i]);
                howmanyBitsRead++;
                dataBegin+=fragmentLength;
                if(dataBegin>=1024)
                {
                    dataBegin+=512+(1024-dataBegin)-fragmentLength;
                    break;
                }
            }
        }

//        if(dog==true){
//            double mymax=-99,mymin=99;
//            for(int i=0;i<check.size();i++){
//                mymax=Math.max(check.get(i),mymax);
//                mymin=Math.min(mymin,check.get(i));
//            }
//            double cat=100/(mymax-mymin);
//            for(int i=0;i<check.size();i++){
//                check.set(i,(check.get(i)-mymin)*cat);
//            }
//        }

//        //一个包没传完
//        if (timeDomain.size() % (bitLength*fragmentLength) != 0) {
//            if(((bitLength*fragmentLength)-timeDomain.size())<=data.length) {
//                //System.arraycopy(data, 0, timeDomain, timelength, ((bitLength * fragmentLength) - timeDomain.length));
//                for(int i=0;i<((bitLength * fragmentLength) - timeDomain.size());i++)timeDomain.add(data[i]);
//            }else{
//                //System.arraycopy(data, 0, timeDomain, timelength, data.length);
//                for(int i=0;i<data.length;i++)timeDomain.add(data[i]);
//            }
//        }
//        ///找到头，开始传
//        if(flag==1) {
//            if (timeDomain.size() == 0) timelength = 0;
//            if (data.length - dataBegin < bitLength*fragmentLength) {
//                //System.arraycopy(data, dataBegin,timeDomain, timelength, data.length - dataBegin);
//                timelength += data.length - dataBegin;
//                for(int i=dataBegin;i<data.length;i++)timeDomain.add(data[i]);
//            } else  {
//                //System.arraycopy(data, dataBegin,timeDomain, timelength, bitLength*fragmentLength);
//                timelength+=bitLength*fragmentLength;
//                for(int i=dataBegin;i<dataBegin+(bitLength*fragmentLength);i++)timeDomain.add(data[i]);
//            }
//        }

        for (float datum : data) {
            allData.add(datum);
        }
    }


    @Override
    public LinkedList<float[]> retriveData(int fragment){
        LinkedList<float[]> result =new LinkedList<>();
        float[] signal = new float[fragmentLength];
        int num=0;
        while(timelength>0){
            if (fragmentLength >= 0) System.arraycopy(timeDomain, 0 + num * fragmentLength, signal, 0, fragmentLength);
            result.add(signal);
            num++;
        }
        return result;
    }

    public void demodulation(LinkedList<float[]> retriveSignals){

    }



}

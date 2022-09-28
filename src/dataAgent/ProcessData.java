package dataAgent;

import OSI.Link.frameConfig;
import com.github.psambit9791.jdsp.misc.UtilMethods;
import com.github.psambit9791.jdsp.signal.CrossCorrelation;
import utils.smartConvertor;

import java.util.ArrayList;
import java.util.LinkedList;


public class ProcessData implements CallBackStoreData {

    private double[] header;
    private final float[] kernel=frameConfig.header;
    private double[] signals;
    private double isHeader=frameConfig.isHeader64;
    private int dataBegin;
    private final int bitLength = frameConfig.bitLength;
    private final int headerLength = frameConfig.headerLength;
    private float[] timeDomain;
    private int timelength;
    private  float fragmentTime=frameConfig.fragmentTime;
    private float samplingRate;
    private int samplesNum;
    public ArrayList<Float> check=new ArrayList<>();
    public ArrayList<Float> allData = new ArrayList<>();

    private double[] outt=new double[512];
    public ProcessData(int sampleFre){
        this.samplingRate=sampleFre;
        this.samplesNum= (int)(samplingRate*fragmentTime);//fragmentLength
    }
    @Override
    public void storeData(float[] data){
        int flag=0;

        signals = new double[data.length];
        for(int i=0;i<data.length;i++){
            signals[i]=Double.parseDouble(String.valueOf(data[i]));
        }
        double[] dataa= UtilMethods.concatenateArray(outt,signals);
        header=smartConvertor.floatToDoubleArray(kernel);
        String mode = "valid";//数目相同对应乘积
        CrossCorrelation cc = new CrossCorrelation(dataa, header);
        double[] out = cc.crossCorrelate(mode);
        for(int i=0;i<out.length;i++){
            if(out[i]>=isHeader){
                dataBegin=i+headerLength;
                flag=1;
                break;
            }
        }
        for (float datum : data) {
            allData.add(datum);
        }
        double maxx=-9999;
        for (double v : out) {
            maxx = Math.max(maxx, v);
        }
        check.add((float)maxx);
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

        if (timeDomain.length % (bitLength*samplesNum) != 0) {
            System.arraycopy(data, 0,timeDomain, timelength, bitLength - (timeDomain.length % (bitLength*samplesNum)));
        }
        if(flag==1) {
            if (timeDomain.length == 0) timelength = 0;
            if (data.length - dataBegin < bitLength*samplesNum) {
                System.arraycopy(data, dataBegin,timeDomain, timelength, data.length - dataBegin);
                timelength += data.length - dataBegin;
            } else  {
                System.arraycopy(data, dataBegin,timeDomain, timelength, bitLength*samplesNum);
                timelength+=bitLength*samplesNum;
            }
        }
    }



    @Override
    public LinkedList<float[]> retriveData(int fragment){
        LinkedList<float[]> result =new LinkedList<>();
        float[] signal = new float[samplesNum];
        int num=0;
        while(timelength>0){
            if (samplesNum >= 0) System.arraycopy(timeDomain, 0 + num * samplesNum, signal, 0, samplesNum);
            result.add(signal);
            num++;
        }
        return result;
    }

    public void demodulation(LinkedList<float[]> retriveSignals){

    }



}

package dataAgent;

import OSI.Link.frameConfig;
import com.github.psambit9791.jdsp.misc.UtilMethods;
import com.github.psambit9791.jdsp.signal.CrossCorrelation;
import utils.smartConvertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class ProcessData implements CallBackStoreData {

    private final float[] header=frameConfig.header;
    private double[] headerD;
    private double[] signals;
    final private double HEADTHERSHOLD =40;
    private int dataBegin;
    private final int bitLength = frameConfig.bitLength;
    private final int headerLength = 440;
    private int timelength;
    private int samplingRate;
    public ArrayList<Float> check=new ArrayList<>();
    public ArrayList<Float> allData = new ArrayList<>();
    public ArrayList<Double> bitWave =new ArrayList<>();

    public ArrayList<Float> fft=new ArrayList<>();

    private int howmanyBitsRead=0;

    private boolean isDatafield =false;
    private int readBitIndex=0;


    private double[] outt=new double[512];
    public ProcessData(int sampleFre){
        this.samplingRate=sampleFre;
        headerD=smartConvertor.floatToDoubleArray(header);
    }
    @Override
    public void storeData(float[] data){
        //s is the doble format of data
        double[] dataD = new double[data.length];
        for(int i=0;i<data.length;i++){
            dataD[i]=Double.parseDouble(String.valueOf(data[i]));
        }
        //记录波形数据
        Arrays.stream(dataD).forEach(x->allData.add((float)x));

        //signals 是原来的data经过一个带通滤波后的结果
//        double[] signals= SoundUtil.bandPassFilter(dataD,samplingRate,1500,12000);
        double[] signals=dataD;

//        _Fourier dft = new FastFourier(signals); //Works well for longer signals (>200 points)
//        dft.transform();
//        boolean onlyPositive = true;
//        double[] ff = dft.getMagnitude(onlyPositive);
//        float[] fff=smartConvertor.doubleToFloatArray(ff);
//        for(int i=0;i<fff.length;i++)fft.add(fff[i]);

        double[] concatenatedWave;
        System.arraycopy(signals,0,outt,0,signals.length);
        if(!isDatafield) {
            concatenatedWave = UtilMethods.concatenateArray(outt,signals);
            CrossCorrelation cc = new CrossCorrelation(concatenatedWave, headerD);
            double[] out = cc.crossCorrelate("valid");
            double maxx =Arrays.stream(out).max().getAsDouble();
            check.add((float)(maxx));
            if (maxx > HEADTHERSHOLD) {
                for(int i=0;i<out.length;i++){
                    if(Math.abs(out[i]-maxx)<0.01f){
                        dataBegin=i + headerLength;
                        System.out.println("dataBegin"+dataBegin);
                        break;
                    }
                }
                isDatafield = true;
                howmanyBitsRead=0;
                System.out.println("发现header");
            }

        }
        if(isDatafield) {
            concatenatedWave=signals;
            while(dataBegin<concatenatedWave.length)
            {
                bitWave.add(concatenatedWave[dataBegin]);
                readBitIndex++;
                dataBegin++;
                if(readBitIndex>=frameConfig.fragmentLength)
                {
                    howmanyBitsRead++;
                    if(howmanyBitsRead>=frameConfig.bitLength)
                    {
                        isDatafield =false;
                        System.out.println("读取Frame完毕");
                        for(int i=0;i<1000;i++){
                            bitWave.add(0.0);
                        }
                        break;
                    }
                    readBitIndex=0;
                }
            }
            dataBegin=0;
        }

    }



    @Override
    public LinkedList<float[]> retriveData(int fragment){
//        LinkedList<float[]> result =new LinkedList<>();
//        float[] signal = new float[fragmentLength];
//        int num=0;
//        while(timelength>0){
//            if (fragmentLength >= 0) System.arraycopy(bitWave, 0 + num * fragmentLength, signal, 0, fragmentLength);
//            result.add(signal);
//            num++;
//        }
//        return result;
        return null;
    }

    public void demodulation(LinkedList<float[]> retriveSignals){

    }



}

package OSI.Physic;

import com.synthbot.jasiohost.AsioChannel;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioDriverListener;
import com.synthbot.jasiohost.AsioDriverState;
import dataAgent.CallBackStoreData;
import dataAgent.LocalTempFile;
import dataAgent.MemoryData;
import dataAgent.StorgePolicy;
import utils.SoundUtil;

import java.util.*;


/**
 * 该层的作用就是简单的收发声音，具体来说，只要isRecording，就会通过CallBackStoreData接口把收到的数据传到上一层。
 * 只要isPlay，就会把playQueue这个FIFO里的信号发出去
 */
public class AudioHw implements AsioDriverListener {
    public static AudioHw audioHwG;
    //驱动层
    private AsioDriver asioDriver;
    //控制区
    public boolean isPlay = false;
    public boolean isRecording = false;
    public float volume=0.7f;
    public int sampleFrequency=44100;

    //数据流
    private final int bufferSize=512;
    private float[] inBuffer;
    public CallBackStoreData dataagent = new MemoryData();
    public LinkedList<float[]> playQueue = new LinkedList<>();
    private int referenceNoiseMeasureIndex=0;
    public double referenceNoise=1f;
    private float inputAmplify=1f;

    private int preheat=0;

    public void init(int sampleFre) {
        sampleFrequency= sampleFre;
        Set<AsioChannel> activeChannels = new HashSet<AsioChannel>();  // create a Set of AsioChannels

        if (asioDriver == null) {
            List<String> driverNameList = AsioDriver.getDriverNames();
            for (var driverName : driverNameList) {
                System.out.println("Driver: " + driverName);
            }
            asioDriver = AsioDriver.getDriver("ASIO4ALL v2");
            asioDriver.addAsioDriverListener(this);   // add an AsioDriverListener in order to receive callbacks from the driver
            System.out.println("------------------");
            System.out.println("Output Channels");
            for (int i = 0; i < asioDriver.getNumChannelsOutput(); i++) {
                System.out.println(asioDriver.getChannelOutput(i));
            }
            System.out.println("------------------");
            System.out.println("Input Channels");
            for (int i = 0; i < asioDriver.getNumChannelsInput(); i++) {
                System.out.println(asioDriver.getChannelInput(i));
            }
            System.out.println("------------------");

            inBuffer = new float[bufferSize];//数组长度固定为buffer大小

            asioDriver.setSampleRate(sampleFrequency);//确定采样率
            /*
             * buffer size should be set either by modifying the JAsioHost source code or
             * configuring the preferred value in ASIO native window. We choose 128 i.e.,
             * asioDriver.getBufferPreferredSize() should be equal to sampleFrequency
             * = 128;
             *
             */

            activeChannels.add(asioDriver.getChannelOutput(0));
            activeChannels.add(asioDriver.getChannelInput(0));
            asioDriver.createBuffers(activeChannels);  // create the audio buffers and prepare the driver to run
            System.out.println("ASIO buffer created, size: " + asioDriver.getBufferPreferredSize());

        }
    }

    public void start() {
        if (asioDriver != null) {
            asioDriver.start();  // start the driver
            System.out.println(asioDriver.getCurrentState());
        }
    }

    public void stop() {
        asioDriver.returnToState(AsioDriverState.INITIALIZED);
        asioDriver.shutdownAndUnloadDriver();  // tear everything down
    }

    public static void initAudioHw() {
        AudioHw.audioHwG=new AudioHw();
        AudioHw.audioHwG.init(48000);
        AudioHw.audioHwG.start();
    }


    @Override
    public void bufferSwitch(final long systemTime, final long samplePosition, final Set<AsioChannel> channels) {

        for (AsioChannel channelInfo : channels) {
            if (isPlay && !channelInfo.isInput()) {
                if (playQueue.size() > 0) {
                    channelInfo.write(SoundUtil.amplify(playQueue.pop(), volume));
                }
                else {
                    channelInfo.write(new float[bufferSize]);
                    isPlay = false;
                    System.out.println("播放完成");
//                    System.out.println("用时" + TimerCounter.stopTimer("SendTimer"));
                }

            }
            if (isRecording && channelInfo.isInput()) {

                channelInfo.read(inBuffer);
                dataagent.storeData(inBuffer);


            }
        }

    }

    @Override
    public void latenciesChanged(final int inputLatency, final int outputLatency) {
        System.out.println("latenciesChanged() callback received.");
    }

    @Override
    public void bufferSizeChanged(final int bufferSize) {
        System.out.println("bufferSizeChanged() callback received.");
    }

    @Override
    public void resetRequest() {
        /*
         * This thread will attempt to shut down the ASIO driver. However, it will block
         * on the AsioDriver object at least until the current method has returned.
         */
        new Thread() {
            @Override
            public void run() {
                System.out.println("resetRequest() callback received. Returning driver to INITIALIZED state.");
                asioDriver.returnToState(AsioDriverState.INITIALIZED);
            }
        }.start();
    }

    @Override
    public void resyncRequest() {
        System.out.println("resyncRequest() callback received.");
    }

    @Override
    public void sampleRateDidChange(final double sampleRate) {
        System.out.println("sampleRateDidChange() callback received.");
    }

    public void changeStorgePolicy(StorgePolicy policy){
        assert false;
        switch (policy) {
            case MEMORY -> dataagent = new MemoryData();
            case FILE -> dataagent = new LocalTempFile();
        }
    }

    public int playRawData(float[] rawdata){
        if(rawdata.length == bufferSize){
            playQueue.add(rawdata);
            return 1;
        }
        if(rawdata.length< bufferSize){
            float[] temp = new float[bufferSize];
            System.arraycopy(rawdata, 0, temp, 0, rawdata.length);
            playQueue.add(temp);
            return 1;
        }
        int count = rawdata.length/bufferSize;
        for(int i = 0;i<count;i++){
            float[] temp = new float[bufferSize];
            System.arraycopy(rawdata, i*bufferSize, temp, 0, bufferSize);
            playQueue.add(temp);
        }
        //处理尾部
        return count+playRawData(Arrays.copyOfRange(rawdata, count*bufferSize, rawdata.length));
    }

        public void playSound(LinkedList<float[]> sound){
        playQueue = sound;
    }


}




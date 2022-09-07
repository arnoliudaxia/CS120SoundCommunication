import com.synthbot.jasiohost.*;
import dataAgent.CallBackStoreData;
import dataAgent.MemoryData;

import javax.security.auth.callback.Callback;
import java.io.*;
import java.util.*;

public class AudioHw implements AsioDriverListener {
    //驱动层
    private AsioDriver asioDriver;
    //控制区
    public boolean isPlay = false;
    public boolean isRecording = false;

    //数据流
    private float[] inBuffer;
    CallBackStoreData dataagent = new MemoryData();
    LinkedList<float[]> playQueue = new LinkedList<>();

    public void init() {
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

            inBuffer = new float[Config.HW_BUFFER_SIZE];

            asioDriver.setSampleRate(Config.PHY_TX_SAMPLING_RATE);
            /*
             * buffer size should be set either by modifying the JAsioHost source code or
             * configuring the preferred value in ASIO native window. We choose 128 i.e.,
             * asioDriver.getBufferPreferredSize() should be equal to Config.HW_BUFFER_SIZE
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

    @Override
    public void bufferSwitch(final long systemTime, final long samplePosition, final Set<AsioChannel> channels) {

        for (AsioChannel channelInfo : channels) {
            if (isPlay && !channelInfo.isInput()) {
                if (playQueue.size() > 0) {
                    channelInfo.write(playQueue.pop());
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


}




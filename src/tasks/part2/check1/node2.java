package tasks.part2.check1;

import OSI.Application.DeviceSettings;
import OSI.Application.GlobalEvent;
import OSI.Application.UserSettings;
import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;
import dataAgent.StorgePolicy;

public class node2 {

    public static void main(final String[] args) {
        //我的任务是要接受node1发来的数据然后发给node3
        AudioHw.initAudioHw();
        AudioHw.audioHwG.changeStorgePolicy(StorgePolicy.FrameRealTimeDetect);
        AudioHw.audioHwG.isRecording = true;
        MACLayer.initMACLayer();
        DeviceSettings.wakeupRef=0.1f;
        DeviceSettings.MACAddress = 1;
        UserSettings.Number_Frames_Trun=1;
        synchronized (GlobalEvent.ALL_DATA_Recieved) {
            try {
                GlobalEvent.ALL_DATA_Recieved.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

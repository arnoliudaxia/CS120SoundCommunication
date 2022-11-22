package OSI.Application;

import OSI.MAC.MACLayer;
import OSI.Physic.AudioHw;

public class SystemController {
    public static void shutdown() {
        System.out.println("系统关闭");
        if (AudioHw.audioHwG != null) {
            AudioHw.audioHwG.stop();
        }
        if (MACLayer.macBufferController != null) {
            MACLayer.macStateMachine.SIG = true;
        }

    }
}

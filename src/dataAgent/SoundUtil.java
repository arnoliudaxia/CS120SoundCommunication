package dataAgent;

import java.util.LinkedList;

public class SoundUtil {
    static void amplify(LinkedList<float[]> data) {
        float MaxPower = 0;

        for (float[] datum : data) {
            for (float d : datum) {
                MaxPower = Math.max(MaxPower, Math.abs(d));
            }
        }
        for (float[] datum : data) {
            for (int i = 0; i < datum.length; i++) {
                datum[i] *= 0.7 / MaxPower;
            }
        }
    }
}

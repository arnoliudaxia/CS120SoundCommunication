package OSI.Application;

import OSI.IP.IPv4;

/**
 * 储存设备异性设置
 */
public class DeviceSettings {
    public static int MACAddress=-1;
    public static float wakeupRef=-1.f;
    public static IPv4 IP;
    public static boolean isSendEndPackage=true;

    @FunctionalInterface
    public interface Function3 <A, B,C, R> {
        //R is like Return, but doesn't have to be last in the list nor named R.
        public R apply (A a, B b,C c);
    }
    public static Function3<Integer,Integer,Integer, Boolean> stopPackageJudge=(seq, crc,frametype)->{
        return false;
    };
}

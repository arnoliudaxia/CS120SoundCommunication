package utils;

import java.text.SimpleDateFormat;

public class DebugHelper {
    static SimpleDateFormat df = new SimpleDateFormat("mm:ss.SSS");
    public static <T> void log(T message) {
        System.out.println(df.format(System.currentTimeMillis())+": "+message);
    }
    public static <T> void logErr(T message) {
        System.err.println(df.format(System.currentTimeMillis())+": "+message);
    }
}

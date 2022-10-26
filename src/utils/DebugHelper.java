package utils;

import java.text.SimpleDateFormat;

public class DebugHelper {
    static SimpleDateFormat df = new SimpleDateFormat("mm:ss");
    public static <T> void log(T message) {
        System.out.println(df.format(System.currentTimeMillis())+": "+message);
    }
}

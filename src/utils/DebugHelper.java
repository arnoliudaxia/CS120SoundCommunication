package utils;

import java.text.SimpleDateFormat;

public class DebugHelper {
    static SimpleDateFormat df = new SimpleDateFormat("mm:ss");
    public static void log(String message) {
        System.out.println(df.format(System.currentTimeMillis())+": "+message);
    }
}

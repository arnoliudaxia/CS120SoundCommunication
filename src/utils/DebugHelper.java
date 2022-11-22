package utils;

import java.text.SimpleDateFormat;

public class DebugHelper {
    static SimpleDateFormat df = new SimpleDateFormat("mm:ss.SSS");
    public enum printColor{
        RED("\33[31m"),GREEN("\33[32m"),YELLOW("\33[33m"),BLUE("\33[34m"),PURPLE("\33[35m"),CYAN("\33[36m"),WHITE("\33[37m"),BLACK("\33[30m"),BLANK("\33[m");
        private String color;
        printColor(String color){
            this.color=color;
        }
        public String getColor(){
            return color;
        }
    }
    public static <T> void log(T message) {
        System.out.println(df.format(System.currentTimeMillis())+": "+message);
    }
    public static <T> void logErr(T message) {
        System.err.println(df.format(System.currentTimeMillis())+": "+message);
    }
    public static <T> void logColorful(T message,printColor color) {
        System.out.println(color.getColor()+df.format(System.currentTimeMillis())+": "+message+printColor.BLANK.getColor());
    }

}

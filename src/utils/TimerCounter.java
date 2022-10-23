package utils;

import java.util.HashMap;
import java.util.Map;

public class TimerCounter {
    public static Map<String, TimerCounter> timers=new HashMap<>();
    public double timePast=0.0;
    public Thread timePastThread;
    public boolean stopNotify=false;

    public static void startTimer(String name)
    {
        TimerCounter timerCounter =new TimerCounter();
        timerCounter.timePastThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (!timerCounter.stopNotify)
                {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timerCounter.timePast+=.01;
                }
            }
        });
        timerCounter.timePastThread.start();
        timers.put(name, timerCounter);
    }
    public static double resetTimer(String name)
    {
        double result=timers.get(name).timePast;
//        System.out.println("resetTimer: "+name+" "+timers.get(name).timePast);
        timers.get(name).timePast=0.0;
        return result;

    }
    public static double stopTimer(String name)
    {
        double result=timers.get(name).timePast;
//        System.out.println("stopTimer: "+name+" "+timers.get(name).timePast);
        timers.get(name).stopNotify=true;
        timers.remove(name);
        return result;
    }


}

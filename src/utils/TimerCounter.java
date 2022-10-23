package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 集群计时器的实现，注意到外部之后使用该类的静态成员，而不是实例成员
 * timers是一个TimerCounter的集群，外部通过函数接口和Timer的Name来访问
 */
public class TimerCounter {
    public static Map<String, TimerCounter> timers=new HashMap<>();
    /**
     * 你不应该使用该变量
     */
    public double timePast=0.0;
    /**
     * 你不应该使用该变量
     */
    public Thread timePastThread;
    /**
     * 你不应该使用该变量
     */
    public boolean stopNotify=false;
    //TODO 后期可能需要一个高精度的计时器，目前计时器的精度是10ms，目的是为了尽量不影响性能

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
        timerCounter.timePastThread.setDaemon(true);
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

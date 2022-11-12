package OSI.Link;

/**
 * 该类用于定义frame的物理属性
 */
public class frameConfig {
    /**
     * 一个frame包含的bit数
     */
    public static final int bitLength = 200;
    /**
     * 一个bit的物理长度,以s为单位
     */
    public static final float fragmentTime = 0.00011f;
    /**
     * 一个bit对应多少采样点
     */
    public static final int bitSamples=5;
    /**
     * 一个bit的物理长度,以采样点为单位
     */
    public static int fragmentLength=(int) (frameConfig.fragmentTime * 48000);;
    /**
     * header的采样点数量
     */
    public static float[] digitalHeader={1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0};
    public static int digitalHeaderLength=digitalHeader.length;

}

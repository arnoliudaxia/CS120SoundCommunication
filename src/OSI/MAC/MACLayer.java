package OSI.MAC;

/**
 * 将MAC层的机制全部封装在这个类里面，其他层只需要关注着一个类即可。<br>
 * 注意MACLayer类是“虚拟的”，实际不应该存在一个MACLayer对象。
 * 它只是将MAC层的相关东西全部封装在一起了而已。
 * 故为了避免误用，MACLayer为一个抽象类。
 */
public abstract class MACLayer {
    public static volatile boolean isChannelReady = true;
    public static void initMACLayer()
    {
        new MACBufferController();
        new MACStateMachine();
    }
    public static MACStateMachine macStateMachine;
    public static MACBufferController macBufferController;


}

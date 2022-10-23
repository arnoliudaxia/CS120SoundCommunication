package OSI.Link;

public interface FrameDetection
{
    /**
     * 判断data数组里是否**可能**存在一个frame
     * @return 如果可能存在返回True
     */
    public boolean detectPossibleFrame(float[] data);
}

package utils;

public class smartConvertor {
    public static float[] doubleToFloatArray(double[] input)
    {
        float[] output=new float[input.length];
        for (var i=0;i<input.length;i++)
        {
            output[i]=(float)input[i];
        }
        return output;
    }
}

package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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
    public static double[] floatToDoubleArray(float[] input)
    {
        double[] output=new double[input.length];
        for (var i=0;i<input.length;i++)
        {
            output[i]=(double) input[i];
        }
        return output;
    }

    /**
     * 如果数据以二进制形式存储在一个文本文件的一行，请调用我嘿嘿
     * @param path 文本文件路径
     * @return 一个ArrayList<Integer>
     * @throws FileNotFoundException
     */
    public static ArrayList<Integer> binInTextFile(String path) throws FileNotFoundException {
        ArrayList<Integer> rawdata = new ArrayList<>();
        File f = new File(path);
        Scanner sc = new Scanner(f);
        var rawstring = sc.nextLine();
        for (int i = 0; i < rawstring.length(); i++) {
            {
                rawdata.add(Integer.parseInt(String.valueOf(rawstring.charAt(i))));
            }
        }
        return rawdata;
    }
}

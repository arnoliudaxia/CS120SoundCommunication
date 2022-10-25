package utils;

import java.io.*;
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
    public static ArrayList<Integer> binInFile(String path) throws FileNotFoundException {
        String result="";
        ArrayList<Integer> resultD = new ArrayList<>();
        try{
            File f = new File(path);
            DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
            ArrayList<Byte> buffer = new ArrayList<>();
            while(is.available()>0){
                buffer.add(is.readByte());
            }
            for (byte a : buffer) {
                String temp = "";
                for (int i = 0; i < 8; i++) {
                    byte b = a;
                    a = (byte) (a >> 1);//每移一位如同将10进制数除以2并去掉余数。
                    a = (byte) (a << 1);
                    if (a == b) {
                        temp = "0" + temp;
                    } else {
                        temp = "1" + temp;
                    }
                    a = (byte) (a >> 1);
                }
                result += temp;
            }
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        for (int i = 0; i < result.length(); i++) {
            var character=result.charAt(i);
            resultD.add(character=='0'?0:1);
        }
        return resultD;
    }
    public static void binToFile(String path,ArrayList<Integer> input)
    {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
            for (int i = 0; i < input.size(); i+=8) {
                byte c=0;
                for (int j = 0; j < 7; j++) {
                    c+=input.get(i+j);
                    c<<=1;
                }
                c+=input.get(i+7);

                out.writeByte(c);
            }
            out.close();
            DebugHelper.log("写入文件:"+path);
        } catch (IOException e) {
        }
    }
}

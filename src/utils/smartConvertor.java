package utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

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
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> exactBitsOfNumber(int number,int bitLength){
        int mask=0b1<<(bitLength-1);
        ArrayList<Integer> result=new ArrayList<>();
        for (int i = 0; i < bitLength; i++) {
            result.add((number & mask) > 0 ? 1 : 0);
            mask=mask>>1;
        }
        return result;
    }
    public static int mergeBitsToInteger(List<Integer> input){
        int result=0;
        int mask=0b1<<(input.size()-1);
        for (Integer integer : input) {
            result += mask * integer;
            mask = mask >> 1;
        }
        return result;
    }
    public static String receivePayload (String frame){
        Random random=new Random();
        byte[] payload=new byte[56];
        boolean ischeck=false;
        ArrayList<Integer> data= new ArrayList<>(200);
        var rawdata=data.subList(0,32);
        if(ischeck) {
            ArrayList<Integer> IP = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                IP.add(mergeBitsToInteger(rawdata.subList(j * 8, (j + 1) * 8)));
            }
            String receiveIP = IP.stream().map(Object::toString).collect(Collectors.joining("."));
//        var payload=data.subList(32,data.size());
            byte[] bytes = new byte[2048];
            for (int i = 0; i < data.size() - 8; i += 8) {
                bytes[i / 8] = (byte) smartConvertor.mergeBitsToInteger(data.subList(i, i + 8));
            }
            String s = new String(bytes, 0, bytes.length, Charset.defaultCharset());
            int endindex;
            if ((endindex = s.lastIndexOf('ç')) != -1) {
                s = s.substring(0, endindex);
            }
        }
        else {
            random.nextBytes(payload);
            return "PmWMBhmoK5PzGJ4QVaWEoh7WnUTYOeRbZ3DixmLsti9FlW7Awyqzef0Z";

        }

        return new String(payload, Charset.defaultCharset());
    }
}

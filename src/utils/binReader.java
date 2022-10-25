package utils;

import java.io.*;
import java.util.ArrayList;

public class binReader {
    public ArrayList<Byte> buffer = new ArrayList<>();
    public String result="";
    public void bin2Float(){
        try{
            File f = new File("res\\INPUT.txt");
            DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
            while(is.available()>0){
                buffer.add(is.readByte());
            }
            for(int j=0;j< buffer.size();j++){
                byte a = buffer.get(j); ;
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
                result+=temp;
            }
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

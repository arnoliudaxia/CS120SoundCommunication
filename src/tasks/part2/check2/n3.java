package tasks.part2.check2;

import utils.DebugHelper;
import utils.ReadTxt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class n3 {
    public static void main (String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("等待连接服务端！");
        Socket socket = new Socket("localhost", 1111);
        System.out.println("连接服务端成功！");
        OutputStream outputStream = socket.getOutputStream();
        ArrayList<String> inputLines=new ArrayList<>();
        try{
            inputLines= ReadTxt.readTxtLines("res\\INPUT.txt");
        }catch(IOException e){
            throw new IOException(e);
        }
        DebugHelper.log("读取input成功");
        StringBuilder s= new StringBuilder();
        for(int i=0;i<inputLines.size();i++){
            s.append(inputLines.get(i));
            s.append("ç");
        }
//        String s=readTxt("res\\INPUT.txt")+"ç";
        byte[] b= s.toString().getBytes("utf-8");
        outputStream.write(b);
    }
}

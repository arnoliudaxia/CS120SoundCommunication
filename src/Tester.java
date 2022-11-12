import utils.smartConvertor;

import java.io.IOException;
import java.util.ArrayList;

public class Tester {
    public static void main(final String[] args) throws IOException {
        ArrayList<Integer> data=new ArrayList<>();
        //random 0 or 1
        for (int i=0;i<6250*8/2;i++)
        {
            boolean choice=Math.random()>0.5;
            if(choice){
                data.add(1);
                data.add(0);
            }
            else{
                data.add(0);
                data.add(1);
            }
        }
        smartConvertor.binToFile("res\\INPUT_6250.bin",data);
    }
}
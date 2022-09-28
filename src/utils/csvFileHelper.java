package utils;

import com.github.psambit9791.jdsp.io.CSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class csvFileHelper {
        CSV writeObj = new CSV(','); //comma is the separator
    public void saveToCsv(String path, ArrayList<Float> data) throws IOException {
        HashMap< String, ArrayList< Object>> result = new HashMap<>();
        result.put("col1", new ArrayList<>(Collections.singletonList(data)));
        writeObj.writeCSV(path, result);
    }
    public float[] readCsv(String path) throws IOException {
        CSV readObj = new CSV(',');
        HashMap< String, ArrayList< Object>> out = readObj.readCSV(path, true);
//        return out.get("col1").toArray(float[]::new);
        var readresult = out.get("col1").toArray(Float[]::new);
        float[] result=new float[readresult.length];
        for(int i=0;i<readresult.length;i++)
        {
            result[i]=readresult[i];
        }
        return result;
    }
}

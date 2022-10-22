package utils;

import com.github.psambit9791.jdsp.io.CSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class csvFileHelper {
        CSV csvobj = new CSV(','); //comma is the separator
    public void saveToCsv(String path, List<Float> data) throws IOException {
        HashMap< String, ArrayList< Object>> result = new HashMap<>();
        result.put("col1", new ArrayList<>(data));
        csvobj.writeCSV(path, result);
        System.out.println("保存到"+path+"成功");
    }
    public void saveToCsvD(String path, List<Double> data) throws IOException {
        saveToCsvD(path,data,"col1");
    }
    public void saveToCsvD(String path, List<Double> data,String name) throws IOException {
        HashMap< String, ArrayList< Object>> result = new HashMap<>();
        result.put(name, new ArrayList<>(data));
        csvobj.writeCSV(path, result);
    }


    public Float[] readCsv(String path) throws IOException {
        HashMap< String, ArrayList< Object>> out = csvobj.readCSV(path, true);
//        return out.get("col1").toArray(float[]::new);
        var readresult = out.get("col1").stream().map(x->Float.parseFloat(x.toString())).toArray(Float[]::new);
        return readresult;
    }
}

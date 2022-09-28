package utils;

import com.github.psambit9791.jdsp.io.CSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class csvFileHelper {
        CSV writeObj = new CSV(','); //comma is the separator
    public void saveToCsv(String path, ArrayList<float[]> data) throws IOException {
        HashMap< String, ArrayList< Object>> result = new HashMap< String, ArrayList< Object>>();
        result.put("col1", new ArrayList< Object>(Collections.singletonList(data)));
        writeObj.writeCSV(path, result);
    }
}

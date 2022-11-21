package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadTxt {
    public void readTxt(String path) throws IOException {
        //read file
        String data = Files.readString(Paths.get(path));
        System.out.println(data);
    }
}

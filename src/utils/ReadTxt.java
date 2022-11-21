package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadTxt {
    public String readTxt(String path) throws IOException {
        //read file
        //        System.out.println(data);
        return Files.readString(Paths.get(path));
    }

    public byte[] readTxtBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
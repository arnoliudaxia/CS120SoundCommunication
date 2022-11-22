package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReadTxt {
    public static String readTxt(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }

    public static byte[] readTxtBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static ArrayList<String> readTxtLines(String path) throws IOException {
        return (ArrayList<String>) Files.readAllLines(Paths.get(path));
    }
}
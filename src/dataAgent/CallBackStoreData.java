package dataAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public interface CallBackStoreData {
    void storeData(float[] data);

    LinkedList<float[]> retriveData(int fragmentSize);
}
package dataAgent;

import java.io.*;
import java.util.LinkedList;

public class LocalTempFile implements CallBackStoreData {

    private BufferedOutputStream tempBufferInFile;

    private ObjectOutputStream outputS;
    private File tempfile;

    private void prepareToWrite(){
        try {
            tempfile=File.createTempFile("temp",".dat");
            tempBufferInFile = new BufferedOutputStream(new FileOutputStream(tempfile));
            outputS = new ObjectOutputStream(tempBufferInFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void releaseFile()
    {
        try{
            outputS.flush();
            outputS.close();
            tempBufferInFile.flush();
            tempBufferInFile.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public LocalTempFile() {
        prepareToWrite();
    }

    @Override
    public void storeData(float[] data) {
        for (float datum : data) {
            try{
                outputS.writeFloat(datum);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public LinkedList<float[]> retriveData(int fragmentSize) {
        releaseFile();

        LinkedList<float[]> result = null;
        ByteArrayInputStream deSerializedStream=null;
        try {
            result= SoundUtil.getSoundFromSerializedByte(new FileInputStream(tempfile).readAllBytes(),fragmentSize);

        }catch (IOException e){
            e.printStackTrace();
        }
        prepareToWrite();
        return result;
    }
}


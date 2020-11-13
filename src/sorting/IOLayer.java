package sorting;

import java.io.*;
import java.nio.ByteBuffer;

public class IOLayer {

    IOLayer(){
    }

    public static byte [] float2ByteArray (float value)
    {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    // read record from file declared in constructor
    public Record readRecord(long index, String filePath){
        float []params = new float[5];
        boolean readed = false;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "r");
            raf.seek(index*5*Float.BYTES);
            byte[] buffer = new byte[5*Float.BYTES];
            raf.read(buffer);
            if(notNullArray(buffer)) {
                params = byte2floatArray(buffer);
                readed = true;
            }
        }catch(Exception e){
            System.out.println("An error occurred while reading from file.");
            e.printStackTrace();
        }finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    System.err.println("Exception:" + e.toString());
                }
            }
        }
        if(readed)
            return new Record(params);
        else
            return null;
    }


    public void writeRecord(String filePath, Record record){
        try{
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");

            //raf.write(record.paramByteArray(),seek*record.size,record.size);
            raf.seek(raf.length());
            raf.write(record.paramByteArray());

            raf.close();
        }catch(Exception e){
            System.out.println("An problem occur in IOLayer while writing!");
            e.printStackTrace();
        }

    }

    public void deleteRecords(String filePath){
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write("");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    float[] byte2floatArray(byte[] _params){
        ByteBuffer bb = ByteBuffer.allocate(20);
        float [] t_params = new float[5];
        bb.put(_params);
        bb.rewind();
        for (int i = 0; i < 5; i++) {
            t_params[i]=bb.getFloat(i*Float.BYTES);
        }
        return t_params;
    }

    boolean notNullArray(byte[] array){
        for (byte var:array) {
            if(var!=0)
                return true;
        }
        return false;
    }

}

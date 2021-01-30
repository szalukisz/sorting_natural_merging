package sorting;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class IOLayer {

    private int pageSize;
    private String filePath;

    IOLayer(int _pageSize, String _filePath){
        pageSize = _pageSize;
        filePath = _filePath;
    }

    public static void deleteRecords(String filePath){
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write("");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public ArrayList<Record> readPage(long index ){
        ArrayList<Record> ret = new ArrayList<>();
        RandomAccessFile raf = null;
        try {
            float[] params;
            raf = new RandomAccessFile(filePath, "r");
            raf.seek(index*5*Float.BYTES*pageSize);
            byte[] buffer = new byte[5*Float.BYTES*pageSize];
            raf.read(buffer);
            for (int i = 0; i < pageSize; i++) {
                if(raf.length()!= 0 && (index*pageSize+i)*5*Float.BYTES<raf.length()) {
                    params = byte2floatArray(Arrays.copyOfRange(buffer, i * 5 * Float.BYTES, (i + 1) * 5 * Float.BYTES));
                    ret.add(new Record(params));
                }else{
                    ret.add(null);
                    break;
                }
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
        return ret;
    }

    public void writePage(ArrayList<Record> page){
        ByteBuffer bb = ByteBuffer.allocate(20*page.size());
        for (Record record: page) {
            bb.put(record.paramByteArray());
        }

        try{
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            raf.seek(raf.length()); // going to end of file
            raf.write(bb.array());
            raf.close();
        }catch(Exception e){
            System.out.println("An problem occur in IOLayer while writing!");
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

}

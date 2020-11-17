package sorting;

import java.util.ArrayList;

public class Tape {

    public String name;
    public int readsCounter=0,writesCounter=0;

    private ArrayList<Record> readBuffer, writeBuffer;
    private int readPageCounter=-1;
    private IOLayer ioLayer;
    private int pageSize;

    Tape(String _name, int _pageSize){
        name = _name;
        pageSize = _pageSize;
        ioLayer = new IOLayer(pageSize);
    }

    // delete content of tape(file)
    public void clearTape(){
        IOLayer.deleteRecords(name);
    }

    // save record in buffer if buffer is full, write it to file and clear buffer
    public void writeRecordOnTape(Record record){
        if(writeBuffer == null) {
            writeBuffer = new ArrayList<>();
        }
        if(writeBuffer.size()==pageSize-1) {
            writeBuffer.add(record);
            ioLayer.writePage(name, writeBuffer);
            writesCounter++;
            writeBuffer = new ArrayList<>();
        }else{
            writeBuffer.add(record);
        }
    }

    // read from buffer if not read next page from file
    public Record readRecordFromTape(long index){
        if(index/pageSize!=readPageCounter){
            readPageCounter++;
            readBuffer = ioLayer.readPage(readPageCounter,name);
            readsCounter++;
        }
        if((int)index%pageSize>=readBuffer.size())
            return null;
        else
            return readBuffer.get((int) index % pageSize);
    }

    // send left data to write to file, clear readBuffer
    public void flushTape(){
        if(writeBuffer!= null && writeBuffer.size()%pageSize!=0){
            ioLayer.writePage(name, writeBuffer);
            writesCounter++;
            writeBuffer = null;
        }
        if(readBuffer!= null){
            readBuffer = null;
            readPageCounter = -1;
        }
    }

    public void printTape(){
        int saveWrites = writesCounter, saveReads = readsCounter;  //save counters to doesn't count extra from printing
        int i=0;
        Record r;
        System.out.println("____________Tape: "+ name+"____________");
        while((r = this.readRecordFromTape(i++))!= null){
            System.out.println(r.toString());
        }
        if(i==1){System.out.println("                 empty");}
        writesCounter = saveWrites ;
        readsCounter = saveReads;
        readPageCounter=-1;
    }

}

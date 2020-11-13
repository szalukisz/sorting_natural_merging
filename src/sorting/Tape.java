package sorting;

import java.io.FileWriter;
import java.io.IOException;

public class Tape {

    private String name;
    Tape(String _name){
        name = _name;
    }

    // place record on the Tape
    public void placeOnTape(Record record){
        IOLayer ioLayer = new IOLayer();
        ioLayer.writeRecord(name, record);
    }

    public Record readFromTape(long index){
        IOLayer ioLayer = new IOLayer();
        return ioLayer.readRecord(index, name);
    }

    public void clearTape(){
        IOLayer ioLayer = new IOLayer();
        ioLayer.deleteRecords(name);
    }

    @Override
    public String toString() {

        return super.toString();
    }

}

package sorting;

import java.nio.ByteBuffer;

public class Record {

    private float a,y,c,z,x;
    private float [] params = new float[5];
    public int size = 5*Float.BYTES;


    Record(float _a,float _y,float _c,float _z,float _x){
        // a y c z x
        a = _a;
        y = _y;
        c = _c;
        z = _z;
        x = _x;
    }

    Record(float [] _params){
        // a y c z x
        params = _params;
        a = _params[0];
        y = _params[1];
        c = _params[2];
        z = _params[3];
        x = _params[4];
    }


    public float getKey(){
        return params[0];// here goes this weird function from quiz
    }

    @Override
    public String toString(){
        return String.format("%.6f %.6f %.6f %.6f %.6f",a,y,c,z,x).replace(',', '.');
    }

    public byte [] paramByteArray ()
    {
        ByteBuffer bb = ByteBuffer.allocate(20);
        for (float var:params) {
            bb.putFloat(var);
        }
        return bb.array();
    }

}

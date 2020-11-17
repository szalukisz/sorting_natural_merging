package sorting;

import java.nio.ByteBuffer;

public class Record {

    private float a,y,c,z,x;
    private float [] params;

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
        //return params[0];// here goes this weird function from quiz
        // should have add key value calculated in constructor
        return (float)(10*a*(Math.sin(Math.pow(x,2)+3*Math.pow(c,3)) - Math.cos(Math.pow(z,4)+5*Math.pow(y,7))));
    }

    @Override
    public String toString(){
        return String.format("%.6f %.6f %.6f %.6f %.6f",a,y,c,z,x).replace(',', '.');
    }

    public byte [] paramByteArray (){
        ByteBuffer bb = ByteBuffer.allocate(20);
        for (float var:params) {
            bb.putFloat(var);
        }
        return bb.array();
    }

}

package framework;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/*
 */
/**
 *
 * @author jhudson
 */
public class UnitSquare {
    Mesh m;
    
    public UnitSquare(){
        m = new Mesh("assets/usq.obj.mesh");
    }
    
    public void draw(Program p){
        m.draw(p);
    }
}

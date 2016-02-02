package framework.drawing;


/*
 */
/**
 *
 * @author jhudson
 */
public class UnitSquare implements Drawable
{
    Mesh m;
    
    public UnitSquare(){
        m = new Mesh("assets/usq.obj.mesh");
    }
    
    public void draw(Program p){
        m.draw(p);
    }
}

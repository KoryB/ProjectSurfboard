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
        m = new Mesh("assets/finished_meshes/usq.obj.mesh");
    }
    
    public void draw(Program p){
        m.draw(p);
    }
}

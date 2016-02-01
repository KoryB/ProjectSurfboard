package framework;

import framework.math3d.*;

/**
 * Created by Michael on 1/26/2016.
 */
public class Hallway {

    public Wall[] mWalls;
    public Floor mFloor;
    public vec4 mCenter;
    
    public Hallway(vec4 position, String hallFile){
        mCenter = position;
    }

    public void genHall(String roomFile){

    }
}

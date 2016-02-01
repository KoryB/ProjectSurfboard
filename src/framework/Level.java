package framework;

import framework.math3d.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Michael on 1/26/2016.
 */
public class Level {

    public ArrayList<Room> mRooms;
    public ArrayList<Hallway> mHallways;
    public vec2 mDimensions;

    public Level(vec2 dimensions,int numRooms){
        mRooms = new ArrayList<>();
        mHallways = new ArrayList<>();
        mDimensions = dimensions;
    }

    public void generate(int numRooms){
        //Create all rooms, make sure none overlap
        for(int i = 0; i < numRooms; i++){

        }

        //Create hallways between each room and the next
        for(int i = 0; i < mRooms.size(); i++){

        }

    }

    public void generateRoom(vec4 position){

    }

    public void generateHallway(vec4 start, vec4 end){

    }
}

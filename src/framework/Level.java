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

    public Level(int maxRoomNum){
        mRooms = new ArrayList<>();
        mHallways = new ArrayList<>();
    }

    public void generate(int i, vec4 pos, int prevDirection){
        if(i > ){
            //gen room
            generateRoom(pos);

            //decide if it has hallways for each direction
            //Gen hallway then recursively call generate
            Random rand = new Random();
            boolean chance;


            //North
            chance = rand.nextBoolean();
            if(chance && prevDirection != 0){

            }

            //East
            chance = rand.nextBoolean();
            if(chance && prevDirection != 2){

            }

            //South
            chance = rand.nextBoolean();
            if(chance && prevDirection != 2){

            }

            //West
            chance = rand.nextBoolean();
            if(chance && prevDirection != 4){

            }


            //recursively gen rooms at the end of new hallways
        }
    }

    public void generateRoom(vec4 position){

    }

    public void generateHallway(vec4 position){

    }
}

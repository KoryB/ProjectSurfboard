package framework;

import framework.collisions.CollisionObject;
import framework.drawing.Drawable;
import framework.drawing.Program;
import framework.math3d.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;


/**
 * Created by Michael on 1/26/2016.
 */
public class Level implements Drawable{

    public vec2 mDimensions, mXRange, mZRange;
    public int mNumTiles;
    public float mPercentFloor;
    public vec4 mStartingCorner;
    public CollisionObject[][] mTiles;
    public ArrayList<Floor> mFloors;
    public ArrayList<Wall> mWalls;
    public GiantFloor mGiantFloor;
    public GiantWall mGiantWall;

    public Level(vec2 dimensions, float percentFloor) {
        mDimensions = dimensions;
        mNumTiles = (int) (dimensions.x * dimensions.y);

        mXRange = new vec2(-((int) dimensions.x / 2), ((int) dimensions.x / 2));
        mZRange = new vec2(-((int) dimensions.y / 2), ((int) dimensions.y / 2));

        mPercentFloor = percentFloor;
        mStartingCorner = new vec4((int) mXRange.x, 0, (int) mZRange.x, 1);

        mTiles = new CollisionObject[(int) dimensions.x][(int) dimensions.y];
        mFloors = new ArrayList<>();
        mWalls = new ArrayList<>();

        genNewLevel();
        System.out.println();


        for(int i = 0; i < mTiles.length; i++){
            for(int j = 0; j < mTiles[0].length; j++){
                if(mTiles[i][j] instanceof Floor)
                    System.out.print("_ ");
                else if(mTiles[i][j] instanceof Wall)
                    System.out.print("# ");
                else
                    System.out.print("  ");
            }
            System.out.println();
        }

        mGiantFloor = new GiantFloor(mFloors.toArray(new Floor[mFloors.size()]));
        mGiantWall = new GiantWall(mWalls.toArray(new Wall[mWalls.size()]));

        for (int k = 0; k < 1000000; k++)
        {
            mWalls.add(new Wall(mStartingCorner, new vec4(1, 2, 1, 0)));
        }
    }

    private void genEmptyLevel(){
        //Stick a Wall in every Empty Spot
        for(int i = 0; i < mTiles.length; i++){
            for(int j = 0; j < mTiles[0].length; j++){
                vec4 pos = new vec4(i, 0, j, 0);
                mTiles[i][j] = new Wall(pos.add(mStartingCorner), new vec4(1, 2, 1, 0));
            }
        }
    }

    private void genFloors(){
        //Find Starting Tile & place floor
        Random rand = new Random();
        int currentX = (int) (mDimensions.x / 2);
        int currentZ = (int) (mDimensions.y / 2);

        vec4 pos = new vec4(currentX, 0, currentZ, 0);
        mTiles[currentX][currentZ] = new Floor(pos.add(mStartingCorner), new vec2(1, 1));

        int numFloors = 1;
        int direction;
        int oppositeDirection = 100;

        //Do random walk algorithm until correct percent of tiles are floors
        while (numFloors < (int) (mNumTiles * mPercentFloor)){
            direction = rand.nextInt(4);

            //Isn't allowed to walk directly backwards.
            while (direction == oppositeDirection){
                direction = rand.nextInt(4);
            }

            //Walk in random direction and make floor as long as not walking onto edge tile
            //North
            if(direction == 0){
                if(currentZ > 1){
                    currentZ -= 1;
                    pos.x = currentX;
                    pos.z = currentZ;
                    oppositeDirection = 2;
                    if(!(mTiles[currentX][currentZ] instanceof Floor)){
                        mTiles[currentX][currentZ] = new Floor(pos.add(mStartingCorner), new vec2(1, 1));
                        numFloors++;
                    }
                }
            }
            //East
            else if(direction == 1){
                if(currentX < mDimensions.x - 2){
                    currentX += 1;
                    pos.x = currentX;
                    pos.z = currentZ;
                    oppositeDirection = 3;
                    if(!(mTiles[currentX][currentZ] instanceof Floor)){
                        mTiles[currentX][currentZ] = new Floor(pos.add(mStartingCorner), new vec2(1, 1));
                        numFloors++;
                    }
                }
            }
            //South
            else if(direction == 2){
                if(currentZ < mDimensions.y - 2){
                    currentZ += 1;
                    pos.x = currentX;
                    pos.z = currentZ;
                    oppositeDirection = 0;
                    if(!(mTiles[currentX][currentZ] instanceof Floor)){
                        mTiles[currentX][currentZ] = new Floor(pos.add(mStartingCorner), new vec2(1, 1));
                        numFloors++;
                    }
                }
            }
            //West
            else {
                if(currentX > 1){
                    currentX -= 1;
                    pos.x = currentX;
                    pos.z = currentZ;
                    oppositeDirection = 1;
                    if(!(mTiles[currentX][currentZ] instanceof Floor)){
                        mTiles[currentX][currentZ] = new Floor(pos.add(mStartingCorner), new vec2(1, 1));
                        numFloors++;
                    }
                }
            }
        }
    }

    private void clearOutsideWalls(){
        for(int i = 0; i < mTiles.length; i++){
            boolean outside = false;
            for(int j = 0; j < mTiles[0].length; j++){
                if(mTiles[i][j] instanceof Wall){

                    if(i == 0){
                        if(j == 0){
                            if(!(mTiles[i+1][j] instanceof Floor) && !(mTiles[i][j+1] instanceof Floor) && !(mTiles[i+1][j+1] instanceof Floor)){
                                mTiles[i][j] = null;
                            }
                        }
                        else if(j == mDimensions.y - 1){
                            if(!(mTiles[i+1][j] instanceof Floor) && !(mTiles[i][j-1] instanceof Floor) && !(mTiles[i+1][j-1] instanceof Floor)){
                                mTiles[i][j] = null;
                            }

                        }
                        else{
                            if(!(mTiles[i+1][j] instanceof Floor) && !(mTiles[i][j-1] instanceof Floor) && !(mTiles[i][j+1] instanceof Floor) && !(mTiles[i+1][j-1] instanceof Floor) && !(mTiles[i+1][j+1] instanceof Floor)){
                                mTiles[i][j] = null;
                            }
                        }

                    }

                    else if(j == 0){
                        if(i == mDimensions.x - 1){
                            if(!(mTiles[i][j+1] instanceof Floor) && !(mTiles[i-1][j] instanceof Floor) && !(mTiles[i-1][j+1] instanceof Floor)){
                                mTiles[i][j] = null;
                            }
                        }
                        else{
                            if(!(mTiles[i+1][j] instanceof Floor) && !(mTiles[i-1][j] instanceof Floor) && !(mTiles[i][j+1] instanceof Floor) && !(mTiles[i+1][j+1] instanceof Floor) && !(mTiles[i-1][j+1] instanceof Floor)){
                                mTiles[i][j] = null;
                            }
                        }
                    }

                    else if(i == mDimensions.x - 1){
                        if(j == mDimensions.y - 1){
                            if(!(mTiles[i][j-1] instanceof Floor) && !(mTiles[i-1][j] instanceof Floor) && !(mTiles[i-1][j-1] instanceof Floor)){
                                mTiles[i][j] = null;
                            }
                        }
                        else{
                            if(!(mTiles[i][j+1] instanceof Floor) && !(mTiles[i][j-1] instanceof Floor) && !(mTiles[i-1][j] instanceof Floor) && !(mTiles[i-1][j+1] instanceof Floor) && !(mTiles[i-1][j-1] instanceof Floor)){
                                mTiles[i][j] = null;
                            }
                        }
                    }

                    else if(j == mDimensions.y - 1){
                        if(!(mTiles[i][j-1] instanceof Floor) && !(mTiles[i+1][j] instanceof Floor) && !(mTiles[i-1][j] instanceof Floor) && !(mTiles[i+1][j-1] instanceof Floor) && !(mTiles[i-1][j-1] instanceof Floor)){
                            mTiles[i][j] = null;
                        }
                    }

                    else{
                        if(!(mTiles[i][j+1] instanceof Floor) && !(mTiles[i][j-1] instanceof Floor) && !(mTiles[i+1][j] instanceof Floor) && !(mTiles[i-1][j] instanceof Floor) && !(mTiles[i-1][j-1] instanceof Floor) && !(mTiles[i-1][j+1] instanceof Floor) && !(mTiles[i+1][j-1] instanceof Floor) && !(mTiles[i+1][j+1] instanceof Floor)){
                            mTiles[i][j] = null;
                        }
                    }
                }
            }
        }
    }

    public void genNewLevel(){
        genEmptyLevel();
        genFloors();
        clearOutsideWalls();

        //Add the walls and floors to an individual arrayList
        for(int i = 0; i < mTiles.length; i++){
            for(int j = 0; j < mTiles[0].length; j++){
                if(mTiles[i][j] instanceof Wall){
                    mWalls.add((Wall) mTiles[i][j]);
                }
                else if(mTiles[i][j] instanceof Floor){
                    mFloors.add((Floor) mTiles[i][j]);
                }
            }
        }
    }

    public void draw(Program program){
        this.drawFloors(program);
        this.drawWalls(program);
    }

    //I made this because i imagine that we will eventually have enemies and such, and will need to draw everything except the
    //Mirror floor in one go when making the reflections, so making this method so i dont have to change the method for reflection
    //- Michael
    public void drawAllExceptFloor(Program program){
        this.drawWalls(program);
    }

    public void drawWalls(Program program){
        mGiantWall.draw(program);
    }

    public void drawFloors(Program program){
        mGiantFloor.draw(program);
    }
}

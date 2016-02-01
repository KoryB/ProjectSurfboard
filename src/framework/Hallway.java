package framework;

import framework.math3d.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.*;

/**
 * Created by Michael on 1/26/2016.
 */
public class Hallway {

    public ArrayList<Wall> mWalls;
    public ArrayList<Floor> mFloors;
    
    public Hallway(Room start, Room end){
        genHorHall(start, end);
        genVertHall(start, end);
    }

    private void genHorHall(Room start, Room end){
        int distance = abs((int) (start.mCenter.x - end.mCenter.x - ((start.mWidth / 2) + (end.mWidth / 2))));
        vec4 curPos;
        if(end.mCenter.x > start.mCenter.x) {
            curPos = new vec4(start.mWidth, 0, 0, 0);
            curPos.add(start.mCenter);

            for(int i = 0; i < distance; i++){
                curPos.add(new vec4(1, 0, 0, 0));
                //create floor tile here and add to mFloors
                System.out.println(curPos);
            }
        }
        else if(end.mCenter.x < start.mCenter.x) {
            curPos = new vec4(-start.mWidth, 0, 0, 0);
            curPos.add(start.mCenter);

            for(int i = 0; i < distance; i++){
                curPos.add(new vec4(-1, 0, 0, 0));
                //create floor tile here and add to mFloors
                System.out.println(curPos);
            }
        }
        else{
            return;
        }
    }

    private void genVertHall(Room start, Room end){
        int distance = abs((int) (start.mCenter.z - end.mCenter.z - ((start.mLength / 2) + (end.mLength / 2))));
        vec4 curPos;
        if(end.mCenter.z > start.mCenter.z) {
            curPos = new vec4(start.mLength, 0, 0, 0);
            curPos.add(start.mCenter);

            for(int i = 0; i < distance; i++){
                curPos.add(new vec4(0, 0, 1, 0));
                //create floor tile here and add to mFloors
                System.out.println(curPos);
            }
        }
        else if(end.mCenter.z < start.mCenter.z) {
            curPos = new vec4(-start.mLength, 0, 0, 0);
            curPos.add(start.mCenter);

            for(int i = 0; i < distance; i++){
                curPos.add(new vec4(0, 0, -1, 0));
                //create floor tile here and add to mFloors
                System.out.println(curPos);
            }
        }
        else{
            return;
        }
    }
}

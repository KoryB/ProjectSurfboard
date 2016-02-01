package framework;

import framework.math3d.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Michael on 1/26/2016.
 */
public class Room {

    public ArrayList<Wall> mWalls;
    public Floor mFloor;
    public vec4 mCenter;
    public int mWidth;
    public int mLength;

    public Room(vec4 centerPoint, String roomFile) throws IOException, ParseException {
        mCenter = centerPoint;
        mWalls = new ArrayList<>();
        genRoom(roomFile, mCenter);
    }

    public void genRoom(String roomFile, vec4 centerPoint) throws IOException, ParseException {
        FileReader reader = new FileReader(roomFile);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonRoom = (JSONObject) jsonParser.parse(reader);
        JSONObject jRooms = (JSONObject) jsonRoom.get("walls");
        JSONObject jFloor = (JSONObject) jsonRoom.get("floor");
        JSONObject jDims = (JSONObject) jsonRoom.get("dimensions");

        //Make walls
        for(int i = 0; i < jRooms.size(); i++){
            JSONObject wall = (JSONObject) jRooms.get("wall" + i);
            JSONArray position = (JSONArray) wall.get("position");
            JSONArray scale = (JSONArray) wall.get("scale");

            vec4 wallPos = new vec4(position.get(0), position.get(1), position.get(2), 0);
            wallPos.add(centerPoint);
            vec3 wallScale = new vec3(scale.get(0), scale.get(1), scale.get(2));

            //Make instance of a wall here and add to mWalls.
        }

        //Make floor
        JSONArray position = (JSONArray) jFloor.get("position");
        JSONArray scale = (JSONArray) jFloor.get("scale");

        vec4 floorPos = new vec4(position.get(0), position.get(1), position.get(2), 0);
        floorPos.add(centerPoint);
        vec3 floorScale = new vec3(scale.get(0), scale.get(1), scale.get(2));
        //Make instance of floor and reference with mFloor.

        //Set dimensions
        mWidth = (int) (long) jDims.get("width");
        mLength = (int) (long) jDims.get("height");
    }
}

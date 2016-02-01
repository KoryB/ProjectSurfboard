package framework;

import framework.math3d.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Created by Michael on 1/26/2016.
 */
public class Room {

    public Wall[] mWalls;
    public Floor mFloor;
    public vec4 mCenter;

    public Room(vec4 centerPoint, String roomFile) throws IOException, ParseException {
        mCenter = centerPoint;
        genRoom(roomFile, mCenter);
    }

    public void genRoom(String roomFile, vec4 centerPoint) throws IOException, ParseException {
        FileReader reader = new FileReader(roomFile);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonRoom = (JSONObject) jsonParser.parse(reader);
        JSONObject jRooms = (JSONObject) jsonRoom.get("walls");
        JSONObject jFloor = (JSONObject) jsonRoom.get("floor");

        //Make walls
        for(int i = 0; i < jRooms.size(); i++){
            JSONObject wall = (JSONObject) jRooms.get("wall" + i);
            JSONArray position = (JSONArray) wall.get("position");
            JSONArray scale = (JSONArray) wall.get("scale");

            vec4 wallPos = new vec4(position.get(0), position.get(1), position.get(2), position.get(3));
            wallPos.add(centerPoint);
            vec3 wallScale = new vec3(scale.get(0), scale.get(1), scale.get(2));

            //Make instance of a wall here and add to mWalls.
        }

        //Make floor
        JSONArray floorPos = (JSONArray) jFloor.get("position");
        floorPos.add(centerPoint);
        JSONArray floorScale = (JSONArray) jFloor.get("scale");
        //Make instance of floor and reference with mFloor.
    }
}

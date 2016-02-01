package framework;

import framework.math3d.*;
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

    public Room(vec4 position, String roomFile){
        mCenter = position;
    }

    public void genRoom(String roomFile) throws IOException, ParseException {
        FileReader reader = new FileReader(roomFile);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonRoom = (JSONObject) jsonParser.parse(reader);


    }
}

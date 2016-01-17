package framework;

/**
 * Created by Michael on 1/17/2016.
 */

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import static JSDL.JSDL.*;

public class InputHandler {

    int mPendingEvents;
    SDL_Event mEvent;
    Set<Integer> mKeysPressed;
    Properties mBindings;

    public InputHandler(String configFile){
        mPendingEvents = 0;
        mEvent = new SDL_Event();
        mKeysPressed = new TreeSet<>();
        mBindings = new Properties();

        try {
            loadBindings(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void poll(){

        while (true){
            mPendingEvents = SDL_PollEvent(mEvent);

            if(mPendingEvents == 0)
                break;
            if( mEvent.type == SDL_QUIT )
                System.exit(0);
            if( mEvent.type == SDL_MOUSEMOTION ){

            }
            if( mEvent.type == SDL_KEYDOWN ){
                mKeysPressed.add(mEvent.key.keysym.sym);
            }
            if( mEvent.type == SDL_KEYUP ){
                mKeysPressed.remove(mEvent.key.keysym.sym);
            }
        }
    }

    public boolean isKeyDown(String key){
        System.out.println(mKeysPressed);
        if(mKeysPressed.contains(Integer.parseInt(mBindings.getProperty(key))))
            return true;
        else
            return false;
    }

    public void loadBindings(String filename) throws IOException {
        InputStream input = new FileInputStream(filename);
        mBindings.load(input);
    }

    public void saveBindings(String filename) throws IOException {
        OutputStream output = new FileOutputStream(filename);
        mBindings.store(output, null);
    }
}

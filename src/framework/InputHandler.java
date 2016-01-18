package framework;

/**
 * Created by Michael on 1/17/2016.
 */

import java.io.*;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import static JSDL.JSDL.*;
import framework.math3d.vec2;

public class InputHandler {

    int mPendingEvents;
    vec2 mMousePos, mMouseOffset;
    SDL_Event mEvent;
    Set<Integer> mKeysPressed, mKeysReleased, mMousePressed, mMouseReleased;
    Properties mBindings;

    public InputHandler(String configFile){
        mPendingEvents = 0;
        mEvent = new SDL_Event();
        mKeysPressed = new TreeSet<>();
        mKeysReleased = new TreeSet<>();
        mMousePressed = new TreeSet<>();
        mMouseReleased = new TreeSet<>();
        mBindings = new Properties();
        mMousePos = new vec2();
        mMouseOffset = new vec2();

        try {
            loadBindings(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void poll(){
        mKeysReleased.clear();
        mMouseReleased.clear();
        mMouseOffset.x = 0;
        mMouseOffset.y = 0;

        while (true){
            mPendingEvents = SDL_PollEvent(mEvent);

            if(mPendingEvents == 0)
                break;
            if( mEvent.type == SDL_QUIT )
                System.exit(0);
            if( mEvent.type == SDL_MOUSEBUTTONDOWN){
                mMousePressed.add(mEvent.button.button);
            }
            if( mEvent.type == SDL_MOUSEBUTTONUP){
                mMouseReleased.add(mEvent.button.button);
            }
            if( mEvent.type == SDL_MOUSEMOTION){
                mMousePos.x = mEvent.motion.x;
                mMousePos.y = mEvent.motion.y;
                mMouseOffset.x = mEvent.motion.xrel;
                mMouseOffset.y = mEvent.motion.yrel;
            }
            if( mEvent.type == SDL_KEYDOWN ){
                mKeysPressed.add(mEvent.key.keysym.sym);
            }
            if( mEvent.type == SDL_KEYUP ){
                mKeysPressed.remove(mEvent.key.keysym.sym);
                mKeysReleased.add(mEvent.key.keysym.sym);
            }
        }
    }

    public boolean keyPressed(String key){
        if(mKeysPressed.contains(Integer.parseInt(mBindings.getProperty(key))))
            return true;
        else
            return false;
    }

    public boolean keyPressed(int key){
        if(mKeysPressed.contains(key))
            return true;
        else
            return false;
    }

    public boolean keyReleased(String key){
        if(mKeysReleased.contains(Integer.parseInt(mBindings.getProperty(key))))
            return true;
        else
            return false;
    }

    public boolean keyReleased(int key){
        if(mKeysReleased.contains(key))
            return true;
        else
            return false;
    }

    public boolean mousePressed(int button){
        if(mMousePressed.contains(button))
            return true;
        else
            return false;
    }

    public boolean mouseReleased(int button){
        if(mMouseReleased.contains(button))
            return true;
        else
            return false;
    }

    public vec2 getMousePos(){
        return mMousePos;
    }

    public vec2 getMouseOffset(){
        return mMouseOffset;
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

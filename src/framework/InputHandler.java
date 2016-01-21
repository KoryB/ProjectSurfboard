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
    Set<Integer> mKeysDown, mKeysPressed, mKeysReleased, mMouseDown, mMousePressed, mMouseReleased;
    Properties mBindings;

    public InputHandler(String configFile){
        mPendingEvents = 0;
        mEvent = new SDL_Event();

        mKeysDown = new TreeSet<>();
        mKeysPressed = new TreeSet<>();
        mKeysReleased = new TreeSet<>();

        mMouseDown = new TreeSet<>();
        mMousePressed = new TreeSet<>();
        mMouseReleased = new TreeSet<>();
        mMousePos = new vec2();
        mMouseOffset = new vec2();

        mBindings = new Properties();


        try {
            loadBindings(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void poll(){
        mKeysPressed.clear();
        mKeysReleased.clear();
        mMousePressed.clear();
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
                if(!mMouseDown.contains(mEvent.button.button))
                    mMousePressed.add(mEvent.button.button);
                mMouseDown.add(mEvent.button.button);
            }

            if( mEvent.type == SDL_MOUSEBUTTONUP){
                mMouseDown.remove(mEvent.button.button);
                mMouseReleased.add(mEvent.button.button);
            }

            if( mEvent.type == SDL_MOUSEMOTION){
                mMousePos.x = mEvent.motion.x;
                mMousePos.y = mEvent.motion.y;
                mMouseOffset.x = mEvent.motion.xrel;
                mMouseOffset.y = mEvent.motion.yrel;
            }

            if( mEvent.type == SDL_KEYDOWN ){
                if(!mKeysDown.contains(mEvent.key.keysym.sym))
                    mKeysPressed.add(mEvent.key.keysym.sym);
                mKeysDown.add(mEvent.key.keysym.sym);
            }

            if( mEvent.type == SDL_KEYUP ){
                mKeysDown.remove(mEvent.key.keysym.sym);
                mKeysReleased.add(mEvent.key.keysym.sym);
            }
        }
    }

    public boolean keyDown(String key){
        if(mKeysDown.contains(Integer.parseInt(mBindings.getProperty(key)))) {
            return true;
        }
        else
            return false;
    }

    public boolean keyDown(int key){
        if(mKeysDown.contains(key))
            return true;
        else
            return false;
    }

    public boolean keyPressed(String key){
        if(mKeysPressed.contains(Integer.parseInt(mBindings.getProperty(key)))) {
            return true;
        }
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

    public boolean mouseDown(int button){
        if(mMousePressed.contains(button))
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

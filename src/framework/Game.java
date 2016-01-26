package framework;

import framework.math3d.vec2;

import static JGL.JGL.*;
import static JSDL.JSDL.*;

/**
 * Created by Michael on 1/15/2016.
 */
public class Game {

    public Screen mActiveScreen;
    public Program mProgram;
    public boolean mRunning;
    private long mWindow;

    public Game(){
        SDL_Init(SDL_INIT_VIDEO);
        mWindow = SDL_CreateWindow("ETGG 2802",40,60, Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT, SDL_WINDOW_OPENGL );
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK,SDL_GL_CONTEXT_PROFILE_CORE);
        SDL_GL_SetAttribute(SDL_GL_DEPTH_SIZE,24);
        SDL_GL_SetAttribute(SDL_GL_STENCIL_SIZE,8);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION,3);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION,2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_FLAGS, SDL_GL_CONTEXT_DEBUG_FLAG);
        SDL_GL_CreateContext(mWindow);

        glDebugMessageControl(GL_DONT_CARE,GL_DONT_CARE,GL_DONT_CARE, 0,null, true );
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(
                new DebugMessageCallback()
                {
                    @Override
                    public void debugCallback(int source, int type, int id, int severity, String message, Object obj)
                    {
                        System.out.println("GL message: " + message);
                        if (severity == GL_DEBUG_SEVERITY_HIGH)
                            System.exit(1);
                    }
                },
                null);

        //Starting with a game screen for now until we have the main menu implemented
        mProgram = new Program("vs.txt","fs.txt");
        mActiveScreen = new GameScreen();
        mRunning = true;
    }

    public void update(){
        mActiveScreen.update();
    }

    public void render(Program program){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        mProgram.use();
        mActiveScreen.render(program);
        SDL_GL_SwapWindow(mWindow);
    }

    public void run(){
        while (mRunning) {
            update();
            render(mProgram);
        }
    }

    public void setActiveScreen(Screen screen){
        mActiveScreen = screen;
    }
}

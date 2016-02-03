package framework;

import framework.drawing.DrawManager;
import framework.drawing.Drawable;
import framework.drawing.Program;

import static JGL.JGL.*;
import static JSDL.JSDL.*;

/**
 * Created by Michael on 1/15/2016.
 */
public class Game implements Drawable{

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
        mProgram = new Program("shaders/vs.txt","shaders/fs.txt");
        mActiveScreen = new GameScreen();
        mRunning = true;
    }

    public void update(){
        mActiveScreen.update();
    }

    public void draw(Program program){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        mProgram.use();
//        DrawManager.getInstance().drawBlur(mActiveScreen, program, null, 4, 20);
        mActiveScreen.draw(program);
        SDL_GL_SwapWindow(mWindow);
    }

    public void run(){
        while (mRunning) {
            update();
            draw(mProgram);
        }
    }

    public void setActiveScreen(Screen screen){
        mActiveScreen = screen;
    }
}

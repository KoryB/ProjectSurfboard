package framework;

import framework.math3d.vec3;
import framework.math3d.mat4;
import java.util.Set;
import java.util.TreeSet;
import static JGL.JGL.*;
import static JSDL.JSDL.*;

/**
 * Created by Michael on 1/15/2016.
 */
public class GameScreen implements Screen {

    //Player mPlayer;
    InputHandler mInput;
    boolean mPaused;
    Camera cam;
    Program mProgram;
    Program blurprog;
    float prev, framenum;
    Mesh column;
    UnitSquare usq;
    ImageTextureArray ita;

    public GameScreen(){
        mPaused = false;
        mInput = new InputHandler("config/cfg.properties");

        int[] tmp = new int[1];
        glGenVertexArrays(1,tmp);
        int vao = tmp[0];
        glBindVertexArray(vao);

        glClearColor(0.2f,0.4f,0.6f,1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        usq = new UnitSquare();
        ita = new ImageTextureArray("assets/globe%02d.png",24);
        cam = new Camera();
        cam.lookAt( new vec3(0,0,5), new vec3(0,0,0), new vec3(0,1,0) );

        prev = (float)(System.nanoTime()*1E-9);

        framenum = 0.0f;
    }

    @Override
    public void update() {
        mInput.poll();

        float now = (float)(System.nanoTime()*1E-9);
        float elapsed = now-prev;

        framenum += 16.0f*elapsed;
        while( framenum >= 24.0f )
            framenum -= 24.0f;

        prev=now;

        if( mInput.keyPressed("CAMERA_MOVE_FORWARD"))
            cam.walk(2.0f * elapsed);
        if( mInput.keyPressed("CAMERA_MOVE_BACKWARD"))
            cam.walk(-0.5f*elapsed);
        if( mInput.keyPressed("CAMERA_TURN_LEFT"))
            cam.turn(0.4f*elapsed);
        if( mInput.keyPressed("CAMERA_TURN_RIGHT"))
            cam.turn(-0.4f*elapsed);
        if( mInput.keyPressed("CAMERA_TILT_LEFT"))
            cam.tilt(0.4f*elapsed);
        if( mInput.keyPressed("CAMERA_TILT_RIGHT"))
            cam.tilt(-0.4f*elapsed);

    }

    @Override
    public void render(Program program) {
        program.setUniform("diffuse_texture", ita);
        program.setUniform("framenumber", framenum);
        program.setUniform("lightPos", new vec3(50, 50, 50));
        cam.draw(program);
        program.setUniform("worldMatrix", mat4.identity());
        usq.draw(program);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide(Screen nextScreen) {

    }

    @Override
    public void pause() {

    }
}

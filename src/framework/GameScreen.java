package framework;

import framework.collisions.CollisionHandler;
import framework.math3d.primitives.BoundedPlane;
import framework.math3d.primitives.IntersectionHandler;
import framework.math3d.primitives.Plane;
import framework.math3d.vec3;
import framework.math3d.mat4;
import framework.math3d.vec4;
import framework.math3d.primitives.Ray;

import static JGL.JGL.*;
import static JSDL.JSDL.*;
import static framework.math3d.math3d.normalize;

/**
 * Created by Michael on 1/15/2016.
 */
public class GameScreen implements Screen {

    //Player mPlayer;
    public static InputHandler mInput = new InputHandler("config/cfg.properties");;
    private boolean mPaused;
    Camera cam;
    Program blurprog;
    float prev, framenum;
    UnitSquare usq;
    ImageTextureArray ita;
    Wall wall, wall2, wall3;
    Player player;
    Floor floor;

    public GameScreen(){
        mPaused = false;

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
//        cam.lookAt(new vec3(-4.5, 3, -.5), new vec3(-3.5, 0, -1.5), normalize(new vec3(1, 0, -1)));
        cam.lookAt(new vec3(0, 4, 0), new vec3(), normalize(new vec3(0, 0, -1)));

        prev = (float)(System.nanoTime()*1E-9);

        framenum = 0.0f;

        wall = new Wall(new vec4(0, 0, 0, 1));
        wall2 = new Wall(new vec4(0.5, 0, 0, 1));
        wall3 = new Wall(new vec4(0.5, 0, -1, 1));
        player = new Player(new vec4(-2.0, 1, -.5, 1));
        floor = new Floor(new vec4(-2.0, 0, -2.0, 1));
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

        if( mInput.keyDown("CAMERA_MOVE_FORWARD"))
            cam.walk(2.0f * elapsed);
        if( mInput.keyDown("CAMERA_MOVE_BACKWARD"))
            cam.walk(-0.5f*elapsed);
//        if( mInput.keyDown("CAMERA_TURN_LEFT"))
//            cam.turn(0.4f*elapsed);
//        if( mInput.keyDown("CAMERA_TURN_RIGHT"))
//            cam.turn(-0.4f*elapsed);
        if( mInput.keyDown("CAMERA_TILT_LEFT"))
            cam.tilt(0.4f*elapsed);
        if( mInput.keyDown("CAMERA_TILT_RIGHT"))
            cam.tilt(-0.4f*elapsed);

        if (mInput.keyDown(SDLK_r))
            cam.tilt(0.4f * elapsed);
        if (mInput.keyDown(SDLK_t))
            cam.tilt(-0.4f * elapsed);
        if (mInput.keyDown(SDLK_f))
            cam.pitch(0.4f * elapsed);
        if (mInput.keyDown(SDLK_g))
            cam.pitch(-0.4f * elapsed);
        if (mInput.keyDown(SDLK_a))
            cam.strafe(new vec3(-0.4f * elapsed, 0, 0));
        if (mInput.keyDown(SDLK_d))
            cam.strafe(new vec3(0.4f * elapsed, 0, 0));

//        TODO: make mouse buttons constants
        if (mInput.mousePressed(1))
        {
            Ray camRay = cam.getRay(mInput.getMousePos());
            camRay.printInfo();
            System.out.println();
            floor.getCollisionPrimitive().printInfo();
            System.out.println();
            Float t = IntersectionHandler.RayPlaneIntersection(camRay, (Plane) floor.getCollisionPrimitive(), false);
            if (t != null)
            {
                System.out.println("Point: " + camRay.getPoint(t));
                System.out.println("Moving along!");
                player.setGotoPoint(camRay.getPoint(t));
            }
        }
        else if (mInput.mousePressed(3))
        {
            player.clearGotoPoint();
        }

        player.update(elapsed);
//
//        CollisionHandler.pushApartAABB(player, wall);
//        CollisionHandler.pushApartAABB(player, wall2);
//        CollisionHandler.pushApartAABB(player, wall3);

    }

    @Override
    public void render(Program program) {
        program.setUniform("diffuse_texture", ita);
        program.setUniform("framenumber", framenum);
        program.setUniform("lightPos", new vec3(50, 50, 50));
        cam.draw(program);
        floor.draw(program);
        wall.draw(program);
        wall2.draw(program);
        wall3.draw(program);
        player.draw(program);
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

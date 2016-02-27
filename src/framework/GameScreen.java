package framework;

import framework.collisions.CollisionHandler;
import framework.collisions.CollisionObject;
import framework.drawing.DrawManager;
import framework.drawing.Framebuffer;
import framework.drawing.Program;
import framework.drawing.textures.SolidTexture;
import framework.drawing.textures.Texture2D;
import framework.math3d.*;
import framework.math3d.primitives.BoundedPlane;
import framework.math3d.primitives.IntersectionHandler;
import framework.math3d.primitives.Plane;
import framework.math3d.primitives.Ray;

import java.util.Arrays;

import static JGL.JGL.*;
import static JSDL.JSDL.*;
import static framework.math3d.math3d.normalize;

/**
 * Created by Michael on 1/15/2016.
 */
public class GameScreen implements Screen
{

    //Player mPlayer;
    public static InputHandler mInput = new InputHandler("config/cfg.properties");
    ;
    private boolean mPaused;
    Camera cam;
    Program blurprog;
    float prev, framenum;
    //    UnitSquare usq;
    //    ImageTextureArray ita;
    Wall wall, wall2, wall3;
    Player player;
    Floor floor;
    Level level;
    Framebuffer shadowFBO = new Framebuffer(512, 512);
    private Texture2D mDummyTexture = new SolidTexture(GL_FLOAT, 0.0f, 0.0f, 0.0f, 0.0f);
    public GameScreen()
    {
        mPaused = false;
        level = new Level(new vec2(50, 50), .12f);

        int[] tmp = new int[1];
        glGenVertexArrays(1, tmp);
        int vao = tmp[0];
        glBindVertexArray(vao);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_STENCIL_TEST);

        //        usq = new UnitSquare();
        //        ita = new ImageTextureArray("assets/globe%02d.png",24);
        cam = new Camera();
        player = new Player(new vec4(0, 1, 0, 1));
        cam.lookAtPlayer(player);
        //        cam.lookAtPlayer(player);
        //        cam.lookAt(new vec3(0, 4, 0), new vec3(), normalize(new vec3(0, 0, -1)));

        prev = (float) (System.nanoTime() * 1E-9);

        framenum = 0.0f;
    }

    @Override
    public void update()
    {
        mInput.poll();

        float now = (float) (System.nanoTime() * 1E-9);
        float elapsed = now - prev;

        System.out.println(1.0/ elapsed);

        prev = now;

        if (mInput.keyDown("CAMERA_MOVE_FORWARD"))
            cam.walk(2.0f * elapsed);
        if (mInput.keyDown("CAMERA_MOVE_BACKWARD"))
            cam.walk(-0.5f * elapsed);
        if (mInput.keyDown("CAMERA_TURN_LEFT"))
            cam.turn(0.4f * elapsed);
        if (mInput.keyDown("CAMERA_TURN_RIGHT"))
            cam.turn(-0.4f * elapsed);
        if (mInput.keyDown("CAMERA_TILT_LEFT"))
            cam.tilt(0.4f * elapsed);
        if (mInput.keyDown("CAMERA_TILT_RIGHT"))
            cam.tilt(-0.4f * elapsed);

        if (mInput.keyDown(SDLK_f))
            cam.pitch(0.4f * elapsed);
        if (mInput.keyDown(SDLK_g))
            cam.pitch(-0.4f * elapsed);
        if (mInput.keyDown(SDLK_z))
            cam.strafe(new vec3(-0.4f * elapsed, 0, 0));
        if (mInput.keyDown(SDLK_c))
            cam.strafe(new vec3(0.4f * elapsed, 0, 0));
        if (mInput.keyDown(SDLK_q))
            cam.strafe(new vec3(0, -0.4f * elapsed, 0));
        if (mInput.keyDown(SDLK_e))
            cam.strafe(new vec3(0, 0.4f * elapsed, 0));

        cam.lookAtPlayer(player);

        //        TODO: make mouse buttons constants
        if (mInput.mouseDown(1))
        {
            Ray camRay = cam.getRay(mInput.getMousePos());
            for (int i = 0; i < level.mTiles.length; i++)
            {
                for (int j = 0; j < level.mTiles[i].length; j++)
                {
                    if (level.mTiles[i][j] instanceof Floor)
                    {
                        Float t = IntersectionHandler.RayPlaneIntersection(camRay, (BoundedPlane) level.mTiles[i][j]
                                .getCollisionPrimitive(), false);
                        if (t != null)
                        {
                            player.setGotoPoint(camRay.getPoint(t));
                        }
                    }
                }
            }
        } else if (mInput.mousePressed(3))
        {
            player.clearGotoPoint();
        }

        for (int i = 0; i < level.mTiles.length; i++)
        {
            for (int j = 0; j < level.mTiles[i].length; j++)
            {
                if (level.mTiles[i][j] instanceof Wall)
                {
                    CollisionHandler.pushApartAABB(level.mTiles[i][j], player);
                }
            }
        }

        player.update(elapsed);

    }

    @Override
    public void draw(Program program)
    {
        shadowFBO.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        shadowFBO.unbind();
        program.setUniform("lightPos", new vec3(50, 50, 50));
        DrawManager.getInstance().drawMirrorFloors(program, cam, level, player);
        DrawManager.getInstance().drawShadowBuffer(program, cam, shadowFBO, level, player);
        program.setUniform("lightViewMatrix", cam.getViewMatrix());
        program.setUniform("lightProjMatrix", cam.compute_projp_matrix());
        program.setUniform("lightHitherYon", new vec3(cam.getHither(), cam.getYon(), cam.getYon() - cam.getHither()));
        program.setUniform("shadow_texture", shadowFBO.texture);
        cam.lookAtPlayer(player);
        cam.draw(program);

        player.draw(program);

        glStencilFunc(GL_ALWAYS, 1, 0xff);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

        level.drawWalls(program);

        //        glClear(GL_DEPTH_BUFFER_BIT);
//        DrawManager.getInstance().drawLaplacian(player, program, null); //this produces white.
        glStencilFunc(GL_EQUAL, 1, 0xff);
        glStencilOp(GL_KEEP, GL_INCR, GL_KEEP);
        player.draw(program);
//        DrawManager.getInstance().drawBlurScreen(player, program, null, 10, 10);

        glStencilFunc(GL_LEQUAL, 2, 0xff); // Reference less than or equal to buffer. 2 <= 2 so works! 2 <= 3 works!
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glClear(GL_DEPTH_BUFFER_BIT);

        DrawManager.getInstance().drawLaplacian(player, program, null, new vec4(0.5, 1.0, 0.5, 0.5), new vec4(0.5, 0.1, 0.1, 0.5));

        glStencilFunc(GL_ALWAYS, 0, 0xff);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        program.setUniform("shadow_texture", mDummyTexture);
    }

    @Override
    public void show()
    {

    }

    @Override
    public void hide(Screen nextScreen)
    {

    }

    @Override
    public void pause()
    {

    }
}

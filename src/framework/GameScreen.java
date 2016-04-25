package framework;

import framework.collisions.CollisionHandler;
import framework.collisions.QuadTree;
import framework.drawing.DrawManager;
import framework.drawing.Framebuffer2D;
import framework.drawing.Program;
import framework.drawing.UnitSquare;
import framework.drawing.textures.SolidTexture;
import framework.drawing.textures.Texture2D;
import framework.math3d.*;
import framework.math3d.primitives.BoundedPlane;
import framework.math3d.primitives.IntersectionHandler;
import framework.math3d.primitives.Ray;

import static JGL.JGL.*;
import static JSDL.JSDL.*;
import static framework.math3d.math3d.normalize;

/**
 * Created by Michael on 1/15/2016.
 */
public class GameScreen implements Screen
{

    Player mPlayer;
    public static InputHandler mInput = new InputHandler("config/cfg.properties");

    public static float SCALE = 5.0f;

    private boolean mPaused;
    Camera cam;
    Program blurprog, kinematicsprog;
    float elapsed, framenum;
    //    UnitSquare usq;
    //    ImageTextureArray ita;
    Wall wall, wall2, wall3;
    Player player;
    Floor floor;
    Level level;
    QuadTree colTree;
    Framebuffer2D shadowFBO = new Framebuffer2D(1024, 1024, GL_R32F, GL_FLOAT);
    private Texture2D mDummyTexture = new SolidTexture(GL_FLOAT, 0.0f, 0.0f, 0.0f, 0.0f);
    private UnitSquare debugSquare = new UnitSquare();
    private Program mSquareDraw = new Program("shaders/blurvs.txt", "shaders/usquarefs.glsl");

    public GameScreen()
    {
        mPaused = false;
        level = new Level(new vec2(50, 50), .55f);
        colTree = new QuadTree(-25f, -25f, 50f);
        for(int i = 0; i < level.mWalls.size(); i++){
            colTree.add(level.mWalls.get(i));
        }

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
        colTree.add(player);
        cam.lookAtPlayer(player);
        //        cam.lookAtPlayer(player);
        //        cam.lookAt(new vec3(0, 4, 0), new vec3(), normalize(new vec3(0, 0, -1)));

        framenum = 0.0f;

        kinematicsprog = new Program("shaders/kinematicsvs.glsl", "shaders/withshadowfs.glsl");
    }

    @Override
    public void update(long dtime)
    {
        mInput.poll();
        elapsed = ((float) dtime) / 1000;

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

//        for (int i = 0; i < level.mTiles.length; i++)
//        {
//            for (int j = 0; j < level.mTiles[i].length; j++)
//            {
//                if (level.mTiles[i][j] instanceof Wall)
//                {
//                    CollisionHandler.pushApartAABB(level.mTiles[i][j], player);
//                }
//            }
//        }
        colTree.update(player);
        colTree.handleCollisions();

        player.update(elapsed);

        cam.lookAtPlayer(player);

    }

    @Override
    public void draw(Program program)
    {
        program.setUniform("lightPos", new vec3(3, 6, 3));
        kinematicsprog.use();
        kinematicsprog.setUniform("lightPos", new vec3(3, 6, 3));
        program.use();

        drawShadows(program);

//        drawShadowBuffer(program);

        drawItems(program);
        program.use();
    }

    public void drawShadowBuffer(Program program)
    {
        mSquareDraw.use();
        mSquareDraw.setUniform("toDisplay", shadowFBO.texture);
        debugSquare.draw(mSquareDraw);
        mSquareDraw.setUniform("toDisplay", mDummyTexture);
        program.use();
    }

    public void drawItems(Program program)
    {
        cam.lookAtPlayer(player);

        DrawManager.getInstance().drawMirrorFloors(program, kinematicsprog, cam, level, player, shadowFBO);
        cam.draw(program);
        kinematicsprog.use();
        cam.draw(kinematicsprog);
        player.draw(kinematicsprog);
        program.use();

        drawLaplacian(program);

    }

    public void drawLaplacian(Program program)
    {
        glStencilFunc(GL_ALWAYS, 1, 0xff);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        program.setUniform("shadow_texture", shadowFBO.texture);

        level.drawWalls(program);

        kinematicsprog.use();
        glStencilFunc(GL_EQUAL, 1, 0xff);
        glStencilOp(GL_KEEP, GL_INCR, GL_KEEP);
        player.draw(kinematicsprog);

        glStencilFunc(GL_LEQUAL, 2, 0xff); // Reference less than or equal to buffer. 2 <= 2 so works! 2 <= 3 works!
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glClear(GL_DEPTH_BUFFER_BIT);

        DrawManager.getInstance().drawLaplacian(player, kinematicsprog, null, new vec4(0.5, 1.0, 0.5, 0.5), new vec4(0.5, 0.1, 0.1, 0.5));

        glStencilFunc(GL_ALWAYS, 0, 0xff);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public void drawShadows(Program program)
    {
        kinematicsprog.use();
        kinematicsprog.setUniform("shadow_texture", mDummyTexture);
        program.use();
        program.setUniform("shadow_texture", mDummyTexture);
        cam.lookAt(new vec3(3, 6, 3), new vec3(0, 0, 0), new vec3(0, 1, 0));
        DrawManager.getInstance().drawShadowBuffer(program, cam, shadowFBO, level, player);
        program.setUniform("lightViewMatrix", cam.getViewMatrix());
        program.setUniform("lightProjMatrix", cam.compute_projp_matrix());
        program.setUniform("lightHitherYon", new vec4(cam.hither, cam.yon, cam.yon - cam.hither, GameScreen.SCALE));
        program.setUniform("shadow_texture", shadowFBO.texture);

        kinematicsprog.use();
        kinematicsprog.setUniform("lightViewMatrix", cam.getViewMatrix());
        kinematicsprog.setUniform("lightProjMatrix", cam.compute_projp_matrix());
        kinematicsprog.setUniform("lightHitherYon", new vec4(cam.hither, cam.yon, cam.yon - cam.hither, GameScreen.SCALE));
        kinematicsprog.setUniform("shadow_texture", shadowFBO.texture);

        program.use();
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

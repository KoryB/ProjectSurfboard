package framework.drawing;

import framework.*;
import framework.drawing.textures.SolidTexture;
import framework.drawing.textures.Texture2D;
import framework.math3d.*;

import static JGL.JGL.*;

public class DrawManager
{
    private static DrawManager mInstance = new DrawManager();
    private static final float[] LAPLACIAN_WEIGHTINGS = new float[]{0, -1, 0, -1, 2.0f, -1, 0, -1, 0};
    //    private static final float[] LAPLACIAN_WEIGHTINGS = new float[]{0, -1, 0, -1, 4, -1, 0, -1, 0};
    private static int NEXT_AVAILABLE_FBO = 0;

    private Program mBlurProgram, mEdgeProgram, mShadowProgram, mNonShadowProgram, mPlayerShadowProgram;
    //    private Framebuffer2D tFBO1, tFBO2;
    private Framebuffer2D[] tFBOArray;
    private UnitSquare mUnitSquare = new UnitSquare();
    private Texture2D mDummyTexture = new SolidTexture(GL_FLOAT, 0.0f, 0.0f, 0.0f, 0.0f);

    private Noise overlayNoise = new Noise();

    private DrawManager()
    {
        mBlurProgram = new Program("shaders/blurvs.txt", "shaders/blurfs.txt");
        mEdgeProgram = new Program("shaders/blurvs.txt", "shaders/edgefs.txt");
        mShadowProgram = new Program("shaders/shadowvs.glsl", "shaders/shadowfs.glsl");
        mPlayerShadowProgram = new Program("shaders/kinematicshadowvs.glsl", "shaders/shadowfs.glsl");
        mNonShadowProgram = new Program("shaders/vs.txt", "shaders/fs.txt");

        tFBOArray = new Framebuffer2D[10];
        for (int i = 0; i < tFBOArray.length; i++)
        {
            tFBOArray[i] = new Framebuffer2D(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
        }

    }

    public static DrawManager getInstance()
    {
        return mInstance;
    }

    public void drawBlurScreen(Drawable toDraw, Program originalProgram, Framebuffer2D renderTarget, int numTimes, int size)
    {
        int myAvailableFBO = NEXT_AVAILABLE_FBO;
        NEXT_AVAILABLE_FBO += 2;

        originalProgram.use();
        tFBOArray[myAvailableFBO].bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        toDraw.draw(originalProgram);
        tFBOArray[myAvailableFBO].unbind();
        mBlurProgram.use();
        mBlurProgram.setUniform("boxWidth", size);
        mBlurProgram.setUniform("depth_texture", tFBOArray[myAvailableFBO].depthtexture);

        tFBOArray[myAvailableFBO+1].bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        tFBOArray[myAvailableFBO+1].unbind();

        for (int i = 0; i < numTimes; i++)
        {
            tFBOArray[myAvailableFBO+1].bind();

            mBlurProgram.setUniform("toBlur", tFBOArray[myAvailableFBO].texture);
            mBlurProgram.setUniform("depth_texture", tFBOArray[myAvailableFBO].depthtexture);
            mBlurProgram.setUniform("blurDelta", new vec2(0.0, 1.0));
            mUnitSquare.draw(mBlurProgram);

            tFBOArray[myAvailableFBO+1].unbind();
            mBlurProgram.setUniform("toBlur", tFBOArray[myAvailableFBO+1].texture);
            mBlurProgram.setUniform("depth_texture", tFBOArray[myAvailableFBO+1].depthtexture);
            if (i != numTimes - 1)
            {
                tFBOArray[myAvailableFBO].bind();
            } else
            {
                if (renderTarget != null)
                {
                    renderTarget.bind();
                }
            }
            mBlurProgram.setUniform("blurDelta", new vec2(1.0, 0.0));
            mUnitSquare.draw(mBlurProgram);

            if (i != numTimes - 1)
            {
                tFBOArray[myAvailableFBO].unbind();
            } else
            {
                if (renderTarget != null)
                {
                    renderTarget.unbind();
                }
            }
            mBlurProgram.setUniform("toBlur", mDummyTexture);
            mBlurProgram.setUniform("depth_texture", mDummyTexture);
        }
        originalProgram.use();

        NEXT_AVAILABLE_FBO -=2;
    }

    public void drawLaplacian(Drawable toDraw, Program originalProgram, Framebuffer2D renderTarget, vec4 onColor, vec4 offColor)
    {
        int myAvailableFBO = NEXT_AVAILABLE_FBO;
        NEXT_AVAILABLE_FBO += 1;

        /*TODO: The problem is that the depth buffer in the FBO is all 0's, but the current reference value is 2. 2<=0 is false!
        **TODO: glGetIntegerV(GL_STENCIL_OP, byte[])
        **TODO: int val = b[0] + b[1]*256 + b[2]*65536 + b[3]*16777216
        **TODO: this will get the current stencil func, etc. Could be used. might not be worth so just leaving it here.
        */

        originalProgram.use();
        tFBOArray[myAvailableFBO].bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glStencilFunc(GL_ALWAYS, 0, 0xff);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        toDraw.draw(originalProgram);
        tFBOArray[myAvailableFBO].unbind();

        if (renderTarget != null)
        {
            renderTarget.bind();
        }

        mEdgeProgram.use();
        mEdgeProgram.setUniform("onColor", onColor);
        mEdgeProgram.setUniform("offColor", offColor);
        mEdgeProgram.setUniform("weightings[0]", LAPLACIAN_WEIGHTINGS);
        mEdgeProgram.setUniform("toEdge", tFBOArray[myAvailableFBO].texture);
        mEdgeProgram.setUniform("depth_texture", tFBOArray[myAvailableFBO].depthtexture);

        glStencilFunc(GL_LEQUAL, 2, 0xff);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

        mUnitSquare.draw(mEdgeProgram);

        if (renderTarget != null)
        {
            renderTarget.unbind();
        }

        mEdgeProgram.setUniform("toEdge", mDummyTexture);
        mEdgeProgram.setUniform("depth_texture", mDummyTexture);

        originalProgram.use();

        NEXT_AVAILABLE_FBO -= 1;
    }

    //Will eventually need to have a list of drawables that cast shadows.
    public void drawShadowBuffer(Program originalProgram, Camera camInUse, Framebuffer2D shadowFBO, Level level, Player player)
    {
        shadowFBO.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        shadowFBO.unbind();

        int myAvailableFBO = NEXT_AVAILABLE_FBO;
        NEXT_AVAILABLE_FBO += 2;
        final int BLUR_TIMES = 3;

//        camInUse.lookAt(new vec3(50, 50, 50), new vec3(0, 0, 0), new vec3(0, 1, 0));
        mShadowProgram.use();
        mShadowProgram.setUniform("viewMatrix", camInUse.getViewMatrix());
        mShadowProgram.setUniform("projMatrix", camInUse.compute_projp_matrix());
        mShadowProgram.setUniform("hitheryon", new vec4(camInUse.hither, camInUse.yon, camInUse.yon - camInUse.hither, GameScreen.SCALE));

        tFBOArray[myAvailableFBO].bind();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        level.drawWalls(mShadowProgram);
        //level.drawFloors(mShadowProgram);

        mPlayerShadowProgram.use();
        mPlayerShadowProgram.setUniform("viewMatrix", camInUse.getViewMatrix());
        mPlayerShadowProgram.setUniform("projMatrix", camInUse.compute_projp_matrix());
        mPlayerShadowProgram.setUniform("hitheryon", new vec4(camInUse.hither, camInUse.yon, camInUse.yon - camInUse.hither, GameScreen.SCALE));
        player.draw(mPlayerShadowProgram);
        tFBOArray[myAvailableFBO].unbind();

        mBlurProgram.use();
        mBlurProgram.setUniform("boxWidth", 3);
        mBlurProgram.setUniform("depth_texture", tFBOArray[myAvailableFBO].depthtexture);

        tFBOArray[myAvailableFBO+1].bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        tFBOArray[myAvailableFBO+1].unbind();

        for (int i = 0; i < BLUR_TIMES; i++)
        {
            tFBOArray[myAvailableFBO+1].bind();

            mBlurProgram.setUniform("toBlur", tFBOArray[myAvailableFBO].texture);
            mBlurProgram.setUniform("depth_texture", tFBOArray[myAvailableFBO].depthtexture);
            mBlurProgram.setUniform("blurDelta", new vec2(0.0, 1.0));
            mUnitSquare.draw(mBlurProgram);

            tFBOArray[myAvailableFBO+1].unbind();
            mBlurProgram.setUniform("toBlur", tFBOArray[myAvailableFBO+1].texture);
            mBlurProgram.setUniform("depth_texture", tFBOArray[myAvailableFBO+1].depthtexture);
            if (i != BLUR_TIMES - 1)
            {
                tFBOArray[myAvailableFBO].bind();
            } else
            {
                shadowFBO.bind();
            }
            mBlurProgram.setUniform("blurDelta", new vec2(1.0, 0.0));
            mUnitSquare.draw(mBlurProgram);

            if (i != BLUR_TIMES - 1)
            {
                tFBOArray[myAvailableFBO].unbind();
            } else
            {
                shadowFBO.unbind();
            }
            mBlurProgram.setUniform("toBlur", mDummyTexture);
            mBlurProgram.setUniform("depth_texture", mDummyTexture);
        }
        originalProgram.use();

        NEXT_AVAILABLE_FBO -=2;
    }
    public void drawMirrorFloors(Program originalProgram, Program playerProgram, Camera camInUse, Level level, Player player, Framebuffer2D shadowFBO){
        int myAvailableFBO = NEXT_AVAILABLE_FBO;
        NEXT_AVAILABLE_FBO += 1;

        originalProgram.use();

        tFBOArray[myAvailableFBO].bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        mat4 flipMatrix = new mat4(new vec4(1, 0, 0, 0),
                new vec4(0, -1, 0, 0),
                new vec4(0, 0, 1, 0),
                new vec4(0, 0, 0, 1));

        //Draw reflection to FBO
        glFrontFace(GL_CW);
        playerProgram.use();
        camInUse.drawWithAdditionalMatrix(playerProgram, flipMatrix);
        player.draw(playerProgram);
        originalProgram.use();
        originalProgram.setUniform("shadow_texture", shadowFBO.texture);
        camInUse.drawWithAdditionalMatrix(originalProgram, flipMatrix);
        level.drawAllExceptFloor(originalProgram);
        tFBOArray[myAvailableFBO].unbind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glFrontFace(GL_CCW);

        //Set up stencil buffer
        glStencilFunc(GL_ALWAYS, 1, ~0);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glDepthMask(false);
        glColorMask(false, false, false, false);

        mNonShadowProgram.use();
        //Draw floor into stencil buffer

        camInUse.draw(mNonShadowProgram);
        level.drawFloors(mNonShadowProgram);

        //Draw only where stencil value == 1
        glColorMask(true, true, true, true);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glStencilFunc(GL_EQUAL, 1, ~0);

        //Set matricies to identity, draw unit square with reflection texture, then reset matricies & turn depth buffer back on
        mNonShadowProgram.setUniform("diffuse_texture", tFBOArray[myAvailableFBO].texture);
        mNonShadowProgram.setUniform("worldMatrix", mat4.identity());
        mNonShadowProgram.setUniform("viewMatrix", mat4.identity());
        mNonShadowProgram.setUniform("projMatrix", mat4.identity());
        mUnitSquare.draw(mNonShadowProgram);
        mNonShadowProgram.setUniform("diffuse_texture", mDummyTexture);
        glDepthMask(true);

        originalProgram.use();
        camInUse.draw(originalProgram);

        //draw the floors and everything else
        if (player.getGotoPoint() != null)
        {
            overlayNoise.draw(originalProgram);
            originalProgram.setUniform("overlayCenter", player.getGotoPoint());
        }
        else
        {
            overlayNoise.draw(originalProgram);
            originalProgram.setUniform("overlayCenter", new vec4(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, 1));
        }
//        originalProgram.setUniform("shadow_texture", shadowFBO.texture);
        level.drawFloors(originalProgram);
        originalProgram.setUniform("overlayCenter", new vec4(0, 0, 0, 0));
        glStencilFunc(GL_ALWAYS, 1, ~0);

        originalProgram.use();
        glClear(GL_STENCIL_BUFFER_BIT);

        NEXT_AVAILABLE_FBO -= 1;
    }
}


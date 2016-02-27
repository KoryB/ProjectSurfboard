package framework.drawing;

import framework.*;
import framework.drawing.textures.SolidTexture;
import framework.drawing.textures.Texture;
import framework.drawing.textures.Texture2D;
import framework.math3d.*;

import static JGL.JGL.*;

public class DrawManager
{
    private static DrawManager mInstance = new DrawManager();
    private static final float[] LAPLACIAN_WEIGHTINGS = new float[]{0, -1, 0, -1, 2.0f, -1, 0, -1, 0};
//    private static final float[] LAPLACIAN_WEIGHTINGS = new float[]{0, -1, 0, -1, 4, -1, 0, -1, 0};
    private static int NEXT_AVAILABLE_FBO = 0;

    private Program mBlurProgram, mEdgeProgram, mShadowProgram;
//    private Framebuffer tFBO1, tFBO2;
    private Framebuffer[] tFBOArray;
    private UnitSquare mUnitSquare = new UnitSquare();
    private Texture2D mDummyTexture = new SolidTexture(GL_FLOAT, 0.0f, 0.0f, 0.0f, 0.0f);

    private DrawManager()
    {
        mBlurProgram = new Program("shaders/blurvs.txt", "shaders/blurfs.txt");
        mEdgeProgram = new Program("shaders/blurvs.txt", "shaders/edgefs.txt");
        mShadowProgram = new Program("shaders/shadowvs.glsl", "shaders/shadowfs.glsl");
//        tFBO1 = new Framebuffer(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
//        tFBO2 = new Framebuffer(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
        tFBOArray = new Framebuffer[10];
        for (int i = 0; i < tFBOArray.length; i++)
        {
            tFBOArray[i] = new Framebuffer(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
        }
//        tFBOArray = new Framebuffer[]{tFBO1, tFBO2};
        
    }

    public static DrawManager getInstance()
    {
        return mInstance;
    }

    public void drawBlurScreen(Drawable toDraw, Program originalProgram, Framebuffer renderTarget, int numTimes, int size)
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

    public void drawLaplacian(Drawable toDraw, Program originalProgram, Framebuffer renderTarget, vec4 onColor, vec4 offColor)
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
    public void drawShadowBuffer(Program originalProgram, Camera camInUse, Framebuffer shadowFBO, Level level, Player player)
    {
        camInUse.lookAt(new vec3(50, 50, 50), new vec3(0, 0, 0), new vec3(0, 1, 0));
        mShadowProgram.use();
        mShadowProgram.setUniform("viewMatrix", camInUse.getViewMatrix());
        mShadowProgram.setUniform("projMatrix", camInUse.compute_projp_matrix());
        mShadowProgram.setUniform("hitheryon", new vec3(camInUse.getHither(), camInUse.getYon(), camInUse.getYon() - camInUse.getHither()));
        shadowFBO.bind();
        level.drawWalls(mShadowProgram);
        player.draw(mShadowProgram);
        shadowFBO.unbind();
        originalProgram.use();
    }

    public void drawMirrorFloors(Program originalProgram, Camera camInUse, Level level, Player player){
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
        camInUse.drawWithAdditionalMatrix(originalProgram, flipMatrix);
        player.draw(originalProgram);
        level.drawAllExceptFloor(originalProgram);
        tFBOArray[myAvailableFBO].unbind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glFrontFace(GL_CCW);

        //Set up stencil buffer
        glStencilFunc(GL_ALWAYS, 1, ~0);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glDepthMask(false);
        glColorMask(false, false, false, false);

        //Draw floor into stencil buffer
        camInUse.draw(originalProgram);
        level.drawFloors(originalProgram);

        //Draw only where stencil value == 1
        glColorMask(true, true, true, true);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glStencilFunc(GL_EQUAL, 1, ~0);

        //Set matricies to identity, draw unit square with reflection texture, then reset matricies & turn depth buffer back on
        originalProgram.setUniform("diffuse_texture", tFBOArray[myAvailableFBO].texture);
        originalProgram.setUniform("worldMatrix", mat4.identity());
        originalProgram.setUniform("viewMatrix", mat4.identity());
        originalProgram.setUniform("projMatrix", mat4.identity());
        mUnitSquare.draw(originalProgram);
        glDepthMask(true);
        camInUse.draw(originalProgram);

        //draw the floors and everything else
        level.drawFloors(originalProgram);
        glStencilFunc(GL_ALWAYS, 1, ~0);
        player.draw(originalProgram);
        level.drawAllExceptFloor(originalProgram);

        originalProgram.use();
        glClear(GL_STENCIL_BUFFER_BIT);

        NEXT_AVAILABLE_FBO -= 1;
    }
}


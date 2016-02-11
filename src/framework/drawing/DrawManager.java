package framework.drawing;

import framework.Floor;
import framework.Util;
import framework.drawing.textures.SolidTexture;
import framework.drawing.textures.Texture;
import framework.drawing.textures.Texture2D;
import framework.math3d.vec2;
import framework.math3d.vec4;

import static JGL.JGL.*;

public class DrawManager
{
    private static DrawManager mInstance = new DrawManager();
    private static final float[] LAPLACIAN_WEIGHTINGS = new float[]{-1, -1, -1, -1, 8.2f, -1, -1, -1, -1};
//    private static final float[] LAPLACIAN_WEIGHTINGS = new float[]{0, -1, 0, -1, 4, -1, 0, -1, 0};
    private static int NEXT_AVAILABLE_FBO = 0;

    private Program mBlurProgram, mEdgeProgram;
//    private Framebuffer tFBO1, tFBO2;
    private Framebuffer[] tFBOArray;
    private UnitSquare mUnitSquare = new UnitSquare();
    private Texture2D mDummyTexture = new SolidTexture(GL_FLOAT, 0.0f, 0.0f, 0.0f, 0.0f);

    private DrawManager()
    {
        mBlurProgram = new Program("shaders/blurvs.txt", "shaders/blurfs.txt");
        mEdgeProgram = new Program("shaders/blurvs.txt", "shaders/edgefs.txt");
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
}


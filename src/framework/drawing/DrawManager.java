package framework.drawing;

import framework.Floor;
import framework.Util;
import framework.drawing.textures.SolidTexture;
import framework.drawing.textures.Texture;
import framework.drawing.textures.Texture2D;
import framework.math3d.vec2;

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

        tFBOArray[myAvailableFBO+1].bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        tFBOArray[myAvailableFBO+1].unbind();

        for (int i = 0; i < numTimes; i++)
        {
            tFBOArray[myAvailableFBO+1].bind();

            mBlurProgram.setUniform("toBlur", tFBOArray[myAvailableFBO].texture);
            mBlurProgram.setUniform("blurDelta", new vec2(0.0, 1.0));
            mUnitSquare.draw(mBlurProgram);

            tFBOArray[myAvailableFBO+1].unbind();
            mBlurProgram.setUniform("toBlur", tFBOArray[myAvailableFBO+1].texture);
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
        }
        originalProgram.use();

        NEXT_AVAILABLE_FBO -=2;
    }

    public void drawLaplacian(Drawable toDraw, Program originalProgram, Framebuffer renderTarget)
    {
        int myAvailableFBO = NEXT_AVAILABLE_FBO;
        NEXT_AVAILABLE_FBO += 1;

        originalProgram.use();
        tFBOArray[myAvailableFBO].bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        toDraw.draw(originalProgram);
        tFBOArray[myAvailableFBO].unbind();

        if (renderTarget != null)
        {
            renderTarget.bind();
        }

        mEdgeProgram.use();
        mEdgeProgram.setUniform("weightings[0]", LAPLACIAN_WEIGHTINGS);
        mEdgeProgram.setUniform("toEdge", tFBOArray[myAvailableFBO].texture);

        mUnitSquare.draw(mEdgeProgram);

        if (renderTarget != null)
        {
            renderTarget.unbind();
        }

        mEdgeProgram.setUniform("toEdge", mDummyTexture);

        NEXT_AVAILABLE_FBO -= 1;
    }
}


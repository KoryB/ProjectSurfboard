package framework.drawing;

import framework.Floor;
import framework.Util;
import framework.drawing.textures.SolidTexture;
import framework.drawing.textures.Texture;
import framework.drawing.textures.Texture2D;
import framework.math3d.vec2;

import static JGL.JGL.*;

/**
 * Created by kory on 2/2/16.
 */
public class DrawManager
{
    private static DrawManager mInstance = new DrawManager();
    private Program mBlurProgram;
    private Framebuffer tFBO1, tFBO2;
    private Framebuffer[] tFBOArray;
    private UnitSquare mUnitSquare = new UnitSquare();
    private Texture2D mDummyTexture = new SolidTexture(GL_FLOAT, 0.0f, 0.0f, 0.0f, 0.0f);

    private DrawManager()
    {
        mBlurProgram = new Program("shaders/blurvs.txt", "shaders/blurfs.txt");
        tFBO1 = new Framebuffer(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
        tFBO2 = new Framebuffer(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
        tFBOArray = new Framebuffer[]{tFBO1, tFBO2};
    }

    public static DrawManager getInstance()
    {
        return mInstance;
    }

    public void drawBlur(Drawable toDraw, Program originalProgram, Framebuffer renderTarget, int numTimes, int size, boolean singleObject)
    {
        originalProgram.use();
        tFBO1.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        toDraw.draw(originalProgram);
        tFBO1.unbind();
        mBlurProgram.use();
        mBlurProgram.setUniform("boxWidth", size);

        tFBO2.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        tFBO2.unbind();

        for (int i = 0; i < numTimes; i++)
        {
            tFBO2.bind();

            mBlurProgram.setUniform("toBlur", tFBO1.texture);
            mBlurProgram.setUniform("blurDelta", new vec2(0.0, 1.0));
            mUnitSquare.draw(mBlurProgram);

            tFBO2.unbind();
            mBlurProgram.setUniform("toBlur", tFBO2.texture);
            if (i != numTimes - 1 || singleObject)
            {
                tFBO1.bind();
            }
            else
            {
//                glClear(GL_DEPTH_BUFFER_BIT);
                if (renderTarget != null)
                {
                    renderTarget.bind();
                }
            }
            mBlurProgram.setUniform("blurDelta", new vec2(1.0, 0.0));
            mUnitSquare.draw(mBlurProgram);

            if (i != numTimes - 1 || singleObject)
            {
                tFBO1.unbind();
            }
            else
            {
                if (renderTarget != null)
                {
                    renderTarget.unbind();
                }
            }
            mBlurProgram.setUniform("toBlur", mDummyTexture);
        }

        if (singleObject)
        {
            originalProgram.use();
            Texture tTex = Floor.MESH.texture;
            Floor.MESH.texture = tFBO1.texture;
            toDraw.draw(originalProgram);
            Floor.MESH.texture = tTex;
        }
    }
}

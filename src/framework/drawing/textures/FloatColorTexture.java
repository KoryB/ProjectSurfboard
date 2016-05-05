package framework.drawing.textures;

import framework.Util;

import static JGL.JGL.GL_FLOAT;
import static JGL.JGL.*;

/**
 *
 * @author jhudson
 */
public class FloatColorTexture extends ColorTexture {

    public FloatColorTexture(int w, int h, int channels){
        super(w,h,GL_FLOAT,channels,
                (channels == 4) ? GL_RGBA32F :
                        (channels == 3) ? GL_RGB32F :
                                (channels == 2 ) ? GL_RG32F :
                                        (channels == 1) ? GL_R32F :
                                                -1);
    }
    public FloatColorTexture(int w, int h, int channels, float[] fdata){
        super(w,h,GL_FLOAT,channels,
                (channels == 4) ? GL_RGBA32F :
                        (channels == 3) ? GL_RGB32F :
                                (channels == 2 ) ? GL_RG32F :
                                        (channels == 1) ? GL_R32F :
                                                -1);
        update(fdata);
    }


    public void update(float[] b){
        if( b.length != w*h*channels)
            throw new RuntimeException("Bad size: Got "+b.length);
        super.update(Util.toByteArray(b));
    }
}
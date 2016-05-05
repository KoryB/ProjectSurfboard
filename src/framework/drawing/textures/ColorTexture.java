/*
 */
package framework.drawing.textures;
import static JGL.JGL.*;

/**
 *
 * @author jhudson
 */
public class ColorTexture extends Texture2D {
    int fmt;
    int channels;

    protected ColorTexture(int w, int h, int fmt, int channels, int ifmt){
        super(w,h);
        if( fmt == -1 )
            throw new RuntimeException("Bad format");
        this.fmt=fmt;
        this.channels=channels;

        bind(0);

        glTexImage2D(GL_TEXTURE_2D,0,
                ifmt,
                w,h,0,
                GL_RGBA,
                fmt,
                null);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);

    }

    protected void update(byte[] b){
        bind(0);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0,0, w,h,
                (channels == 4)? GL_RGBA : GL_RED,
                fmt,b);
    }
}

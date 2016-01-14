/*
 */
package framework;
import static JGL.JGL.*;

/**
 *
 * @author jhudson
 */
public class ColorTexture extends Texture2D {
    int fmt;
    int w,h;
    
    //fmt = GL_FLOAT or GL_UNSIGNED_BYTE
    ColorTexture(int w, int h, int fmt){
        this.w=w;
        this.h=h;
        this.fmt=fmt;
        if(  fmt != GL_UNSIGNED_BYTE && fmt != GL_FLOAT )
            throw new RuntimeException("Bad format");
        
        bind(0);
        glTexImage2D(GL_TEXTURE_2D,0,(fmt == GL_FLOAT) ? GL_RGBA32F : GL_RGBA,
            w,h,0,
            GL_RGBA,
            fmt,
            null);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
        
    }
    
    void update(byte[] b){
        if( b.length != w*h*4*(fmt == GL_FLOAT ? 4 : 1 ))
            throw new RuntimeException("Bad size: Got "+b.length+" but expected "+w+"*"+h+"*4="+w*h*4);
        
        bind(0);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0,0, w,h, GL_RGBA,fmt,b);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;
import static JGL.JGL.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class SolidTexture extends Texture2D{
    int fmt;
    int w=1, h=1;
    //fmt = GL_FLOAT or GL_UNSIGNED_BYTE
    SolidTexture(int fmt, float r, float g, float b, float a){
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
        if( fmt == GL_FLOAT ){
            ByteBuffer bb = ByteBuffer.allocate(16);
            FloatBuffer fb = bb.asFloatBuffer();
            float[] f = new float[]{r,g,b,a};
            fb.put(f);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0,0, w,h, GL_RGBA,fmt,bb.array());
        }
        else{
            byte[] bb = new byte[]{ (byte)(r*255), (byte)(g*255), (byte)(b*255), (byte)(a*255) };
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0,0, w,h, GL_RGBA,fmt,bb);
        }
    }
}
 

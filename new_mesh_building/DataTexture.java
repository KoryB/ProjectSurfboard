/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import static JGL.JGL.GL_CLAMP_TO_EDGE;
import static JGL.JGL.GL_FLOAT;
import static JGL.JGL.GL_NEAREST;
import static JGL.JGL.GL_RGBA;
import static JGL.JGL.GL_RGBA32F;
import static JGL.JGL.GL_TEXTURE_2D;
import static JGL.JGL.GL_TEXTURE_MAG_FILTER;
import static JGL.JGL.GL_TEXTURE_MIN_FILTER;
import static JGL.JGL.GL_TEXTURE_WRAP_S;
import static JGL.JGL.GL_TEXTURE_WRAP_T;
import static JGL.JGL.GL_UNSIGNED_BYTE;
import static JGL.JGL.glTexImage2D;
import static JGL.JGL.glTexParameteri;
import static JGL.JGL.glTexSubImage2D;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
 

public class DataTexture extends Texture2D{
    DataTexture(int w, int h, float[] data){
        super(w,h);
        init();
        update(data);
    }
    DataTexture(int w, int h, byte[] data){
        super(w,h);
        init();
        update(data);
    }
    
    private void init(){
        bind(0);
        glTexImage2D(GL_TEXTURE_2D,
            0, 
            GL_RGBA32F ,
            w,h,0,
            GL_RGBA,
            GL_FLOAT,
            null);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
    }
    
    void update(float[] b){
        if( b.length != w*h*4)
            throw new RuntimeException("Bad size: Got "+b.length+" but expected "+w+"*"+h+"*4="+w*h*4);
        ByteBuffer bb = ByteBuffer.allocate(b.length*4);
        bb.order(ByteOrder.nativeOrder());
        bb.asFloatBuffer().put(b);
        bind(0);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0,0, w,h, GL_RGBA,GL_FLOAT,bb.array());
    }
    
    //assumes b points to blob of floats
    void update(byte[] b){
        if( b.length != w*h*4*4)
            throw new RuntimeException("Bad size: Got "+b.length+" but expected "+w+"*"+h+"*4*4="+w*h*4*4);
        bind(0);
        
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        float[] x = new float[b.length/4];
        fb.get(x);
        
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0,0, w,h, GL_RGBA,GL_FLOAT,b);
    }
    
}

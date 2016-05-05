/*
 */
package framework.drawing;

import framework.drawing.textures.Texture;
import framework.drawing.textures.Texture2D;

import static JGL.JGL.*;

/**
 *
 * @author jhudson
 */
public class Framebuffer2D extends FramebufferObject {
    int width,height;       //size of FBO
    public Texture2D texture;      //alias for textures[0]
    Texture2D depthtexture;         //depth texture (z buffer) + stencil
    int fbo;                //GL identifier
    
    public Framebuffer2D(int width, int height){
        init(width,height,GL_RGBA,GL_UNSIGNED_BYTE,1);
    }
   
    public Framebuffer2D(int width, int height, int format, int type){
        init(width,height,format,type,1);
    }

    public Framebuffer2D(int width, int height, int format, int type, int count){
        init(width,height,format,type,count);
    }

    private void init(int width, int height, int format, int type, int count){
        
        this.width=width;
        this.height=height;
        
        textures = new Texture2D[count];
        for(int i=0;i<count;++i){
            textures[i] = new Texture2D(width,height);
            textures[i].bind(0);
            glTexImage2D(GL_TEXTURE_2D, 0, format, width,height,0, GL_RGBA, type, null );
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            textures[i].unbind();
        }
        texture = textures[0];
        depthtexture = new Texture2D(width, height);
        depthtexture.bind(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width,height,0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, null );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        int[] tmp = new int[1];
        glGenFramebuffers(1,tmp);
        fbo = tmp[0];
        glBindFramebuffer(GL_FRAMEBUFFER, fbo );
        for(int i=0;i<count;++i){
            glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, textures[i].getId(), 0 );
        }
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, depthtexture.getId(), 0 );
        
        int complete = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if( complete != GL_FRAMEBUFFER_COMPLETE ){
            throw new RuntimeException("Framebuffer2D is not complete: "+complete);
        }
        
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        
    }
    
    public void bind(){
        if( active_fbo != null )
            unbind();
        checkOkToBind();
        active_fbo = this;
        glBindFramebuffer(GL_FRAMEBUFFER,fbo);
        glGetIntegerv(GL_VIEWPORT,viewport);
        glViewport(0,0,width,height);
    }
    

    
}

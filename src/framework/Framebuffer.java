/*
 */
package framework;
import static JGL.JGL.*;

/**
 *
 * @author jhudson
 */
public class Framebuffer {
    int width,height;       //size of FBO
    Texture2D[] textures;   //all the textures for the fbo
    Texture2D texture;      //alias for textures[0]
    Texture2D depthtexture;         //depth texture (z buffer) + stencil
    int fbo;                //GL identifier
    static Framebuffer active_fbo;  //tells which FBO is currently active, or null if none
    static int[] viewport = new int[4];     //viewport that was active before 
    
    
    public Framebuffer(int width, int height){
        init(width,height,GL_RGBA,GL_UNSIGNED_BYTE,1);
    }
   
    public Framebuffer(int width, int height, int format, int type){
        init(width,height,format,type,1);
    }

    public Framebuffer(int width, int height, int format, int type, int count){
        init(width,height,format,type,count);
    }

    private void init(int width, int height, int format, int type, int count){
        
        this.width=width;
        this.height=height;
        
        textures = new Texture2D[count];
        for(int i=0;i<count;++i){
            textures[i] = new Texture2D();
            textures[i].bind(0);
            glTexImage2D(GL_TEXTURE_2D, 0, format, width,height,0, format, type, null );
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            textures[i].unbind();
        }
        texture = textures[0];
        depthtexture = new Texture2D();
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
            throw new RuntimeException("Framebuffer is not complete: "+complete);
        }
        
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        
    }
    
    public void bind(){
        if( active_fbo != null )
            throw new RuntimeException("Another FBO is already bound");
        for(int i=0;i<textures.length;++i){
            if( !textures[i].on_units.isEmpty() ){
                String tmp="";
                for(Integer j : textures[i].on_units){
                    if( Texture.active_textures[j] != textures[i] ){
                        throw new RuntimeException("Internal consistency error: "+
                                    Texture.active_textures[j]+" "+textures[i]);
                    }
                    tmp += " "+j;{
                }
            }
                throw new RuntimeException("This FBO has textures that are active on units: "+tmp);
            }
        }
        
        active_fbo = this;
        glBindFramebuffer(GL_FRAMEBUFFER,fbo);
        glGetIntegerv(GL_VIEWPORT,viewport);
        glViewport(0,0,width,height);
    }
    
    public void unbind(){
        if( active_fbo != this )
            throw new RuntimeException("This FBO is not bound");
        active_fbo=null;
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glViewport(viewport[0],viewport[1],viewport[2],viewport[3]);
    }
    
}

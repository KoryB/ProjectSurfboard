/*
 */
package framework.drawing;
import framework.drawing.textures.CubeTexture;
import framework.drawing.textures.Texture;
import framework.drawing.textures.Texture2D;

import static JGL.JGL.*;

public class FramebufferCube extends FramebufferObject {
    int size;       //size of FBO sides; width = height
    int[] fbos = new int[6];    //one fbo for each side of cube map
    CubeTexture texture;
    Texture2D[] depthtextures = new Texture2D[6];
    
    public FramebufferCube(int size){
        init(size,GL_RGBA,GL_UNSIGNED_BYTE);
    }
   
    public FramebufferCube(int size, int format, int type){
        init(size,format,type);
    }

    private void init(int size, int format, int type){
        this.size=size;
        texture = new CubeTexture(size);
        textures = new Texture[]{texture};
        texture.bind(0);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        
        //must generate textures first, then attach to fbo
        for(int j=0;j<6;++j){
            int target = GL_TEXTURE_CUBE_MAP_POSITIVE_X+j;
            glTexImage2D(target, 0, format, size,size,0, GL_RGBA, type, null );
        }
        
        for(int j=0;j<6;++j){
            
            int[] tmp = new int[1];
            glGenFramebuffers(1,tmp);
            fbos[j] = tmp[0];
            
            glBindFramebuffer(GL_FRAMEBUFFER, fbos[j] );
            
            int target = GL_TEXTURE_CUBE_MAP_POSITIVE_X+j;
            glFramebufferTexture2D(
                    GL_FRAMEBUFFER, 
                    GL_COLOR_ATTACHMENT0, 
                    target,                 //GL_TEXTURE_CUBE_MAP_{POSITIVE,NEGATIVE}_{X,Y,Z}
                    texture.getId(), 0);    //texture, level
            
            depthtextures[j] = new Texture2D(size,size);
            depthtextures[j].bind(0);   //binds to the 2d attachment point, not the cube point
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, size,size,0,
                    GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, null );
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glFramebufferTexture(GL_FRAMEBUFFER, 
                    GL_DEPTH_STENCIL_ATTACHMENT, depthtextures[j].getId(), 0 );
        
            int complete = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            if( complete != GL_FRAMEBUFFER_COMPLETE ){
                throw new RuntimeException("Framebuffer2D is not complete: "+complete);
            }
        
            glBindFramebuffer(GL_FRAMEBUFFER,0);
            
        }
        
    }
    
    //face should be GL_TEXTURE_CUBE_MAP_{POSITIVE,NEGATIVE}_{X,Y,Z}
    public void bind(int face){
        if( active_fbo != null )
            unbind();
        checkOkToBind();
        active_fbo = this;
        int idx = face - GL_TEXTURE_CUBE_MAP_POSITIVE_X;
        glBindFramebuffer(GL_FRAMEBUFFER,fbos[idx]);
        glGetIntegerv(GL_VIEWPORT,viewport);
        glViewport(0,0,size,size);
    }
}

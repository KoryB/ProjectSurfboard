
package framework.drawing;

//generic base for framebuffers

import framework.drawing.textures.Texture;

import static JGL.JGL.GL_FRAMEBUFFER;
import static JGL.JGL.glBindFramebuffer;
import static JGL.JGL.glViewport;

public class FramebufferObject {
    public static FramebufferObject active_fbo;  //tells which FBO is currently active, or null if none
    public static int[] viewport = new int[4];     //viewport that was active before
    public Texture[] textures; //initialized by subclass

    protected void checkOkToBind(){
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
        
    }
    public void unbind(){
        if( active_fbo != this )
            throw new RuntimeException("This FBO is not bound");
        active_fbo=null;
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glViewport(viewport[0],viewport[1],viewport[2],viewport[3]);
    }
}

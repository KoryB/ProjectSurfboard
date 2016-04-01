/*
 */
package framework;

import java.util.ArrayList;
import java.util.TreeSet;
import static JGL.JGL.*;


/**
 *
 * @author jhudson
 */
public class Texture {
    private int tex;      //GL texture name
    //active_textures[i] tells which texture is used on texture unit i.
    //we impose a limit of 128 units
    static Texture[] active_textures = new Texture[128];   
    private int gltype;  //GL_TEXTURE_2D or similar
    
    //tells which texture units this texture is on
    TreeSet<Integer> on_units = new TreeSet<>();
    
    protected Texture(int textype){
        gltype = textype;
        int[] tmp = new int[2];
        glGenTextures(1,tmp);
        tex = tmp[0];
    }
        
    final public void bind(int unit){
        if( Framebuffer2D.active_fbo != null ){
            for(int i=0;i<Framebuffer2D.active_fbo.textures.length;++i){
                if( Framebuffer2D.active_fbo.textures[i] == this )
                    throw new RuntimeException("This texture is part of an active FBO");
            }
        }
        
        glActiveTexture(GL_TEXTURE0 + unit );
        glBindTexture(gltype,tex);
        if( active_textures[unit] != null )
            active_textures[unit].on_units.remove(unit);
        
        active_textures[unit]=this;
        on_units.add(unit);
    }    
    
    public void unbind(){
        //unbind from all units
        for(Integer i : on_units ){
            glActiveTexture( GL_TEXTURE0 + i );
            glBindTexture(gltype,0);
            active_textures[i] = null;
        }
        on_units.clear();
    }
    
    //don't use this for bind/unbind operations; it will mess up the internal 
    //bookkeeping
    protected int getId(){
        return tex;
    }
    
    static boolean isPowerOf2(int x){
        return  ((x-1)&x) == 0;
    }
    
}

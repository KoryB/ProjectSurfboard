/*
 */
package framework.drawing.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import static JGL.JGL.*;


/**
 *
 * @author jhudson
 */
public class ImageTexture  extends Texture2D{
    
    public ImageTexture(String filename){
        super(0,0);
        byte[] pix;
        try {
            BufferedImage img = ImageIO.read(new File(filename));
            int[] pdata = img.getRGB(0,0,img.getWidth(),img.getHeight(),null,0,img.getWidth()); 
            pix = new byte[pdata.length*4];
            //need to invert the y's 
            int i=0;
            for(int row=0;row<img.getHeight();++row){
                int j= (img.getHeight()-row-1)*img.getWidth()*4;
                for(int k=0;k<img.getWidth();++k){
                    pix[j++] = (byte)((pdata[i] & 0x00ff0000)>>16);
                    pix[j++] = (byte)((pdata[i] & 0x0000ff00)>>8);
                    pix[j++] = (byte)((pdata[i] & 0x000000ff));
                    pix[j++] = (byte)((pdata[i] & 0xff000000)>>24);
                    i++;
                }
            }
            this.w=img.getWidth();
            this.h=img.getHeight();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read image "+filename);
        }

        bind(0);
        
        int fmt = GL_RGBA;

        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,w,h,0,fmt,GL_UNSIGNED_BYTE,
            pix);
        
        if(isPowerOf2(w) && isPowerOf2(h) ){
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        }
        else{
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        }
    }
   
}
        

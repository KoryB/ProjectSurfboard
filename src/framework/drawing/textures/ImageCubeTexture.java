
package framework.drawing.textures;

import static JGL.JGL.GL_CLAMP_TO_EDGE;
import static JGL.JGL.GL_LINEAR;
import static JGL.JGL.GL_LINEAR_MIPMAP_LINEAR;
import static JGL.JGL.GL_REPEAT;
import static JGL.JGL.GL_RGBA;
import static JGL.JGL.GL_TEXTURE_CUBE_MAP;
import static JGL.JGL.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static JGL.JGL.GL_TEXTURE_MAG_FILTER;
import static JGL.JGL.GL_TEXTURE_MIN_FILTER;
import static JGL.JGL.GL_TEXTURE_WRAP_S;
import static JGL.JGL.GL_TEXTURE_WRAP_T;
import static JGL.JGL.GL_UNSIGNED_BYTE;
import static JGL.JGL.glGenerateMipmap;
import static JGL.JGL.glTexImage2D;
import static JGL.JGL.glTexParameteri;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ImageCubeTexture extends CubeTexture{
    
    //+-X, +-Y, +-Z
    public ImageCubeTexture(String[] filenames){
        super(0);
        bind(0);
        int fmt = GL_RGBA;
        
        byte[] pix;
        for(int m=0;m<6;++m){
            try {
                BufferedImage img = ImageIO.read(new File(filenames[m]));
                int[] pdata = img.getRGB(0,0,img.getWidth(),img.getHeight(),null,0,img.getWidth()); 
                pix = new byte[pdata.length*4];
                //need to invert the y's 
                int i=0;
                for(int row=0;row<img.getHeight();++row){
                    //int j= (img.getHeight()-row-1)*img.getWidth()*4;
                    int j = row*img.getWidth()*4;
                    for(int k=0;k<img.getWidth();++k){
                        pix[j++] = (byte)((pdata[i] & 0x00ff0000)>>16);
                        pix[j++] = (byte)((pdata[i] & 0x0000ff00)>>8);
                        pix[j++] = (byte)((pdata[i] & 0x000000ff));
                        pix[j++] = (byte)((pdata[i] & 0xff000000)>>24);
                        i++;
                    }
                }
                if( img.getWidth() != img.getHeight() ){
                    throw new RuntimeException("Cube map images must be square");
                }
                if( img.getWidth() != img.getHeight() )
                    throw new RuntimeException("Image is not square");
                if( this.size != 0 && this.size != img.getWidth() )
                    throw new RuntimeException("Mismatched sizes");
                
                this.size=img.getWidth();
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+m,
                    0,GL_RGBA,size,size,0,fmt,GL_UNSIGNED_BYTE,
                    pix);

            } catch (IOException ex) {
                throw new RuntimeException("Cannot read image "+filenames[m]);
            }
        }

        
        if(isPowerOf2(size) ){
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
            //FIXME
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MIN_FILTER,GL_LINEAR); //_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        }
        else{
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        }
    }
   
}
        


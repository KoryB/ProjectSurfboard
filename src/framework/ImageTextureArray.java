/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import static JGL.JGL.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author jhudson
 */
public class ImageTextureArray extends Texture2D{
    int w,h;
    
    public ImageTextureArray(String filename_pattern, int last){
        ArrayList<Byte> apix = new ArrayList<>();
        for(int fr=0;fr<last;++fr){
            String filename = String.format(filename_pattern,fr);
            BufferedImage img;

            try{
                img = ImageIO.read(new File(filename));
            } catch (IOException ex) {
               throw new RuntimeException("Cannot read image "+filename);
            }
            int[] pdata = img.getRGB(0,0,img.getWidth(),img.getHeight(),null,0,img.getWidth()); 
            byte[] pix = new byte[pdata.length*4];
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
            w=img.getWidth();
            h=img.getHeight();
            for(byte b : pix ){
                apix.add(b);
            }
        }
      
        byte[] bpix = new byte[apix.size()];
        for(int i=0;i<apix.size();++i)
            bpix[i] = apix.get(i);

        bind(0);
        
        int fmt = GL_RGBA;

        glTexImage3D(GL_TEXTURE_2D_ARRAY,0,GL_RGBA,w,h,last,0,fmt,GL_UNSIGNED_BYTE,bpix);
        
        if(this.isPowerOf2(w) && this.isPowerOf2(h) ){
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_S,GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_T,GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        }
        else{
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        }
    }
    boolean isPowerOf2(int x){
        return  ((x-1)&x) == 0;
    }
}    

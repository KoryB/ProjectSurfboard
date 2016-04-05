package framework.drawing.textures;

import static JGL.JGL.*;

public class CubeTexture extends Texture{
    int size;
    public CubeTexture(int size){
        super(GL_TEXTURE_CUBE_MAP);
        this.size=size;
    }
}

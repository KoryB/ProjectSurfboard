package framework.drawing;

import static framework.math3d.math3d.length;
import static framework.math3d.math3d.mul;
import static framework.math3d.math3d.normalize;

import framework.Util;
import framework.drawing.textures.FloatColorTexture;
import framework.math3d.vec3;
import framework.math3d.vec4;
import java.util.Random;

/**
 *
 * @author jhudson
 */
public class Noise {

    FloatColorTexture Ptex;
    FloatColorTexture Gtex;

    public Noise(){
        //make noise data
        float[] P = new float[256];
        for(int i=0;i<256;++i)
            P[i]=i;

        for(int i=0;i<256;++i){
            int idx = (int) Util.randrange(0,256);
            float tmp = P[i];
            P[i]=P[idx];
            P[idx] = tmp;
        }

        vec4[] G = new vec4[256];
        for(int i=0;i<256;++i){
            vec4 v = new vec4( Util.randrange(-1,1),Util.randrange(-1,1),Util.randrange(-1,1), Util.randrange(-1,1) );
            float le = length(v);
            if( le == 0.0 )
                i--;
            else
                G[i] = mul(1.0/le,v);
        }
        for(int i=0;i<256;++i){
            int idx = (int)Util.randrange(0,256);
            vec4 tmp = G[i];
            G[i] = G[idx];
            G[idx]=tmp;
        }

        float[] GG = new float[G.length*4];
        for(int i=0,j=0;i<256;++i){
            GG[j++] = G[i].x;
            GG[j++] = G[i].y;
            GG[j++] = G[i].z;
            GG[j++] = G[i].w;
        }
        Ptex = new FloatColorTexture(256,1,1);
        Ptex.update(P);
        Gtex = new FloatColorTexture(256,1,4);
        Gtex.update(GG);


    }

    void draw(Program prog){
        prog.setUniform("Ptex",Ptex);
        prog.setUniform("Gtex",Gtex);
        //prog.setUniform("noiseScale",1);
    }
}

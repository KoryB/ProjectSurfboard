package framework.drawing;

import static JGL.JGL.*;

import framework.Player;
import framework.Util;
import framework.drawing.textures.ImageTexture;
import framework.math3d.math3d;
import framework.math3d.vec2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Kory Byrne on 04/14/2016.
 */
public class CloudParticles implements Drawable
{
    private ImageTexture texture;
    private int vao;
    private int vbuff;
    private int ibuff;
    private int itype;
    private int isize;

    private Player player;

    public CloudParticles(String textureFile, int numParticles, vec2 center, float radius, Player player)
    {
        init(textureFile, numParticles, player);
    }

    public void init(String textureFile, int numParticles, Player player)
    {
        texture = new ImageTexture(textureFile);
        this.player = player;

        //Create vertex buffer
        ByteBuffer bb = ByteBuffer.allocate(numParticles * (5 * 4) * 4); // 20 floats per vertex, 4 verticies per particle
        ByteBuffer ibb = ByteBuffer.allocate(numParticles*4*4); // 4 indicies each 4 bytes

        bb.order(ByteOrder.nativeOrder());
        ibb.order(ByteOrder.nativeOrder());

        for (int i = 0; i < numParticles; i++)
        {
            double angle = Util.randRange(-Math.PI, Math.PI);
            float xcomp = (float) Math.cos(angle);
            float ycomp = (float) Math.sin(angle);
            float[] tmpf = {
                    xcomp, 0, ycomp,    0, 0,     // A            _______
                    xcomp, 0, ycomp,    0, 1,     // B           |B     C|
                    xcomp, 0, ycomp,    1, 1,     // C           |       |
                    xcomp, 0, ycomp,    1, 0,     // D           |A_____D|
            };
            bb.asFloatBuffer().put(tmpf);
        }

        for (int i = 0; i < numParticles*4; i += 4)
        {
            ibb.asFloatBuffer().put(new float[]{1+i, 0+i, 2+i, 0+i, 3+i, 2+i});
            isize += 6;
        }

        byte[] byteData = bb.array();
        byte[] indexData = ibb.array();

        int[] tmp = new int[1];
        glGenVertexArrays(1, tmp);
        vao = tmp[0];

        glBindVertexArray(vao);
        glGenBuffers(1, tmp);
        vbuff = tmp[0];
        glBindBuffer(GL_ARRAY_BUFFER, vbuff);
        glBufferData(GL_ARRAY_BUFFER, byteData.length, byteData, GL_STATIC_DRAW);

        glEnableVertexAttribArray(Program.POSITION_INDEX);
        glEnableVertexAttribArray(Program.TEXCOORD_INDEX);
        glVertexAttribPointer(Program.POSITION_INDEX, 3, GL_FLOAT, false, 5*4, 0);
        glVertexAttribPointer(Program.TEXCOORD_INDEX, 2, GL_FLOAT, false, 3*4, 3*4);

        glBindVertexArray(0);

        glGenBuffers(1,tmp);
        ibuff = tmp[0];
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, ibuff );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, indexData.length, indexData, GL_STATIC_DRAW );

        this.itype = GL_UNSIGNED_INT;
    }

    public void draw(Program program)
    {
        program.setUniform("worldMatrix", math3d.translation(player.getPosition()));
        program.setUniform("diffuse_texture", texture);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, isize, itype,0);
    }
}
package framework;

import framework.drawing.Program;
import framework.drawing.textures.ImageTexture;
import framework.drawing.textures.Texture;
import framework.math3d.math3d;
import framework.math3d.vec4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import static JGL.JGL.*;

/**
 * Created by Michael on 2/9/2016.
 */
public class GiantWall {

    private byte[] mVertexData;
    private byte[] mIndexData;
    private ArrayList<Float> mVData;
    private ArrayList<Integer> mIData;

    Texture mTexture;
    int vao;
    int vbuff,ibuff;
    int itype, floats_per_vertex;

    vec4 mPosition;

    public GiantWall(Wall[] walls){
        mPosition = new vec4(0, 0, 0, 1);
        mVData = new ArrayList<>();
        mIData = new ArrayList<>();

        this.addAllWalls(walls);
        mVertexData = floatArray2ByteArray(mVData.toArray(new Float[mVData.size()]));
        mIndexData = integerArray2ByteArray(mIData.toArray(new Integer[mIData.size()]));

        //mesh type stuff
        mTexture = new ImageTexture("assets/checker.png");
        floats_per_vertex = 8;

        int[] tmp = new int[1];
        glGenVertexArrays(1,tmp);
        this.vao = tmp[0];
        glBindVertexArray(vao);

        glGenBuffers(1,tmp);
        vbuff = tmp[0];
        glBindBuffer( GL_ARRAY_BUFFER, vbuff );
        glBufferData( GL_ARRAY_BUFFER, mVertexData.length , mVertexData, GL_STATIC_DRAW );

        glGenBuffers(1,tmp);
        ibuff = tmp[0];
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, ibuff );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, mIndexData.length, mIndexData, GL_STATIC_DRAW );

        this.itype = GL_UNSIGNED_INT;

        //set the vao data
        glEnableVertexAttribArray(Program.POSITION_INDEX);
        glEnableVertexAttribArray(Program.TEXCOORD_INDEX);
        glEnableVertexAttribArray(Program.NORMAL_INDEX);
        glVertexAttribPointer(Program.POSITION_INDEX, 3, GL_FLOAT, false, floats_per_vertex*4,   0);
        glVertexAttribPointer(Program.TEXCOORD_INDEX, 2, GL_FLOAT, false, floats_per_vertex*4,   3*4);
        glVertexAttribPointer(Program.NORMAL_INDEX, 3, GL_FLOAT, false, floats_per_vertex*4,     5*4);
        glBindVertexArray(0);    //so no one else interferes with us...
    }

    public void draw(Program prog){
        prog.setUniform("worldMatrix", math3d.translation(mPosition).mul(math3d.scaling(1.0f, 3.0f, 1.0f)));
        prog.setUniform("diffuse_texture",this.mTexture);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, mIData.size(),this.itype,0);
    }

    public void addAllWalls(Wall[] walls){
        for(int i = 0; i < walls.length; i++){
            this.addToVertData(walls[i].getVData());
            this.addToIndexData(walls[i].getIData(), i);
        }
    }

    public void addToVertData(Float[] floats){
        for(int i = 0; i < floats.length; i++){
            mVData.add(floats[i]);
        }
    }

    public  void addToIndexData(int[] indicies, int offset){
        for(int i = 0; i < indicies.length; i++){
            mIData.add(indicies[i] + (24 * offset));
        }
    }

    public byte[] floatArray2ByteArray(Float[] floats){
        ByteBuffer buffer = ByteBuffer.allocate(4 * floats.length);
        buffer.order(ByteOrder.nativeOrder());

        for (float value : floats){
            buffer.putFloat(value);
        }

        return buffer.array();
    }

    public byte[] integerArray2ByteArray(Integer[] nums){
        ByteBuffer buffer = ByteBuffer.allocate(4 * nums.length);
        buffer.order(ByteOrder.nativeOrder());

        for (Integer value : nums){
            buffer.putInt(value.intValue());
        }

        return buffer.array();
    }
}

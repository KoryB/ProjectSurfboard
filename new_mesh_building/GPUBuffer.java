package framework;

import static JGL.JGL.GL_SHADER_STORAGE_BUFFER;
import static JGL.JGL.GL_STREAM_DRAW;
import static JGL.JGL.glBindBuffer;
import static JGL.JGL.glBufferData;
import static JGL.JGL.glGenBuffers;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class GPUBuffer {
    int buffer;

    public GPUBuffer(int[] data){
        ByteBuffer bb = ByteBuffer.allocate(data.length*4);
        bb.order(ByteOrder.nativeOrder());
        bb.asIntBuffer().put(data);
        init(bb.array());
    }
    public GPUBuffer(ArrayList<Float> a){
        float[] tmp = new float[a.size()];
        for(int i=0;i<a.size();++i)
            tmp[i]=a.get(i);
        ByteBuffer bb = ByteBuffer.allocate(a.size()*4);
        bb.order(ByteOrder.nativeOrder());
        bb.asFloatBuffer().put(tmp);
        init(bb.array());
    }
    
    private void init(byte[] data){
        int[] tmp = new int[1];
        glGenBuffers(1,tmp);
        buffer = tmp[0];
        glBindBuffer(GL_SHADER_STORAGE_BUFFER,buffer);
        glBufferData(GL_SHADER_STORAGE_BUFFER,data.length,data,GL_STREAM_DRAW );
    }
}

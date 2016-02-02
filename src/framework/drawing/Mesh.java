package framework.drawing;

import static JGL.JGL.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static framework.math3d.math3d.add;
import static framework.math3d.math3d.mul;

import framework.drawing.Drawable;
import framework.drawing.Program;
import framework.drawing.textures.ImageTexture;
import framework.drawing.textures.Texture;
import framework.math3d.vec3;




public class Mesh implements Drawable{
    Texture texture;
    Texture spec_texture;
    Texture emit_texture;
    String filename="(none)";
    int numvertices;
    int floats_per_vertex;
    int numindices;
    int bits_per_index;
    int vbuff,ibuff;
    int itype;
    int vao;
    
    vec3 bbmin,bbmax;
    vec3 centroid;
    
    private String readLine(DataInputStream din) {
        String x="";
        
        while(true){
            try{
                byte b = din.readByte();
                if( b == '\n' )
                    return x;
                x += (char)b;
            }
            catch(EOFException e){
                if( x.length() == 0)
                    return null;
                else
                    return x;
            }
            catch(IOException e){
                throw new RuntimeException("IO error");
            }
        }
    }
      
    public  Mesh(String filename){
        
        texture=null;
        this.filename = filename;

        int idx = filename.lastIndexOf("/");
        String prefix="";
        if( idx != -1 )
            prefix = filename.substring(0,idx);
        
        FileInputStream fin;
        try{
            fin = new FileInputStream(filename);
        }
        catch(FileNotFoundException ex ){
            throw new RuntimeException("File not found: "+filename);
        }
        
        DataInputStream din = new DataInputStream(fin);

        String line;
        
        line = readLine(din);
        if( !line.startsWith("mesh_01"))
            throw new RuntimeException("Incorrect mesh format: "+line);
        
        byte[] vdata=null,idata=null;
        
        while(true){
            line = readLine(din);
            if( line == null )
                break;
            String[] lst = line.split(" ");
            if( lst.length == 0 ){
            }
            else if(lst[0].equals("num_vertices"))
                this.numvertices = Integer.parseInt(lst[1]);
            else if(lst[0].equals("floats_per_vertex"))
                this.floats_per_vertex = Integer.parseInt(lst[1]);
            else if(lst[0].equals("num_indices"))
                this.numindices = Integer.parseInt(lst[1]);
            else if(lst[0].equals("texture_file"))
                this.texture = new ImageTexture(prefix+"/"+lst[1]);
            else if(lst[0].equals("specular_map"))
                this.spec_texture = new ImageTexture(prefix+"/"+lst[1]);
            else if(lst[0].equals("emission_map"))
                this.emit_texture = new ImageTexture(prefix+"/"+lst[1]);
            else if(lst[0].equals("vertex_data")){
                int numbytes=Integer.parseInt(lst[1]);
                vdata = new byte[numbytes];
                try {
                    din.readFully(vdata);
                } catch (IOException ex) {
                    throw new RuntimeException("Short read "+filename);
                }
            }
            else if(lst[0].equals("bits_per_index"))
                this.bits_per_index = Integer.parseInt(lst[1]);
            else if(lst[0].equals("index_data")){
                int numbytes = Integer.parseInt(lst[1]);
                idata = new byte[numbytes];
                try {
                    din.readFully(idata);
                } catch (IOException ex) {
                    throw new RuntimeException("Short read "+filename);
                }
            }
            else if(lst[0].equals("end"))
                break;
            else if(lst[0].equals("total_vertex_floats")){
                //nothing to do
            }
            else{
                if( line.length() > 0 )
                    System.out.println("Warning: Ignoring "+line);
            }
        }
        
        int[] tmp = new int[1];
        glGenVertexArrays(1,tmp);
        this.vao = tmp[0];
        glBindVertexArray(vao);      //do it here so it captures the bindbuffer's below
        
        glGenBuffers(1,tmp);
        vbuff = tmp[0];
        glBindBuffer( GL_ARRAY_BUFFER, vbuff );
        glBufferData( GL_ARRAY_BUFFER, vdata.length , vdata, GL_STATIC_DRAW );
        
        float[] mins = new float[]{Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE};
        float[] maxs = new float[]{-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE};
        ByteBuffer tmpb = ByteBuffer.wrap(vdata);
        tmpb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = tmpb.asFloatBuffer();
        int i=0;
        while(i<fb.limit()){
            for(int j=0;j<3;++j){
                float tmp1 = fb.get(i+j);
                mins[j] = Math.min(mins[j],tmp1);
                maxs[j] = Math.max(maxs[j],tmp1);
            }
            i+=floats_per_vertex;
        }
        bbmin = new vec3(mins[0],mins[1],mins[2]);
        bbmax = new vec3(maxs[0],maxs[1],maxs[2]);
        centroid = mul( 0.5f, add(bbmin,bbmax) );
        
        glGenBuffers(1,tmp);
        ibuff = tmp[0];
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, ibuff );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, idata.length, idata, GL_STATIC_DRAW );
        
        switch (bits_per_index) {
            case 8:
                this.itype = GL_UNSIGNED_BYTE;
                break;
            case 16:
                this.itype = GL_UNSIGNED_SHORT;
                break;
            case 32:
                this.itype = GL_UNSIGNED_INT;
                break;
            default:
                throw new RuntimeException("Bad bits: "+bits_per_index);
        }

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
        if(this.texture != null )
            prog.setUniform("diffuse_texture",this.texture);
        if(this.emit_texture != null )
            prog.setUniform("emit_texture",this.texture);
        if( this.spec_texture != null )
            prog.setUniform("spec_texture",this.texture);
        
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES,this.numindices,this.itype,0);
    }
}

                

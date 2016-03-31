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

import framework.drawing.textures.DataTexture;
import framework.drawing.textures.ImageTexture;
import framework.drawing.textures.Texture;
import framework.math3d.vec3;
import static framework.math3d.math3d.add;
import static framework.math3d.math3d.mul;
import static framework.math3d.math3d.add;
import static framework.math3d.math3d.mul;
import static framework.math3d.math3d.add;
import static framework.math3d.math3d.mul;




public class Mesh {
    public Texture texture;
    public Texture bonetex;
    public Texture mattex;
    public Texture spec_texture;
    public Texture emit_texture;
    public Texture bump_texture;
    public String filename="(none)";
    public int numvertices;
    //int floats_per_vertex;
    public int numindices;
    public int bits_per_index;
    //int vbuff,ibuff;
    public int itype;
    public int vao;
    public int numbones;
    public int numframes=0;
    public int maxdepth=0;
    
    vec3 specular = new vec3(1,1,1);
    //vec3 bbmin,bbmax;
    //vec3 centroid;
    boolean with_adjacency;

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
      
    private byte[] readbytes(DataInputStream din, String[] lst ){
        int numbytes=Integer.parseInt(lst[1]);
        byte[] data = new byte[numbytes];
        try {
            din.readFully(data);
        } catch (IOException ex) {
            throw new RuntimeException("Short read "+filename);
        }
        return data;
    }
    private void makebuffer(int attribidx, int count, byte[] data){
        int[] tmp = new int[1];
        glGenBuffers(1,tmp);
        int buff = tmp[0];
        glEnableVertexAttribArray(attribidx);
        glBindBuffer( GL_ARRAY_BUFFER, buff );
        glBufferData( GL_ARRAY_BUFFER, data.length , data, GL_STATIC_DRAW );
        glVertexAttribPointer(attribidx, count, GL_FLOAT, false, count*4,0);
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
        if( !line.equals("mesh_07"))
            throw new RuntimeException("Incorrect mesh format: "+line);
        
        byte[] vdata=null,ndata=null,tcdata=null,tdata=null,idata=null,wdata=null,infdata=null;
        
        byte[] boneheads=null,bonetails=null,matrices=null,quaternions=null;
        
        while(true){
            line = readLine(din);
            if( line == null )
                break;
            String[] lst = line.split(" ");
            
            if( lst.length == 0 ){
            }
            else if(lst[0].equals("num_vertices"))
                this.numvertices = Integer.parseInt(lst[1]);
            //else if(lst[0].equals("floats_per_vertex"))
            //    this.floats_per_vertex = Integer.parseInt(lst[1]);
            else if(lst[0].equals("num_indices"))
                this.numindices = Integer.parseInt(lst[1]);
            else if(lst[0].equals("map_Kd"))
                this.texture = new ImageTexture(prefix+"/"+lst[1]);
            else if(lst[0].equals("map_Ks"))
                this.spec_texture = new ImageTexture(prefix+"/"+lst[1]);
            else if(lst[0].equals("map_Ke"))
                this.emit_texture = new ImageTexture(prefix+"/"+lst[1]);
            else if(lst[0].equals("map_Bump"))
                this.bump_texture = new ImageTexture(prefix+"/"+lst[1]);
            else if(lst[0].equals("Ks") ){
                specular = new vec3(Float.parseFloat(lst[1]),
                        Float.parseFloat(lst[2]),
                        Float.parseFloat(lst[3]));
            }
            else if(lst[0].equals("vertex_data"))
                vdata = readbytes(din,lst);
            else if(lst[0].equals("normal_data"))
                ndata = readbytes(din,lst);
            else if(lst[0].equals("texcoord_data"))
                tcdata = readbytes(din,lst);
            else if(lst[0].equals("tangent_data"))
                tdata = readbytes(din,lst);
            else if(lst[0].equals("index_data"))
                idata = readbytes(din,lst);
            else if( lst[0].equals("weight_data"))
                wdata = readbytes(din,lst);
            else if( lst[0].equals("influence_data"))
                infdata = readbytes(din,lst);
            else if( lst[0].equals("boneheads"))
                boneheads = readbytes(din,lst);
            else if( lst[0].equals("bonetails"))
                bonetails = readbytes(din,lst);
            else if( lst[0].equals("matrices"))
                matrices = readbytes(din,lst);
            else if( lst[0].equals("quaternions"))
                quaternions = readbytes(din,lst);
            else if( lst[0].equals("with_adjacency")){
                if(lst[1].equals("True"))
                    with_adjacency=true;
                else if( lst[1].equals("False"))
                    with_adjacency=false;
                else
                    throw new RuntimeException("?");
            }
            else if( lst[0].equals("numbones"))
                numbones = Integer.parseInt(lst[1]);
            else if( lst[0].equals("numframes"))
                numframes = Integer.parseInt(lst[1]);
            else if( lst[0].equals("maxdepth"))
                maxdepth = Integer.parseInt(lst[1]);
            else if(lst[0].equals("bits_per_index"))
                this.bits_per_index = Integer.parseInt(lst[1]);
            else if(lst[0].equals("end"))
                break;
            else if( lst[0].equals("Ka") || lst[0].equals("illum") || lst[0].equals("Kd") || lst[0].equals("d") || lst[0].equals("Ns") || lst[0].equals("Ni") || lst[0].equals("Ke") )
                ;   //do nothing
            else{
                if( line.length() > 0 )
                    System.out.println("Warning: Ignoring "+line);
            }
        }
        
        /*
        float[] mins = new float[]{Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE};
        float[] maxs = new float[]{-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE};
        ByteBuffer tmpb = ByteBuffer.wrap(vdata);
        tmpb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = tmpb.asFloatBuffer();
        int i=0;
        while(i<fb.limit()){
            for(int j=0;j<3;++j){
                float tmp1 = fb.get(i+j);
                mins[j] = Float.min(mins[j],tmp1);
                maxs[j] = Float.max(maxs[j],tmp1);
            }
            i+=floats_per_vertex;
        }
        bbmin = new vec3(mins[0],mins[1],mins[2]);
        bbmax = new vec3(maxs[0],maxs[1],maxs[2]);
        centroid = mul( 0.5f, add(bbmin,bbmax) );
        */
        
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

        int[] tmp = new int[1];
        glGenVertexArrays(1,tmp);
        this.vao = tmp[0];
        glBindVertexArray(vao);      //do it here so it captures the bindbuffer's below
        
        if( vdata != null )
            makebuffer(Program.POSITION_INDEX,3,vdata);
        if( ndata != null )
            makebuffer(Program.NORMAL_INDEX,3,ndata);
        if( tcdata != null )
            makebuffer(Program.TEXCOORD_INDEX,2,tcdata);
        if( tdata != null )
            makebuffer( Program.TANGENT_INDEX,3,tdata);
        if( wdata != null )
            makebuffer( Program.WEIGHT_INDEX,4,wdata);
        if( infdata != null )
            makebuffer( Program.INFLUENCE_INDEX,4,infdata);
        
        glGenBuffers(1,tmp);
        int ibuff = tmp[0];
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, ibuff );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, idata.length, idata, GL_STATIC_DRAW );
        
        glBindVertexArray(0);    //so no one else interferes with us...
        
        if( boneheads != null ){
            /*ByteBuffer tmpx = ByteBuffer.allocate(boneheads.length );
            tmpx.order(ByteOrder.nativeOrder());
            FloatBuffer tmpf = tmpx.asFloatBuffer();
            tmpx.put(boneheads);
            float[] F = new float[boneheads.length/4];
            tmpf.get(F);*/
            bonetex = new DataTexture(numbones,1,boneheads);
        }
        
        if( matrices != null )
            mattex = new DataTexture( numbones*4, numframes, matrices );
        
    }
    
    public void draw(Program prog){
        if(this.texture != null )
            prog.setUniform("diffuse_texture",this.texture);
        //if(this.emit_texture != null )
        //    prog.setUniform("emit_texture",this.texture);
        //if( this.spec_texture != null )
        //    prog.setUniform("spec_texture",this.texture);
        if( this.bump_texture != null )
            prog.setUniform("bump_texture",this.bump_texture);
        if( bonetex != null )
            prog.setUniform("bonetex",this.bonetex);
        if( mattex != null )
            prog.setUniform("mattex", this.mattex );
        
        prog.setUniform("specular",specular);
        
        glBindVertexArray(vao);
        if(with_adjacency)
            glDrawElements(GL_TRIANGLES_ADJACENCY,this.numindices,this.itype,0);
        else
            glDrawElements(GL_TRIANGLES,this.numindices,this.itype,0);

    }
}

                

package framework.drawing;

import framework.drawing.textures.CubeTexture;
import framework.drawing.textures.Texture2D;
import framework.math3d.vec2;
import framework.math3d.vec3;
import framework.math3d.mat4;
import framework.math3d.vec4;
import java.util.*;
import java.io.*;
import static JGL.JGL.*;
import java.nio.file.Paths;
import java.nio.file.Files;

public class Program{

    //indices of attributes
    public static final int POSITION_INDEX = 0;
    public static final int TEXCOORD_INDEX = 1;
    public static final int NORMAL_INDEX = 2;


    //the GL identifier for the shader program
    private int prog;
    
    //setters for the uniforms, keyed on the uniform name
    private Map<String,UniformSetter> uniforms = new TreeMap<>();
    
    private static TreeMap<String,Object> currentuniforms = new TreeMap<>();
    
    //the currently active program
    private static Program active;
    
     public Program(String vsfname, String fsfname ){
        init(vsfname,null,null,null,fsfname,null);
    }
    public Program(String vsfname,String gsfname, String fsfname){
        init(vsfname,null,null,gsfname,fsfname,null);
    }
    public Program(String vsfname, String tcsfname, String tesfname, String gsfname, String fsfname){
        init(vsfname,tcsfname, tesfname, gsfname,fsfname,null);
    }
    
    private void init(String vsfname, String tcsfname, String tesfname, String gsfname, String fsfname, String csfname){
        
        int vs=0,tcs=0,tes=0,gs=0,fs=0,cs=0;
        if(vsfname!=null)
            vs = make_shader(vsfname,GL_VERTEX_SHADER);
        if(tcsfname!=null)
            tcs = make_shader(tcsfname,GL_TESS_CONTROL_SHADER);
        if(tesfname!=null)
            tes = make_shader(tesfname,GL_TESS_EVALUATION_SHADER);
        if(gsfname!=null)
            gs = make_shader(vsfname,GL_GEOMETRY_SHADER);
        if(fsfname!=null)
            fs = make_shader(fsfname, GL_FRAGMENT_SHADER);
        if(csfname!=null)
            cs = make_shader(csfname,GL_COMPUTE_SHADER);
        
        prog = glCreateProgram();
        if( vs != 0 )
            glAttachShader(prog,vs);
        if( tcs != 0 )
            glAttachShader(prog,tcs);
        if( tes != 0 )
            glAttachShader(prog,tes);
        if( gs != 0 )
            glAttachShader(prog,gs);
        if( fs != 0 )
            glAttachShader(prog,fs);
        if( cs != 0 )
            glAttachShader(prog,cs);
        
        //set attribute locations
        glBindAttribLocation(prog,POSITION_INDEX,"a_position");
        glBindAttribLocation(prog,TEXCOORD_INDEX,"a_texcoord");
        glBindAttribLocation(prog,NORMAL_INDEX,"a_normal");
//        glBindAttribLocation(prog,TANGENT_INDEX,"a_tangent");
        
        glLinkProgram(prog);
        
        int[] tmp = new int[1];
        glGetProgramiv(prog,GL_INFO_LOG_LENGTH,tmp);
        if( tmp[0] > 0 ){
            byte[] buf = new byte[tmp[0]];
            glGetProgramInfoLog(prog,buf.length, (int[])null, buf );
            String ilog = new String(buf).trim();
            if( ilog.length() > 0 ){
                System.out.println("When linking "+vsfname+"+"+fsfname+":");
                System.out.println(ilog);
            }
        }
        glGetProgramiv(prog,GL_LINK_STATUS,tmp);
        if( tmp[0] == 0 ){
            throw new RuntimeException("Could not link shaders");
        }
        
        glGetProgramiv( prog, GL_ACTIVE_UNIFORMS, tmp );
        int numuniforms = tmp[0];
        int texcount=0;
        
        for(int i=0;i<numuniforms;++i){
            int[] ty = new int[1];
            int[] sz = new int[1];
            int[] idx = new int[1];
            
            glGetActiveUniformsiv(prog,1,new int[]{i}, 
                    GL_UNIFORM_TYPE, 
                    ty );
                    
            glGetActiveUniformsiv(prog,1,new int[]{i},
                    GL_UNIFORM_SIZE, sz );
            
            byte[] nm = new byte[128];
            int[] le = new int[1];
            glGetActiveUniformName( prog, i, nm.length,
                le, nm );
            String nm_ = new  String(nm,0,le[0]);
            
            UniformSetter setter = null;
            
            int uloc = glGetUniformLocation(prog,nm_);
            
            if(ty[0] == GL_FLOAT_MAT4 && sz[0] == 1 )
                setter = new Mat4Setter(nm_,uloc);
            else if(ty[0] == GL_FLOAT_VEC4 && sz[0] == 1 )
                setter = new Vec4Setter(nm_,uloc);
            else if(ty[0] == GL_FLOAT_VEC3 && sz[0] == 1 )
                setter = new Vec3Setter(nm_,uloc);
            else if(ty[0] == GL_FLOAT_VEC2 && sz[0] == 1 )
                setter = new Vec2Setter(nm_,uloc);
            else if(ty[0] == GL_FLOAT && sz[0] == 1 )
                setter = new FloatSetter(nm_,uloc);
            else if(ty[0] == GL_FLOAT && sz[0] > 1 )
                setter = new FloatArraySetter(nm_,uloc, sz[0]);
            else if(ty[0] == GL_BOOL && sz[0] == 1 )
                setter = new BooleanSetter(nm_,uloc);
            else if( ty[0] == GL_SAMPLER_2D && sz[0] == 1 )
                setter = new Sampler2DSetter(nm_,uloc,texcount++);
            else if( ty[0] == GL_SAMPLER_CUBE && sz[0] == 1 )
                setter = new SamplerCubeSetter(nm_,uloc,texcount++);
            else
                throw new RuntimeException("Don't know about type for uniform "+nm_);

            uniforms.put(nm_,setter);
        }
        
        glBindFragDataLocation(prog,0,"color");
        for(int i=0;i<8;++i){
            glBindFragDataLocation(prog,i,"color"+i);
        }
    }
       
    public void use(){
        glUseProgram(prog);
        active=this;
        for(String s : currentuniforms.keySet() ){
            this.setUniform(s,currentuniforms.get(s));
        }
    }
        

    public void setUniform(String name, Object value){
        //System.out.println("Set "+name+" to\n"+value);
        
        if(active != this)
            throw new RuntimeException("This program is not active");
        
        if( uniforms.keySet().contains(name))
        {
            uniforms.get(name).set(value);
        }

    }

    public int make_shader(String filename, int shadertype){
        String sdata = read_file(filename);
        int s = glCreateShader(shadertype);
        glShaderSource( 
            s,1, 
            new String[]{sdata}, 
            new int[]{sdata.length()} );
        glCompileShader(s);
        int[] tmp = new int[1];
        glGetShaderiv(s,GL_INFO_LOG_LENGTH,tmp);
        if(tmp[0] > 0 ){
            byte[] b = new byte[tmp[0]];
            int[] len = new int[1];
            glGetShaderInfoLog(s,b.length,len,b);
            String ilog = new String(b);
            ilog = ilog.trim();
            if( ilog.length() > 0 ){
                System.out.println("When compiling "+filename+":");
                System.out.println(ilog);
            }
        }
        
        glGetShaderiv( s, GL_SHADER_SOURCE_LENGTH,tmp);
        
        glGetShaderiv( s, GL_COMPILE_STATUS,tmp);
        if( tmp[0] == 0 )
            throw new RuntimeException("Cannot compile "+filename+": "+tmp[0]);

        return s;
    }
    
    public String read_file(String filename){
        try{
            //http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
            return new String( Files.readAllBytes(Paths.get(filename)) );
        }
        catch(IOException e){
            throw new RuntimeException("Cannot load shader "+filename);
        }
    }
    
    abstract class UniformSetter{
        protected String name;
        protected int i;
        public UniformSetter(String name, int idx){
            this.name=name;
            this.i=idx;
        }
        public void set(Object o){
            Program.currentuniforms.put(name,makecopy(o));
            do_set(o);
        }
        protected abstract void do_set(Object o);
        protected abstract Object makecopy(Object o);
    }
    
    class Mat4Setter extends UniformSetter{
        public Mat4Setter(String name,int idx){
            super(name,idx);
        }
        @Override
        protected void do_set(Object o){
            if( ! (o instanceof mat4) )
                throw new RuntimeException("Not a mat4");
                
            mat4 v = (mat4) o;
            //System.out.println("Set matrix "+v);
            glUniformMatrix4fv( i, 1, true, v.tofloats() );
        }
        protected Object makecopy(Object o){
            return ((mat4)o).clone();
        }
    }
    
    class Vec4Setter extends UniformSetter{
        public Vec4Setter(String name,int idx){
            super(name,idx);
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof vec4) )
                throw new RuntimeException("Not a vec4");
            vec4 v = (vec4) o;
            glUniform4f( i, v.x, v.y, v.z, v.w );
        }
        protected Object makecopy(Object o){
            return ((vec4)o).clone();
        }
    }
    class Sampler2DSetter extends UniformSetter{
        int unit;
        public Sampler2DSetter(String name, int idx, int unit){
            super(name,idx);
            this.unit=unit;
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof Texture2D) )
                throw new RuntimeException("Not a Texture2D");
            Texture2D v =  (Texture2D) o;
            v.bind(unit);
            glUniform1i(i,unit);
        }
        protected Object makecopy(Object o){
            return o;
        }
    }

    class Vec3Setter extends UniformSetter{
        public Vec3Setter(String name, int idx){
            super(name,idx);
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof vec3) )
                throw new RuntimeException("Not a vec3: "+o);
            vec3 v = (vec3) o;
            glUniform3f( i, v.x, v.y, v.z );
        }
        protected Object makecopy(Object o){
            return ((vec3)o).clone();
        }
    }

    class Vec2Setter extends UniformSetter{
        public Vec2Setter(String name, int idx){
            super(name,idx);
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof vec2) )
                throw new RuntimeException("Not a vec2: "+o);
            vec2 v = (vec2) o;
            glUniform2f(i, v.x, v.y);
        }
        protected Object makecopy(Object o){
            return ((vec2)o).clone();
        }
    }
      
    class FloatSetter extends UniformSetter{
        public FloatSetter(String name,int idx){
            super(name,idx);
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof Number) )
                throw new RuntimeException("Not a float/double/int/other number");
            Number n = (Number) o;
            glUniform1f( i, n.floatValue() );
        }
        protected Object makecopy(Object o){
            return o;
        }
    }

    class FloatArraySetter extends UniformSetter{
        protected int size;
        public FloatArraySetter(String name,int idx, int size)
        {
            super(name,idx);
            this.size = size;
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof float[] && ((float[])o).length == size))
                throw new RuntimeException("Not a float[], and/or size doesn't match");
            float[] n = (float[]) o;
            glUniform1fv(i, size, n);
        }
        protected Object makecopy(Object o){
            return o;
        }
    }
	class BooleanSetter extends UniformSetter{
        public BooleanSetter(String name,int idx){
            super(name,idx);
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof Boolean) )
                throw new RuntimeException("Not a boolean");
            Boolean n = (Boolean) o;
            glUniform1i( i, n ? 1:0);
        }
        protected Object makecopy(Object o){
            return o;
        }
    }
    class SamplerCubeSetter extends UniformSetter{
        int unit;
        public SamplerCubeSetter(String name, int idx, int unit){
            super(name,idx);
            this.unit=unit;
        }
        @Override
        public void do_set(Object o){
            if( ! (o instanceof CubeTexture) )
                throw new RuntimeException("Not a CubeTexture");
            CubeTexture v =  (CubeTexture) o;
            v.bind(unit);
            glUniform1i(i,unit);
        }
        protected Object makecopy(Object o){
            return o;
        }
    }
}
            
            

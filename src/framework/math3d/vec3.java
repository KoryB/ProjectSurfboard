package framework.math3d;


public class vec3{
    public float x,y,z;
    
    public vec3(){
        x=y=z=0.0f;
    }
    
    public vec3(Object... args){
        init(args);
    }
    
    public vec2 xy(){
        return new vec2(x,y);
    }
    
    public Object clone(){
        return new vec3(x,y,z);
    }
    
    public String toString(){
        return "["+x+" "+y+" "+z+"]";
    }
    private void init(Object[] args){
        int ctr=0;
        float[] _M = new float[3];
        for(Object a : args){
            if( a instanceof Number )
                _M[ctr++] = ((Number)a).floatValue();
            else if( a instanceof vec2 ){
                _M[ctr++] = ((vec2)a).x;
                _M[ctr++] = ((vec2)a).y;
            }
            else if( a instanceof vec3 ){
                _M[ctr++] = ((vec3)a).x;
                _M[ctr++] = ((vec3)a).y;
                _M[ctr++] = ((vec3)a).z;
            }
            else if( a instanceof vec4 ){
                _M[ctr++] = ((vec4)a).x;
                _M[ctr++] = ((vec4)a).y;
                _M[ctr++] = ((vec4)a).z;
                _M[ctr++] = ((vec4)a).w;
            }
            else if( a instanceof float[] ){
                float[] tmp = (float[])a;
                for(int i=0;i<tmp.length;i++)
                    _M[ctr++] = tmp[i];
            }
            else
                throw new RuntimeException("Bad type for vec3 constructor");
        }
        
        if( ctr != 3 )
            throw new RuntimeException("Bad number of arguments for vec3 constructor");
        
        x=_M[0];
        y=_M[1];
        z=_M[2];
        
    }

    public float[] tofloats(){
        return new float[]{x,y,z};
    }
    
    public vec3 add(vec3 o){
        return new vec3(x+o.x,y+o.y,z+o.z);
    }
    public vec3 sub(vec3 o){
        return new vec3(x-o.x,y-o.y,z-o.z);
    }
        
    public vec3 mul(float d){
        return new vec3(d*x,d*y,d*z);
    }
    
    public vec3 neg(){
        return mul(-1.0f);
    }
    
    public float dot(vec3 o){
        return x*o.x+y*o.y+z*o.z;
    }
    
}

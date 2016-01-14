package framework.math3d;


public class vec2{
    public float x,y;
    
    public vec2(){
        x=y=0.0f;
    }
    
    public vec2(Object... args){
        init(args);
    }
    public Object clone(){
        return new vec2(x,y);
    }
    
    public String toString(){
        return "["+x+" "+y+"]";
    }
    private void init(Object[] args){
        int ctr=0;
        float[] _M = new float[2];
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
                throw new RuntimeException("Bad type for vec2 constructor");
        }
        
        if( ctr != 2 )
            throw new RuntimeException("Bad number of arguments for vec2 constructor");
        
        x=_M[0];
        y=_M[1];
        
    }

    public float[] tofloats(){
        return new float[]{x,y};
    }
    
    public float dot(vec2 o ){
        return x*o.x+y*o.y;
    }
    public vec2 add(vec2 o){
        return new vec2(x+o.x,y+o.y);
    }
    public vec2 sub(vec2 o){
        return new vec2(x-o.x,y-o.y);
    }
        
    public vec2 mul(float d){
        return new vec2(d*x,d*y);
    }
    
    
    public vec2 neg(){
        return mul(-1.0f);
    }
}

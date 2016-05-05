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
        
    public vec3 mul(double d){
        return new vec3(d*x,d*y,d*z);
    }
    
    public vec3 neg(){
        return mul(-1.0f);
    }
    
    public float dot(vec3 o){
        return x*o.x+y*o.y+z*o.z;
    }
    
    
    
    public vec2 xx(){ return new vec2(x, x); }
    public vec3 xxx(){ return new vec3(x, x, x); }
    public vec3 xxy(){ return new vec3(x, x, y); }
    public vec3 xxz(){ return new vec3(x, x, z); }
    public vec3 xyx(){ return new vec3(x, y, x); }
    public vec3 xyy(){ return new vec3(x, y, y); }
    public vec3 xyz(){ return new vec3(x, y, z); }
    public vec2 xz(){ return new vec2(x, z); }
    public vec3 xzx(){ return new vec3(x, z, x); }
    public vec3 xzy(){ return new vec3(x, z, y); }
    public vec3 xzz(){ return new vec3(x, z, z); }
    public vec2 yx(){ return new vec2(y, x); }
    public vec3 yxx(){ return new vec3(y, x, x); }
    public vec3 yxy(){ return new vec3(y, x, y); }
    public vec3 yxz(){ return new vec3(y, x, z); }
    public vec2 yy(){ return new vec2(y, y); }
    public vec3 yyx(){ return new vec3(y, y, x); }
    public vec3 yyy(){ return new vec3(y, y, y); }
    public vec3 yyz(){ return new vec3(y, y, z); }
    public vec2 yz(){ return new vec2(y, z); }
    public vec3 yzx(){ return new vec3(y, z, x); }
    public vec3 yzy(){ return new vec3(y, z, y); }
    public vec3 yzz(){ return new vec3(y, z, z); }
    public vec2 zx(){ return new vec2(z, x); }
    public vec3 zxx(){ return new vec3(z, x, x); }
    public vec3 zxy(){ return new vec3(z, x, y); }
    public vec3 zxz(){ return new vec3(z, x, z); }
    public vec2 zy(){ return new vec2(z, y); }
    public vec3 zyx(){ return new vec3(z, y, x); }
    public vec3 zyy(){ return new vec3(z, y, y); }
    public vec3 zyz(){ return new vec3(z, y, z); }
    public vec2 zz(){ return new vec2(z, z); }
    public vec3 zzx(){ return new vec3(z, z, x); }
    public vec3 zzy(){ return new vec3(z, z, y); }
    public vec3 zzz(){ return new vec3(z, z, z); }
    
    
    
}

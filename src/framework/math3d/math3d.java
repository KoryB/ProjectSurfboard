package framework.math3d;

/*
# some of these functions (individually noted) are based on code from TDL
# The TDL copyright is as follows:
# 
#  Copyright 2009, Google Inc.
#  All rights reserved.
# 
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are
#  met:
# 
#      *  Redistributions of source code must retain the above copyright
#  notice, this list of conditions and the following disclaimer.
#      *  Redistributions in binary form must reproduce the above
#  copyright notice, this list of conditions and the following disclaimer
#  in the documentation and/or other materials provided with the
#  distribution.
#      *  Neither the name of Google Inc. nor the names of its
#  contributors may be used to endorse or promote products derived from
#  this software without specific prior written permission.
# 
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
#  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
#  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
#  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
#  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
#  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
#  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
#  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
*/


public class math3d{
    
    public static float length(vec4 v){
        return (float)Math.pow( v.dot(v),0.5f);
    }
    public static float length(vec3 v){
        return (float)Math.pow( v.dot(v),0.5f);
    }
    public static float length(vec2 v){
        return (float)Math.pow( v.dot(v),0.5f);
    }
    public static vec4 normalize(vec4 v){
        float le = length(v);
        return v.mul(1.0f/le);
    }
    public static vec3 normalize(vec3 v){
        float le = length(v);
        return v.mul(1.0f/le);
    }
    public static vec2 normalize(vec2 v){
        float le = length(v);
        return v.mul(1.0f/le);
    }

/*    
#from TDL
def det(M):
    if type(M) == mat2:
        return m[0][0]*m[1][1] - m[0][1]*m[1][0]
    elif type(M) == mat3:
        return m[2][2] * (m[0][0] * m[1][1] - m[0][1] * m[1][0]) -              m[2][1] * (m[0][0] * m[1][2] - m[0][2] * m[1][0]) +                m[2][0] * (m[0][1] * m[1][2] - m[0][2] * m[1][1])
    elif type(M) == mat4:
        t01 = m[0][0] * m[1][1] - m[0][1] * m[1][0]
        t02 = m[0][0] * m[1][2] - m[0][2] * m[1][0]
        t03 = m[0][0] * m[1][3] - m[0][3] * m[1][0]
        t12 = m[0][1] * m[1][2] - m[0][2] * m[1][1]
        t13 = m[0][1] * m[1][3] - m[0][3] * m[1][1]
        t23 = m[0][2] * m[1][3] - m[0][3] * m[1][2]
        return (m[3][3] * (m[2][2] * t01 - m[2][1] * t02 + m[2][0] * t12) -
             m[3][2] * (m[2][3] * t01 - m[2][1] * t03 + m[2][0] * t13) +
             m[3][1] * (m[2][3] * t02 - m[2][2] * t03 + m[2][0] * t23) -
             m[3][0] * (m[2][3] * t12 - m[2][2] * t13 + m[2][1] * t23) )
    else:
        assert 0

#from TDL
def inverse(m):
    if type(m) == mat2:
        d = 1.0 / (m[0][0] * m[1][1] - m[0][1] * m[1][0])
        return mat2(d * m[1][1], -d * m[0][1], -d * m[1][0], d * m[0][0])
    elif type(m) == mat3:
        t00 = m[1][1] * m[2][2] - m[1][2] * m[2][1]
        t10 = m[0][1] * m[2][2] - m[0][2] * m[2][1]
        t20 = m[0][1] * m[1][2] - m[0][2] * m[1][1]
        d = 1.0 / (m[0][0] * t00 - m[1][0] * t10 + m[2][0] * t20)
        return mat3( d * t00, -d * t10, d * t20,
              -d * (m[1][0] * m[2][2] - m[1][2] * m[2][0]),
               d * (m[0][0] * m[2][2] - m[0][2] * m[2][0]),
              -d * (m[0][0] * m[1][2] - m[0][2] * m[1][0]),
               d * (m[1][0] * m[2][1] - m[1][1] * m[2][0]),
              -d * (m[0][0] * m[2][1] - m[0][1] * m[2][0]),
               d * (m[0][0] * m[1][1] - m[0][1] * m[1][0]) )
    elif type(m) == mat4:
       
*/


  
    public static mat4 axisRotation(vec4 axis, double angle){
        axis=normalize(axis);
        float x,y,z;
        x = axis.x;
        y = axis.y;
        z = axis.z;
        return axisRotation(x,y,z,angle);
    }
    
    //from TDL
    public static mat4 axisRotation(float x, float y, float z, double angle){
        float xx,yy,zz,c,s;
        xx = x * x;
        yy = y * y;
        zz = z * z;
        c = (float)Math.cos(angle);
        s = (float)Math.sin(angle);
        float oneMinusCosine = 1 - c;
        return new mat4(
            xx + (1 - xx) * c,
            x * y * oneMinusCosine + z * s,
            x * z * oneMinusCosine - y * s,
            0,
            x * y * oneMinusCosine - z * s,
            yy + (1 - yy) * c,
            y * z * oneMinusCosine + x * s,
            0,
            x * z * oneMinusCosine + y * s,
            y * z * oneMinusCosine - x * s,
            zz + (1 - zz) * c,
            0,
            0, 0, 0, 1
        );
    }
    
    public static mat4 axisRotation(vec3 axis, double angle){
        return axisRotation(new vec4(axis.x,axis.y,axis.z,0.0),(float)angle);
    }
    
    
    
    public static mat4 scaling( vec3 v ){
        return scaling(v.x,v.y,v.z);
    }

    public static mat4 scaling( vec4 v ){
        return scaling(v.x,v.y,v.z);
    }

    //from TDL
    public static mat4 scaling( float x, float y, float z ){
        return new mat4(
                x,0,0,0,
                0,y,0,0,
                0,0,z,0,
                0,0,0,1.0);
    }
    
    public static mat4 translation(vec3 v){
        return translation(v.x,v.y,v.z);
    }
    
    public static mat4 translation(vec4 v){
        return translation(v.x,v.y,v.z);
    }
    
    //from TDL
    public static mat4 translation(float x, float y, float z){
        return new mat4(
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            x,y,z,1);
    }    
    

    public static vec4 add( vec4... ob ){
        vec4 res = ob[0];
        for(int i=1;i<ob.length;++i){
            res = res.add(ob[i]);
        }
        return res;
    }
    
    public static vec3 add( vec3... ob ){
        vec3 res = ob[0];
        for(int i=1;i<ob.length;++i){
            res = res.add(ob[i]);
        }
        return res;
    }
    
    public static vec4 sub( vec4... ob ){
        vec4 res = ob[0];
        for(int i=1;i<ob.length;++i){
            res = res.sub(ob[i]);
        }
        return res;
    }
    
    public static vec3 sub( vec3... ob ){
        vec3 res = ob[0];
        for(int i=1;i<ob.length;++i){
            res = res.sub(ob[i]);
        }
        return res;
    }
    
    
    public static vec4 mul(vec4 v, mat4 m){
        return v.mul(m);
    }
    
    public static vec4 mul(float d, vec4 v){
        return v.mul(d);
    }
    public static vec4 mul(vec4 v, float d){
        return v.mul(d);
    }
    
    public static vec4 mul(double d, vec4 v){
        return v.mul((float)d);
    }
    public static vec4 mul(vec4 v, double d){
        return v.mul((float)d);
    }

    public static vec4 mul(int d, vec4 v){
        return v.mul((float)d);
    }
    public static vec4 mul(vec4 v, int d){
        return v.mul((float)d);
    }
    
    public static mat4 mul( mat4... ob ){
        mat4 res = ob[0];
        for(int i=1;i<ob.length;++i){
            res = res.mul(ob[i]);
        }
        return res;
    }
 

    public static vec3 mul(float d, vec3 v){
        return v.mul(d);
    }
    
    public static vec3 mul(vec3 v, float d){
        return v.mul(d);
    }
    
    public static vec3 mul(double d, vec3 v){
        return v.mul((float)d);
    }
    
    public static vec3 mul(vec3 v, double d){
        return v.mul((float)d);
    }

    public static vec3 mul(int d, vec3 v){
        return v.mul((float)d);
    }
    public static vec3 mul(vec3 v, int d){
        return v.mul((float)d);
    }
    
    public static vec3 cross(vec3 v, vec3 w){
        return new vec3(
            v.y*w.z - w.y*v.z,
            w.x*v.z - v.x*w.z,
            v.x*w.y - w.x*v.y
        );
    }
    
    public static vec4 cross(vec4 v, vec4 w){
        if( v.w != 0 || w.w != 0 )
            throw new RuntimeException("w must be zero for cross()");
        return new vec4(
            v.y*w.z - w.y*v.z,
            w.x*v.z - v.x*w.z,
            v.x*w.y - w.x*v.y,
            0.0f
        );
    }
    
    public static float dot(vec4 v, vec4 w ){
        return v.x*w.x+v.y*w.y+v.z*w.z+v.w*w.w;
    }
    
    public static float dot(vec3 v, vec3 w ){
        return v.x*w.x+v.y*w.y+v.z*w.z;
    }
    
    
}
 
/*
if __name__ == "__main__":
    #test harness
    v2a=vec2(2,4)
    v2b=vec2(10,11)
    
    assert v2a+v2b == vec2(12,15)
    assert v2a-v2b == vec2(-8,-7)
    assert v2a+v2b != vec2(12,3)
    assert v2a+v2b != vec2(3,15)
    assert v2a*v2b == vec2(20,44)
    assert 5*v2a == vec2(10,20)
    assert v2a*5 == vec2(10,20)
    
    assert v2a.xy == v2a
    assert v2a.xx == vec2(2,2)
    assert v2a.yy == vec2(4,4)
    assert v2a.yx == vec2(4,2)
    
    v3a=vec3(2,4,6)
    v3b=vec3(10,11,12)
    
    assert v3a+v3b == vec3(12,15,18)
    assert v3a-v3b == vec3(-8,-7,-6)
    assert v3a+v3b != vec3(12,3,18)
    assert v3a+v3b != vec3(3,15,18)
    assert v3a+v3b != vec3(12,3,0)
    assert v3a*v3b == vec3(20,44,72)
    assert 5*v3a == vec3(10,20,30)
    assert v3a*5 == vec3(10,20,30)
    
    assert v3a.xyz == v3a
    assert v3a.xxx == vec3(2,2,2)
    assert v3a.yyy == vec3(4,4,4)
    
    m4=mat4(3,1,4,1,5,9,2,6,5,3,5,8,9,7,9,3)
    v4=vec4(2,4,6,7)
    va = v4*m4
    vb = m4*v4
    assert transpose(m4) != m4
    assert transpose(transpose(m4)) == m4
    
    m4i = inverse(m4)
    p=m4*m4i
    p2=m4i*m4
    
    for i in range(4):
        for j in range(4):
            if i == j:
                t=1
            else:
                t=0
            assert abs(p[i][j]-t) < 0.001
            assert abs(p2[i][j]-t) < 0.001
    
    M=axisRotation(vec3(0,1,0),math.radians(90))
    v=vec4(0,0,1,0)*M
    assert abs(dot(v,vec4(0,0,1,0))) < 0.01
    assert abs(dot(v,vec4(1,0,0,0))-1) < 0.01
    
    v1=vec3(3,1,4)
    v2=vec3(-5,2,9)
    v1=normalize(v1)
    v2=normalize(v2)
    v3 = cross(v1,v2)
    assert abs(dot(v1,v3)) < 0.01
    assert abs(dot(v2,v3)) < 0.01
    
    #TODO: FIXME: Finish: Write the rest of the tests
    print("All tests OK")
*/

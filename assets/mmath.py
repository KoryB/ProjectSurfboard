#!/usr/bin/env python3

#matrix/vector math

import sys,struct,random,math,os.path,traceback,re

# a 4d vector: xyzw
class Vector4(object):
    def __init__(self,x,y,z,w):
        self.x=x
        self.y=y
        self.z=z
        self.w=w
    def __add__(self,o):
        assert type(o) == Vector4
        return Vector4(self.x+o.x,self.y+o.y,self.z+o.z,self.w+o.w)
    def __rmul__(self,o):
        if type(o) != float and type(o) != int:
            print(o,type(o))
            assert type(o) == float or type(o) == int
        return Vector4(o*self.x,o*self.y,o*self.z,o*self.w)
    def __sub__(self,o):
        assert type(o) == Vector4
        return Vector4(self.x-o.x,self.y-o.y,self.z-o.z,self.w-o.w)
    def __repr__(self):
        return "[ %f , %f , %f , %f ]" % (self.x,self.y,self.z,self.w) 
    def __getitem__(self,idx):
        if idx ==0:
            return self.x
        elif idx == 1:
            return self.y
        elif idx == 2:
            return self.z
        elif idx == 3:
            return self.w
        else:
            assert 0
def length(v):
    return dot(v,v)**0.5

def dot(v,w):
    assert v.w == 0.0 and w.w == 0.0
    return v.x*w.x+v.y*w.y+v.z*w.z+v.w*w.w

def cross(v,w):
    assert v.w == 0.0 and w.w == 0.0
    return Vector4( v.y*w.z-v.z*w.y, w.x*v.z-v.x*w.z, v.x*w.y-v.y*w.x, 0 )
    
def normalize(v):
    return  1.0/length(v) * v
 
class Quaternion(object):
    def __init__(self,v,a):
        self.a=a
        self.x=v.x
        self.y=v.y
        self.z=v.z
    def __mul__(self,o):
        if type(o) == Quaternion:
            v1=Vector4(self.x,self.y,self.z,0)
            v2=Vector4(o.x,o.y,o.z,0)
            a=self.a*o.a-dot(v1,v2)
            v3=self.a*v2 + o.a*v1 + cross(v1,v2)
            return Quaternion(v3,a)
        else:
            assert 0
    def conj(self):
        return Quaternion(-1.0*self.v,self.a)
    def __repr__(self):
        return "<a=%f axis=%f %f %f>" % (self.a,self.x,self.y,self.z)
        
def quat_for_rot(radians,axis):
    assert type(axis) == Vector4
    c=math.cos(radians/2.0)
    s=math.sin(radians/2.0)
    return Quaternion( s*axis , c )
    
#4x4 matrix
class Matrix4(object):
    def __init__(self,v=None):
        self.M=[ [1,0,0,0] , [0,1,0,0] , [0,0,1,0], [0,0,0,1] ]
        if v != None:
            assert type(v) == list 
            assert len(v) == 16
            c=0
            for i in range(4):
                for j in range(4):
                    self.M[i][j] = v[c]
                    c+=1
                    
    def __mul__(self,o):
        R=Matrix4( [0,0,0,0,  0,0,0,0,  0,0,0,0,  0,0,0,0 ] )
        if type(o) == Matrix4:
            for i in range(4):
                for j in range(4):
                    s=0
                    for k in range(4):
                        s += self.M[i][k] * o.M[k][j]
                    R.M[i][j]=s
            return R
        elif type(o) == Vector4:
            R=[0,0,0,0]
            v=[o.x,o.y,o.z,o.w]
            for i in range(4):
                for j in range(4):
                    R[i] += self.M[i][j] * v[j]
            return Vector4(R[0],R[1],R[2],R[3])
        else:
            assert 0
    
    def transpose(self):
        R=Matrix4()
        for i in range(4):
            for j in range(4):
                R[i][j]=self.M[j][i]
        return R
        
    def __rmul__(self,o):
        v=[o.x,o.y,o.z,o.w]
        R=[0,0,0,0]
        for i in range(4):
            for j in range(4):
                   R[i] += v[j]*self.M[j][i] 

#simulate matrix multiply on two lists of lists
def matrix_multiply(M,N):
    R=[]
    nrm = len(M)
    ncm = len(M[0])
    nrn = len(N)
    ncn = len(N[0])
    assert ncm == nrn
    
    for i in range(nrm):
        R.append([])
        for j in range(ncn):
            summ=0
            for k in range(ncm):
                summ += M[i][k] * N[k][j]
            R[-1].append(summ)

    assert len(R) == nrm
    assert len(R[0]) == ncn
    return R
    
def axis_rotation(axis,angle):
    #from tdl
    x = axis[0];
    y = axis[1];
    z = axis[2];
    n = (x * x + y * y + z * z)**0.5
    if( n == 0.0 ):
        assert 0
  
    x /= n;
    y /= n;
    z /= n;
    xx = x * x;
    yy = y * y;
    zz = z * z;
    c = math.cos(angle);
    s = math.sin(angle);
    oneMinusCosine = 1 - c;

    return Matrix4(
        [xx + (1 - xx) * c,
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
        0, 0, 0, 1]
    )

def translation(T):
    tx=T[0]
    ty=T[1]
    tz=T[2]
    return Matrix4( [
        1,0,0,0,
        0,1,0,0,
        0,0,1,0,
        tx,ty,tz,1
    ])
    
def matrix_to_quat(M):
    #from tdl

    m=[]
    for i in range(4):
        m.append([])
        for j in range(4):
            m[-1].append( M.M[i][j] )
            
    print(m)
    #Choose u, v, and w such that u is the index of the biggest diagonal entry
    #of m, and u v w is an even permutation of 0 1 and 2.
    if (m[0][0] > m[1][1] and m[0][0] > m[2][2]):
        u = 0;
        v = 1;
        w = 2;
    elif (m[1][1] > m[0][0] and m[1][1] > m[2][2]):
        u = 1;
        v = 2;
        w = 0;
    else:
        u = 2;
        v = 0;
        w = 1;

    r = (1 + m[u][u] - m[v][v] - m[w][w])**0.5
    print(r)
    q = [None,None,None,None]
    q[u] = 0.5 * r;
    q[v] = 0.5 * (m[v][u] + m[u][v]) / r;
    q[w] = 0.5 * (m[u][w] + m[w][u]) / r;
    q[3] = 0.5 * (m[v][w] - m[w][v]) / r;
    
    #3 is a; 0,1,2 are axis x,y,z
    Q = Quaternion(Vector4(q[0],q[1],q[2],0),q[3]);
    
    return Q

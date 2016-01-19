

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


import array,math

class mat4:
    def __init__(self,*args):
    
        if len(args) == 0:
            args = [0]*16
        
        L=[]
        for a in args:
            if type(a) == float or type(a) == int:
                L.append(a)
            elif type(a) == vec2:
                L += [a[0],a[1]]
            elif type(a) == vec3:
                L += [a[0],a[1],a[2]]
            elif type(a) == vec4:
                L += [a[0],a[1],a[2],a[3]]
            elif type(a) == type(self):
                for b in a._M:
                    L.append(b)
            elif type(a) == list or type(a) == tuple:
                L += a
            else:
                raise RuntimeError("Bad type for mat constructor")
                
        if len(L) != 16:
            raise RuntimeError("Bad number of arguments for mat constructor")
            
        self._M = array.array("f",L)

    def __add__(self,o):
        if not type(o) == type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._M)):
            L.append( self._M[i]+o._M[i])
        return mat4(L)
        
    def __sub__(self,o):
        if type(o) != type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._M)):
            L.append( self._M[i]-o._M[i])
        return mat4(L)
        
    def __mul__(self,o):
        if type(o) == type(self):
            R=mat4()
            for i in range(4):
                for j in range(4):
                    total=0
                    for k in range(4):
                        total += self[i][k] * o[k][j]
                    R[i][j]=total
            return R
        elif type(o) == vec4:
            R=vec4()
            
            for i in range(4):
                total=0
                for j in range(4):
                    total += self[i][j] * o[j]
                R[i]=total
            return R
        elif type(o) == float or type(o) == int:
            R=mat4( [q*o for q in self._M] )
            return R
        else:
            return NotImplemented
    
    def __rmul__(self,o):
        # o * self
        if type(o) == type(self):
            assert 0        #should never happen
        elif type(o) == vec4:
            assert 0        #should not happen
        elif type(o) == float or type(o) == int:
            R=mat4( [q*o for q in self._M] )
            return R
        else:
            return NotImplemented
    
    def __neg__(self):
        return mat4( [-q for q in self._M] )

    def __pos__(self):
        return mat4( [q for q in self._M] )
        
    def tobytes(self):
        return self._M.tobytes()

    class MProxy:
        def __init__(self,m,i):
            self.m=m
            self.i=i
        def __getitem__(self,j):
            return self.m._M[self.i*4 + j]
        def __setitem__(self,j,v):
            self.m._M[self.i*4+j]=v
            
    def __getitem__(self,i):
        return mat4.MProxy(self,i)
        
    def __eq__(self,o):
        if type(o) != type(self):
            return False
        for i in range(16):
            if self._M[i] != o._M[i]:
                return False
        return True
        
    def __ne__(self,o):
        return not self==o
        
    def __str__(self):
        s=""
        for i in range(4):
            s += "["
            for j in range(4):
                s += "%-4.6f" % self[i][j]
                s += "   "
            s += "]\n"
        return s
    def __repr__(self):
        return str(self)
        
    @staticmethod
    def identity():
        return mat4(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1)
    def transpose(self):
        R=mat4()
        R[ 0 ][ 0 ]=self[ 0 ][ 0 ]
        R[ 0 ][ 1 ]=self[ 1 ][ 0 ]
        R[ 0 ][ 2 ]=self[ 2 ][ 0 ]
        R[ 0 ][ 3 ]=self[ 3 ][ 0 ]
        R[ 1 ][ 0 ]=self[ 0 ][ 1 ]
        R[ 1 ][ 1 ]=self[ 1 ][ 1 ]
        R[ 1 ][ 2 ]=self[ 2 ][ 1 ]
        R[ 1 ][ 3 ]=self[ 3 ][ 1 ]
        R[ 2 ][ 0 ]=self[ 0 ][ 2 ]
        R[ 2 ][ 1 ]=self[ 1 ][ 2 ]
        R[ 2 ][ 2 ]=self[ 2 ][ 2 ]
        R[ 2 ][ 3 ]=self[ 3 ][ 2 ]
        R[ 3 ][ 0 ]=self[ 0 ][ 3 ]
        R[ 3 ][ 1 ]=self[ 1 ][ 3 ]
        R[ 3 ][ 2 ]=self[ 2 ][ 3 ]
        R[ 3 ][ 3 ]=self[ 3 ][ 3 ]
        return R


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


import array,math

class mat3:
    def __init__(self,*args):
    
        if len(args) == 0:
            args = [0]*9
        
        L=[]
        for a in args:
            if type(a) == float or type(a) == int:
                L.append(a)
            elif type(a) == vec2:
                L += [a[0],a[1]]
            elif type(a) == vec3:
                L += [a[0],a[1],a[2]]
            elif type(a) == vec4:
                L += [a[0],a[1],a[2],a[3]]
            elif type(a) == type(self):
                for b in a._M:
                    L.append(b)
            elif type(a) == list or type(a) == tuple:
                L += a
            else:
                raise RuntimeError("Bad type for mat constructor")
                
        if len(L) != 9:
            raise RuntimeError("Bad number of arguments for mat constructor")
            
        self._M = array.array("f",L)

    def __add__(self,o):
        if not type(o) == type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._M)):
            L.append( self._M[i]+o._M[i])
        return mat3(L)
        
    def __sub__(self,o):
        if type(o) != type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._M)):
            L.append( self._M[i]-o._M[i])
        return mat3(L)
        
    def __mul__(self,o):
        if type(o) == type(self):
            R=mat3()
            for i in range(3):
                for j in range(3):
                    total=0
                    for k in range(3):
                        total += self[i][k] * o[k][j]
                    R[i][j]=total
            return R
        elif type(o) == vec3:
            R=vec3()
            
            for i in range(3):
                total=0
                for j in range(3):
                    total += self[i][j] * o[j]
                R[i]=total
            return R
        elif type(o) == float or type(o) == int:
            R=mat3( [q*o for q in self._M] )
            return R
        else:
            return NotImplemented
    
    def __rmul__(self,o):
        # o * self
        if type(o) == type(self):
            assert 0        #should never happen
        elif type(o) == vec3:
            assert 0        #should not happen
        elif type(o) == float or type(o) == int:
            R=mat3( [q*o for q in self._M] )
            return R
        else:
            return NotImplemented
    
    def __neg__(self):
        return mat3( [-q for q in self._M] )

    def __pos__(self):
        return mat3( [q for q in self._M] )
        
    def tobytes(self):
        return self._M.tobytes()

    class MProxy:
        def __init__(self,m,i):
            self.m=m
            self.i=i
        def __getitem__(self,j):
            return self.m._M[self.i*3 + j]
        def __setitem__(self,j,v):
            self.m._M[self.i*3+j]=v
            
    def __getitem__(self,i):
        return mat3.MProxy(self,i)
        
    def __eq__(self,o):
        if type(o) != type(self):
            return False
        for i in range(9):
            if self._M[i] != o._M[i]:
                return False
        return True
        
    def __ne__(self,o):
        return not self==o
        
    def __str__(self):
        s=""
        for i in range(3):
            s += "["
            for j in range(3):
                s += "%-4.6f" % self[i][j]
                s += "   "
            s += "]\n"
        return s
    def __repr__(self):
        return str(self)
        

    def identity():
        return mat3(1,0,0,0,1,0,0,0,1)
    def transpose(self):
        R=mat3()
        R[ 0 ][ 0 ]=self[ 0 ][ 0 ]
        R[ 0 ][ 1 ]=self[ 1 ][ 0 ]
        R[ 0 ][ 2 ]=self[ 2 ][ 0 ]
        R[ 1 ][ 0 ]=self[ 0 ][ 1 ]
        R[ 1 ][ 1 ]=self[ 1 ][ 1 ]
        R[ 1 ][ 2 ]=self[ 2 ][ 1 ]
        R[ 2 ][ 0 ]=self[ 0 ][ 2 ]
        R[ 2 ][ 1 ]=self[ 1 ][ 2 ]
        R[ 2 ][ 2 ]=self[ 2 ][ 2 ]
        return R


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


import array,math

class mat2:
    def __init__(self,*args):
    
        if len(args) == 0:
            args = [0]*4
        
        L=[]
        for a in args:
            if type(a) == float or type(a) == int:
                L.append(a)
            elif type(a) == vec2:
                L += [a[0],a[1]]
            elif type(a) == vec3:
                L += [a[0],a[1],a[2]]
            elif type(a) == vec4:
                L += [a[0],a[1],a[2],a[3]]
            elif type(a) == type(self):
                for b in a._M:
                    L.append(b)
            elif type(a) == list or type(a) == tuple:
                L += a
            else:
                raise RuntimeError("Bad type for mat constructor")
                
        if len(L) != 4:
            raise RuntimeError("Bad number of arguments for mat constructor")
            
        self._M = array.array("f",L)

    def __add__(self,o):
        if not type(o) == type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._M)):
            L.append( self._M[i]+o._M[i])
        return mat2(L)
        
    def __sub__(self,o):
        if type(o) != type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._M)):
            L.append( self._M[i]-o._M[i])
        return mat2(L)
        
    def __mul__(self,o):
        if type(o) == type(self):
            R=mat2()
            for i in range(2):
                for j in range(2):
                    total=0
                    for k in range(2):
                        total += self[i][k] * o[k][j]
                    R[i][j]=total
            return R
        elif type(o) == vec2:
            R=vec2()
            
            for i in range(2):
                total=0
                for j in range(2):
                    total += self[i][j] * o[j]
                R[i]=total
            return R
        elif type(o) == float or type(o) == int:
            R=mat2( [q*o for q in self._M] )
            return R
        else:
            return NotImplemented
    
    def __rmul__(self,o):
        # o * self
        if type(o) == type(self):
            assert 0        #should never happen
        elif type(o) == vec2:
            assert 0        #should not happen
        elif type(o) == float or type(o) == int:
            R=mat2( [q*o for q in self._M] )
            return R
        else:
            return NotImplemented
    
    def __neg__(self):
        return mat2( [-q for q in self._M] )

    def __pos__(self):
        return mat2( [q for q in self._M] )
        
    def tobytes(self):
        return self._M.tobytes()

    class MProxy:
        def __init__(self,m,i):
            self.m=m
            self.i=i
        def __getitem__(self,j):
            return self.m._M[self.i*2 + j]
        def __setitem__(self,j,v):
            self.m._M[self.i*2+j]=v
            
    def __getitem__(self,i):
        return mat2.MProxy(self,i)
        
    def __eq__(self,o):
        if type(o) != type(self):
            return False
        for i in range(4):
            if self._M[i] != o._M[i]:
                return False
        return True
        
    def __ne__(self,o):
        return not self==o
        
    def __str__(self):
        s=""
        for i in range(2):
            s += "["
            for j in range(2):
                s += "%-4.6f" % self[i][j]
                s += "   "
            s += "]\n"
        return s
    def __repr__(self):
        return str(self)
        

    def identity():
        return mat2(1,0,0,1)
    def transpose(self):
        R=mat2()
        R[ 0 ][ 0 ]=self[ 0 ][ 0 ]
        R[ 0 ][ 1 ]=self[ 1 ][ 0 ]
        R[ 1 ][ 0 ]=self[ 0 ][ 1 ]
        R[ 1 ][ 1 ]=self[ 1 ][ 1 ]
        return R
class vec4:
    def __init__(self,*args):
        if len(args)==0:
            args=[ 0,0,0,0 ]

        L=[]
        for a in args:
            if type(a) == float:
                L.append(a)
            elif type(a) == int:
                L.append(a)
            elif type(a) == vec2:
                L += (a.x,a,y)
            elif type(a) == vec3:
                L += (a.x,a.y,a.z)
            elif type(a) == vec4:
                L += (a.x,a.y,a.z,a.w)
            elif type(a) == list or type(a) == tuple:
                L += a
            elif type(a) == array.array:
                L += [q for q in a]
            else:
                raise RuntimeError("Bad argument to vec constructor: "+str(type(a)))
        if len(L) == 1:
            L=L[0]*4
        if len(L) != 4:
            raise RuntimeError("Bad number of items to vec constructor")
        self._v = array.array("f",L)

    def tobytes(self):
        return self._v.tobytes()
        
    def __getitem__(self,key):
        return self._v[key]
            
    def __setitem__(self,key,value):
        self._v[key]=value
        
    def __str__(self):
        return "vec4(" + ",".join([str(q) for q in self._v])+")"
        
    def __repr__(self):
        return str(self)
      
    def __len__(self):
        return 4
        
    def __add__(self,o):
        if not type(o) == type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._v)):
            L.append( self._v[i]+o._v[i])
        return vec4(L)
        
    def __sub__(self,o):
        if type(o) != type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._v)):
            L.append( self._v[i]-o._v[i])
        return vec4(L)
        
    def __mul__(self,o):
        if type(o) == type(self):
            R=vec4()
            for i in range(4):
                R[i] = self[i]*o[i]
            return R
        elif type(o) == mat4:
            R=vec4()
            
            for i in range(4):
                total=0
                for j in range(4):
                    total += self[j]*o[j][i]
                R[i]=total
            return R
        elif type(o) == float or type(o) == int:
            R=vec4( [q*o for q in self._v] )
            return R
        else:
            return NotImplemented
    
    def __rmul__(self,o):
        # o * self
        if type(o) == type(self):
            assert 0        #should never happen
        elif type(o) == mat4:
            assert 0        #should not happen
        elif type(o) == float or type(o) == int:
            R=vec4( [q*o for q in self._v] )
            return R
        else:
            return NotImplemented
    
    def __neg__(self):
        return vec4( [-q for q in self._v] )

    def __pos__(self):
        return vec4( [q for q in self._v] )
     
    def __iter__(self):
        return self._v.__iter__()
      
    def __eq__(self,o):
        if type(o) != type(self):
            return False
        for i in range(4):
            if self._v[i] != o._v[i]:
                return False
        return True
        
    def __ne__(self,o):
        return not self==o
        
        
        
    
    def _getx(self):
        return self._v[ 0 ]
    def _setx(self,v):
        self._v[ 0 ]=v
    x = property(_getx , _setx )
    def _gety(self):
        return self._v[ 1 ]
    def _sety(self,v):
        self._v[ 1 ]=v
    y = property(_gety , _sety )
    def _getz(self):
        return self._v[ 2 ]
    def _setz(self,v):
        self._v[ 2 ]=v
    z = property(_getz , _setz )
    def _getw(self):
        return self._v[ 3 ]
    def _setw(self,v):
        self._v[ 3 ]=v
    w = property(_getw , _setw )
    def _getww(self):
        return vec2 ( self.w,self.w )
    ww = property( _getww )
    def _getwww(self):
        return vec3 ( self.w,self.w,self.w )
    www = property( _getwww )
    def _getwwww(self):
        return vec4 ( self.w,self.w,self.w,self.w )
    wwww = property( _getwwww )
    def _getwwwx(self):
        return vec4 ( self.w,self.w,self.w,self.x )
    wwwx = property( _getwwwx )
    def _getwwwy(self):
        return vec4 ( self.w,self.w,self.w,self.y )
    wwwy = property( _getwwwy )
    def _getwwwz(self):
        return vec4 ( self.w,self.w,self.w,self.z )
    wwwz = property( _getwwwz )
    def _getwwx(self):
        return vec3 ( self.w,self.w,self.x )
    wwx = property( _getwwx )
    def _getwwxw(self):
        return vec4 ( self.w,self.w,self.x,self.w )
    wwxw = property( _getwwxw )
    def _getwwxx(self):
        return vec4 ( self.w,self.w,self.x,self.x )
    wwxx = property( _getwwxx )
    def _getwwxy(self):
        return vec4 ( self.w,self.w,self.x,self.y )
    wwxy = property( _getwwxy )
    def _getwwxz(self):
        return vec4 ( self.w,self.w,self.x,self.z )
    wwxz = property( _getwwxz )
    def _getwwy(self):
        return vec3 ( self.w,self.w,self.y )
    wwy = property( _getwwy )
    def _getwwyw(self):
        return vec4 ( self.w,self.w,self.y,self.w )
    wwyw = property( _getwwyw )
    def _getwwyx(self):
        return vec4 ( self.w,self.w,self.y,self.x )
    wwyx = property( _getwwyx )
    def _getwwyy(self):
        return vec4 ( self.w,self.w,self.y,self.y )
    wwyy = property( _getwwyy )
    def _getwwyz(self):
        return vec4 ( self.w,self.w,self.y,self.z )
    wwyz = property( _getwwyz )
    def _getwwz(self):
        return vec3 ( self.w,self.w,self.z )
    wwz = property( _getwwz )
    def _getwwzw(self):
        return vec4 ( self.w,self.w,self.z,self.w )
    wwzw = property( _getwwzw )
    def _getwwzx(self):
        return vec4 ( self.w,self.w,self.z,self.x )
    wwzx = property( _getwwzx )
    def _getwwzy(self):
        return vec4 ( self.w,self.w,self.z,self.y )
    wwzy = property( _getwwzy )
    def _getwwzz(self):
        return vec4 ( self.w,self.w,self.z,self.z )
    wwzz = property( _getwwzz )
    def _getwx(self):
        return vec2 ( self.w,self.x )
    def _setwx(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.x=v[1]
    wx = property( _getwx , _setwx )
    def _getwxw(self):
        return vec3 ( self.w,self.x,self.w )
    wxw = property( _getwxw )
    def _getwxww(self):
        return vec4 ( self.w,self.x,self.w,self.w )
    wxww = property( _getwxww )
    def _getwxwx(self):
        return vec4 ( self.w,self.x,self.w,self.x )
    wxwx = property( _getwxwx )
    def _getwxwy(self):
        return vec4 ( self.w,self.x,self.w,self.y )
    wxwy = property( _getwxwy )
    def _getwxwz(self):
        return vec4 ( self.w,self.x,self.w,self.z )
    wxwz = property( _getwxwz )
    def _getwxx(self):
        return vec3 ( self.w,self.x,self.x )
    wxx = property( _getwxx )
    def _getwxxw(self):
        return vec4 ( self.w,self.x,self.x,self.w )
    wxxw = property( _getwxxw )
    def _getwxxx(self):
        return vec4 ( self.w,self.x,self.x,self.x )
    wxxx = property( _getwxxx )
    def _getwxxy(self):
        return vec4 ( self.w,self.x,self.x,self.y )
    wxxy = property( _getwxxy )
    def _getwxxz(self):
        return vec4 ( self.w,self.x,self.x,self.z )
    wxxz = property( _getwxxz )
    def _getwxy(self):
        return vec3 ( self.w,self.x,self.y )
    def _setwxy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.x=v[1]
        self.y=v[2]
    wxy = property( _getwxy , _setwxy )
    def _getwxyw(self):
        return vec4 ( self.w,self.x,self.y,self.w )
    wxyw = property( _getwxyw )
    def _getwxyx(self):
        return vec4 ( self.w,self.x,self.y,self.x )
    wxyx = property( _getwxyx )
    def _getwxyy(self):
        return vec4 ( self.w,self.x,self.y,self.y )
    wxyy = property( _getwxyy )
    def _getwxyz(self):
        return vec4 ( self.w,self.x,self.y,self.z )
    def _setwxyz(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.x=v[1]
        self.y=v[2]
        self.z=v[3]
    wxyz = property( _getwxyz , _setwxyz )
    def _getwxz(self):
        return vec3 ( self.w,self.x,self.z )
    def _setwxz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.x=v[1]
        self.z=v[2]
    wxz = property( _getwxz , _setwxz )
    def _getwxzw(self):
        return vec4 ( self.w,self.x,self.z,self.w )
    wxzw = property( _getwxzw )
    def _getwxzx(self):
        return vec4 ( self.w,self.x,self.z,self.x )
    wxzx = property( _getwxzx )
    def _getwxzy(self):
        return vec4 ( self.w,self.x,self.z,self.y )
    def _setwxzy(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.x=v[1]
        self.z=v[2]
        self.y=v[3]
    wxzy = property( _getwxzy , _setwxzy )
    def _getwxzz(self):
        return vec4 ( self.w,self.x,self.z,self.z )
    wxzz = property( _getwxzz )
    def _getwy(self):
        return vec2 ( self.w,self.y )
    def _setwy(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.y=v[1]
    wy = property( _getwy , _setwy )
    def _getwyw(self):
        return vec3 ( self.w,self.y,self.w )
    wyw = property( _getwyw )
    def _getwyww(self):
        return vec4 ( self.w,self.y,self.w,self.w )
    wyww = property( _getwyww )
    def _getwywx(self):
        return vec4 ( self.w,self.y,self.w,self.x )
    wywx = property( _getwywx )
    def _getwywy(self):
        return vec4 ( self.w,self.y,self.w,self.y )
    wywy = property( _getwywy )
    def _getwywz(self):
        return vec4 ( self.w,self.y,self.w,self.z )
    wywz = property( _getwywz )
    def _getwyx(self):
        return vec3 ( self.w,self.y,self.x )
    def _setwyx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.y=v[1]
        self.x=v[2]
    wyx = property( _getwyx , _setwyx )
    def _getwyxw(self):
        return vec4 ( self.w,self.y,self.x,self.w )
    wyxw = property( _getwyxw )
    def _getwyxx(self):
        return vec4 ( self.w,self.y,self.x,self.x )
    wyxx = property( _getwyxx )
    def _getwyxy(self):
        return vec4 ( self.w,self.y,self.x,self.y )
    wyxy = property( _getwyxy )
    def _getwyxz(self):
        return vec4 ( self.w,self.y,self.x,self.z )
    def _setwyxz(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.y=v[1]
        self.x=v[2]
        self.z=v[3]
    wyxz = property( _getwyxz , _setwyxz )
    def _getwyy(self):
        return vec3 ( self.w,self.y,self.y )
    wyy = property( _getwyy )
    def _getwyyw(self):
        return vec4 ( self.w,self.y,self.y,self.w )
    wyyw = property( _getwyyw )
    def _getwyyx(self):
        return vec4 ( self.w,self.y,self.y,self.x )
    wyyx = property( _getwyyx )
    def _getwyyy(self):
        return vec4 ( self.w,self.y,self.y,self.y )
    wyyy = property( _getwyyy )
    def _getwyyz(self):
        return vec4 ( self.w,self.y,self.y,self.z )
    wyyz = property( _getwyyz )
    def _getwyz(self):
        return vec3 ( self.w,self.y,self.z )
    def _setwyz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.y=v[1]
        self.z=v[2]
    wyz = property( _getwyz , _setwyz )
    def _getwyzw(self):
        return vec4 ( self.w,self.y,self.z,self.w )
    wyzw = property( _getwyzw )
    def _getwyzx(self):
        return vec4 ( self.w,self.y,self.z,self.x )
    def _setwyzx(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.y=v[1]
        self.z=v[2]
        self.x=v[3]
    wyzx = property( _getwyzx , _setwyzx )
    def _getwyzy(self):
        return vec4 ( self.w,self.y,self.z,self.y )
    wyzy = property( _getwyzy )
    def _getwyzz(self):
        return vec4 ( self.w,self.y,self.z,self.z )
    wyzz = property( _getwyzz )
    def _getwz(self):
        return vec2 ( self.w,self.z )
    def _setwz(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.z=v[1]
    wz = property( _getwz , _setwz )
    def _getwzw(self):
        return vec3 ( self.w,self.z,self.w )
    wzw = property( _getwzw )
    def _getwzww(self):
        return vec4 ( self.w,self.z,self.w,self.w )
    wzww = property( _getwzww )
    def _getwzwx(self):
        return vec4 ( self.w,self.z,self.w,self.x )
    wzwx = property( _getwzwx )
    def _getwzwy(self):
        return vec4 ( self.w,self.z,self.w,self.y )
    wzwy = property( _getwzwy )
    def _getwzwz(self):
        return vec4 ( self.w,self.z,self.w,self.z )
    wzwz = property( _getwzwz )
    def _getwzx(self):
        return vec3 ( self.w,self.z,self.x )
    def _setwzx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.z=v[1]
        self.x=v[2]
    wzx = property( _getwzx , _setwzx )
    def _getwzxw(self):
        return vec4 ( self.w,self.z,self.x,self.w )
    wzxw = property( _getwzxw )
    def _getwzxx(self):
        return vec4 ( self.w,self.z,self.x,self.x )
    wzxx = property( _getwzxx )
    def _getwzxy(self):
        return vec4 ( self.w,self.z,self.x,self.y )
    def _setwzxy(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.z=v[1]
        self.x=v[2]
        self.y=v[3]
    wzxy = property( _getwzxy , _setwzxy )
    def _getwzxz(self):
        return vec4 ( self.w,self.z,self.x,self.z )
    wzxz = property( _getwzxz )
    def _getwzy(self):
        return vec3 ( self.w,self.z,self.y )
    def _setwzy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.z=v[1]
        self.y=v[2]
    wzy = property( _getwzy , _setwzy )
    def _getwzyw(self):
        return vec4 ( self.w,self.z,self.y,self.w )
    wzyw = property( _getwzyw )
    def _getwzyx(self):
        return vec4 ( self.w,self.z,self.y,self.x )
    def _setwzyx(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.w=v[0]
        self.z=v[1]
        self.y=v[2]
        self.x=v[3]
    wzyx = property( _getwzyx , _setwzyx )
    def _getwzyy(self):
        return vec4 ( self.w,self.z,self.y,self.y )
    wzyy = property( _getwzyy )
    def _getwzyz(self):
        return vec4 ( self.w,self.z,self.y,self.z )
    wzyz = property( _getwzyz )
    def _getwzz(self):
        return vec3 ( self.w,self.z,self.z )
    wzz = property( _getwzz )
    def _getwzzw(self):
        return vec4 ( self.w,self.z,self.z,self.w )
    wzzw = property( _getwzzw )
    def _getwzzx(self):
        return vec4 ( self.w,self.z,self.z,self.x )
    wzzx = property( _getwzzx )
    def _getwzzy(self):
        return vec4 ( self.w,self.z,self.z,self.y )
    wzzy = property( _getwzzy )
    def _getwzzz(self):
        return vec4 ( self.w,self.z,self.z,self.z )
    wzzz = property( _getwzzz )
    def _getxw(self):
        return vec2 ( self.x,self.w )
    def _setxw(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.w=v[1]
    xw = property( _getxw , _setxw )
    def _getxww(self):
        return vec3 ( self.x,self.w,self.w )
    xww = property( _getxww )
    def _getxwww(self):
        return vec4 ( self.x,self.w,self.w,self.w )
    xwww = property( _getxwww )
    def _getxwwx(self):
        return vec4 ( self.x,self.w,self.w,self.x )
    xwwx = property( _getxwwx )
    def _getxwwy(self):
        return vec4 ( self.x,self.w,self.w,self.y )
    xwwy = property( _getxwwy )
    def _getxwwz(self):
        return vec4 ( self.x,self.w,self.w,self.z )
    xwwz = property( _getxwwz )
    def _getxwx(self):
        return vec3 ( self.x,self.w,self.x )
    xwx = property( _getxwx )
    def _getxwxw(self):
        return vec4 ( self.x,self.w,self.x,self.w )
    xwxw = property( _getxwxw )
    def _getxwxx(self):
        return vec4 ( self.x,self.w,self.x,self.x )
    xwxx = property( _getxwxx )
    def _getxwxy(self):
        return vec4 ( self.x,self.w,self.x,self.y )
    xwxy = property( _getxwxy )
    def _getxwxz(self):
        return vec4 ( self.x,self.w,self.x,self.z )
    xwxz = property( _getxwxz )
    def _getxwy(self):
        return vec3 ( self.x,self.w,self.y )
    def _setxwy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.w=v[1]
        self.y=v[2]
    xwy = property( _getxwy , _setxwy )
    def _getxwyw(self):
        return vec4 ( self.x,self.w,self.y,self.w )
    xwyw = property( _getxwyw )
    def _getxwyx(self):
        return vec4 ( self.x,self.w,self.y,self.x )
    xwyx = property( _getxwyx )
    def _getxwyy(self):
        return vec4 ( self.x,self.w,self.y,self.y )
    xwyy = property( _getxwyy )
    def _getxwyz(self):
        return vec4 ( self.x,self.w,self.y,self.z )
    def _setxwyz(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.w=v[1]
        self.y=v[2]
        self.z=v[3]
    xwyz = property( _getxwyz , _setxwyz )
    def _getxwz(self):
        return vec3 ( self.x,self.w,self.z )
    def _setxwz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.w=v[1]
        self.z=v[2]
    xwz = property( _getxwz , _setxwz )
    def _getxwzw(self):
        return vec4 ( self.x,self.w,self.z,self.w )
    xwzw = property( _getxwzw )
    def _getxwzx(self):
        return vec4 ( self.x,self.w,self.z,self.x )
    xwzx = property( _getxwzx )
    def _getxwzy(self):
        return vec4 ( self.x,self.w,self.z,self.y )
    def _setxwzy(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.w=v[1]
        self.z=v[2]
        self.y=v[3]
    xwzy = property( _getxwzy , _setxwzy )
    def _getxwzz(self):
        return vec4 ( self.x,self.w,self.z,self.z )
    xwzz = property( _getxwzz )
    def _getxx(self):
        return vec2 ( self.x,self.x )
    xx = property( _getxx )
    def _getxxw(self):
        return vec3 ( self.x,self.x,self.w )
    xxw = property( _getxxw )
    def _getxxww(self):
        return vec4 ( self.x,self.x,self.w,self.w )
    xxww = property( _getxxww )
    def _getxxwx(self):
        return vec4 ( self.x,self.x,self.w,self.x )
    xxwx = property( _getxxwx )
    def _getxxwy(self):
        return vec4 ( self.x,self.x,self.w,self.y )
    xxwy = property( _getxxwy )
    def _getxxwz(self):
        return vec4 ( self.x,self.x,self.w,self.z )
    xxwz = property( _getxxwz )
    def _getxxx(self):
        return vec3 ( self.x,self.x,self.x )
    xxx = property( _getxxx )
    def _getxxxw(self):
        return vec4 ( self.x,self.x,self.x,self.w )
    xxxw = property( _getxxxw )
    def _getxxxx(self):
        return vec4 ( self.x,self.x,self.x,self.x )
    xxxx = property( _getxxxx )
    def _getxxxy(self):
        return vec4 ( self.x,self.x,self.x,self.y )
    xxxy = property( _getxxxy )
    def _getxxxz(self):
        return vec4 ( self.x,self.x,self.x,self.z )
    xxxz = property( _getxxxz )
    def _getxxy(self):
        return vec3 ( self.x,self.x,self.y )
    xxy = property( _getxxy )
    def _getxxyw(self):
        return vec4 ( self.x,self.x,self.y,self.w )
    xxyw = property( _getxxyw )
    def _getxxyx(self):
        return vec4 ( self.x,self.x,self.y,self.x )
    xxyx = property( _getxxyx )
    def _getxxyy(self):
        return vec4 ( self.x,self.x,self.y,self.y )
    xxyy = property( _getxxyy )
    def _getxxyz(self):
        return vec4 ( self.x,self.x,self.y,self.z )
    xxyz = property( _getxxyz )
    def _getxxz(self):
        return vec3 ( self.x,self.x,self.z )
    xxz = property( _getxxz )
    def _getxxzw(self):
        return vec4 ( self.x,self.x,self.z,self.w )
    xxzw = property( _getxxzw )
    def _getxxzx(self):
        return vec4 ( self.x,self.x,self.z,self.x )
    xxzx = property( _getxxzx )
    def _getxxzy(self):
        return vec4 ( self.x,self.x,self.z,self.y )
    xxzy = property( _getxxzy )
    def _getxxzz(self):
        return vec4 ( self.x,self.x,self.z,self.z )
    xxzz = property( _getxxzz )
    def _getxy(self):
        return vec2 ( self.x,self.y )
    def _setxy(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
    xy = property( _getxy , _setxy )
    def _getxyw(self):
        return vec3 ( self.x,self.y,self.w )
    def _setxyw(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
        self.w=v[2]
    xyw = property( _getxyw , _setxyw )
    def _getxyww(self):
        return vec4 ( self.x,self.y,self.w,self.w )
    xyww = property( _getxyww )
    def _getxywx(self):
        return vec4 ( self.x,self.y,self.w,self.x )
    xywx = property( _getxywx )
    def _getxywy(self):
        return vec4 ( self.x,self.y,self.w,self.y )
    xywy = property( _getxywy )
    def _getxywz(self):
        return vec4 ( self.x,self.y,self.w,self.z )
    def _setxywz(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
        self.w=v[2]
        self.z=v[3]
    xywz = property( _getxywz , _setxywz )
    def _getxyx(self):
        return vec3 ( self.x,self.y,self.x )
    xyx = property( _getxyx )
    def _getxyxw(self):
        return vec4 ( self.x,self.y,self.x,self.w )
    xyxw = property( _getxyxw )
    def _getxyxx(self):
        return vec4 ( self.x,self.y,self.x,self.x )
    xyxx = property( _getxyxx )
    def _getxyxy(self):
        return vec4 ( self.x,self.y,self.x,self.y )
    xyxy = property( _getxyxy )
    def _getxyxz(self):
        return vec4 ( self.x,self.y,self.x,self.z )
    xyxz = property( _getxyxz )
    def _getxyy(self):
        return vec3 ( self.x,self.y,self.y )
    xyy = property( _getxyy )
    def _getxyyw(self):
        return vec4 ( self.x,self.y,self.y,self.w )
    xyyw = property( _getxyyw )
    def _getxyyx(self):
        return vec4 ( self.x,self.y,self.y,self.x )
    xyyx = property( _getxyyx )
    def _getxyyy(self):
        return vec4 ( self.x,self.y,self.y,self.y )
    xyyy = property( _getxyyy )
    def _getxyyz(self):
        return vec4 ( self.x,self.y,self.y,self.z )
    xyyz = property( _getxyyz )
    def _getxyz(self):
        return vec3 ( self.x,self.y,self.z )
    def _setxyz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
        self.z=v[2]
    xyz = property( _getxyz , _setxyz )
    def _getxyzw(self):
        return vec4 ( self.x,self.y,self.z,self.w )
    def _setxyzw(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
        self.z=v[2]
        self.w=v[3]
    xyzw = property( _getxyzw , _setxyzw )
    def _getxyzx(self):
        return vec4 ( self.x,self.y,self.z,self.x )
    xyzx = property( _getxyzx )
    def _getxyzy(self):
        return vec4 ( self.x,self.y,self.z,self.y )
    xyzy = property( _getxyzy )
    def _getxyzz(self):
        return vec4 ( self.x,self.y,self.z,self.z )
    xyzz = property( _getxyzz )
    def _getxz(self):
        return vec2 ( self.x,self.z )
    def _setxz(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.z=v[1]
    xz = property( _getxz , _setxz )
    def _getxzw(self):
        return vec3 ( self.x,self.z,self.w )
    def _setxzw(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.z=v[1]
        self.w=v[2]
    xzw = property( _getxzw , _setxzw )
    def _getxzww(self):
        return vec4 ( self.x,self.z,self.w,self.w )
    xzww = property( _getxzww )
    def _getxzwx(self):
        return vec4 ( self.x,self.z,self.w,self.x )
    xzwx = property( _getxzwx )
    def _getxzwy(self):
        return vec4 ( self.x,self.z,self.w,self.y )
    def _setxzwy(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.z=v[1]
        self.w=v[2]
        self.y=v[3]
    xzwy = property( _getxzwy , _setxzwy )
    def _getxzwz(self):
        return vec4 ( self.x,self.z,self.w,self.z )
    xzwz = property( _getxzwz )
    def _getxzx(self):
        return vec3 ( self.x,self.z,self.x )
    xzx = property( _getxzx )
    def _getxzxw(self):
        return vec4 ( self.x,self.z,self.x,self.w )
    xzxw = property( _getxzxw )
    def _getxzxx(self):
        return vec4 ( self.x,self.z,self.x,self.x )
    xzxx = property( _getxzxx )
    def _getxzxy(self):
        return vec4 ( self.x,self.z,self.x,self.y )
    xzxy = property( _getxzxy )
    def _getxzxz(self):
        return vec4 ( self.x,self.z,self.x,self.z )
    xzxz = property( _getxzxz )
    def _getxzy(self):
        return vec3 ( self.x,self.z,self.y )
    def _setxzy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.z=v[1]
        self.y=v[2]
    xzy = property( _getxzy , _setxzy )
    def _getxzyw(self):
        return vec4 ( self.x,self.z,self.y,self.w )
    def _setxzyw(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.z=v[1]
        self.y=v[2]
        self.w=v[3]
    xzyw = property( _getxzyw , _setxzyw )
    def _getxzyx(self):
        return vec4 ( self.x,self.z,self.y,self.x )
    xzyx = property( _getxzyx )
    def _getxzyy(self):
        return vec4 ( self.x,self.z,self.y,self.y )
    xzyy = property( _getxzyy )
    def _getxzyz(self):
        return vec4 ( self.x,self.z,self.y,self.z )
    xzyz = property( _getxzyz )
    def _getxzz(self):
        return vec3 ( self.x,self.z,self.z )
    xzz = property( _getxzz )
    def _getxzzw(self):
        return vec4 ( self.x,self.z,self.z,self.w )
    xzzw = property( _getxzzw )
    def _getxzzx(self):
        return vec4 ( self.x,self.z,self.z,self.x )
    xzzx = property( _getxzzx )
    def _getxzzy(self):
        return vec4 ( self.x,self.z,self.z,self.y )
    xzzy = property( _getxzzy )
    def _getxzzz(self):
        return vec4 ( self.x,self.z,self.z,self.z )
    xzzz = property( _getxzzz )
    def _getyw(self):
        return vec2 ( self.y,self.w )
    def _setyw(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.w=v[1]
    yw = property( _getyw , _setyw )
    def _getyww(self):
        return vec3 ( self.y,self.w,self.w )
    yww = property( _getyww )
    def _getywww(self):
        return vec4 ( self.y,self.w,self.w,self.w )
    ywww = property( _getywww )
    def _getywwx(self):
        return vec4 ( self.y,self.w,self.w,self.x )
    ywwx = property( _getywwx )
    def _getywwy(self):
        return vec4 ( self.y,self.w,self.w,self.y )
    ywwy = property( _getywwy )
    def _getywwz(self):
        return vec4 ( self.y,self.w,self.w,self.z )
    ywwz = property( _getywwz )
    def _getywx(self):
        return vec3 ( self.y,self.w,self.x )
    def _setywx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.w=v[1]
        self.x=v[2]
    ywx = property( _getywx , _setywx )
    def _getywxw(self):
        return vec4 ( self.y,self.w,self.x,self.w )
    ywxw = property( _getywxw )
    def _getywxx(self):
        return vec4 ( self.y,self.w,self.x,self.x )
    ywxx = property( _getywxx )
    def _getywxy(self):
        return vec4 ( self.y,self.w,self.x,self.y )
    ywxy = property( _getywxy )
    def _getywxz(self):
        return vec4 ( self.y,self.w,self.x,self.z )
    def _setywxz(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.w=v[1]
        self.x=v[2]
        self.z=v[3]
    ywxz = property( _getywxz , _setywxz )
    def _getywy(self):
        return vec3 ( self.y,self.w,self.y )
    ywy = property( _getywy )
    def _getywyw(self):
        return vec4 ( self.y,self.w,self.y,self.w )
    ywyw = property( _getywyw )
    def _getywyx(self):
        return vec4 ( self.y,self.w,self.y,self.x )
    ywyx = property( _getywyx )
    def _getywyy(self):
        return vec4 ( self.y,self.w,self.y,self.y )
    ywyy = property( _getywyy )
    def _getywyz(self):
        return vec4 ( self.y,self.w,self.y,self.z )
    ywyz = property( _getywyz )
    def _getywz(self):
        return vec3 ( self.y,self.w,self.z )
    def _setywz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.w=v[1]
        self.z=v[2]
    ywz = property( _getywz , _setywz )
    def _getywzw(self):
        return vec4 ( self.y,self.w,self.z,self.w )
    ywzw = property( _getywzw )
    def _getywzx(self):
        return vec4 ( self.y,self.w,self.z,self.x )
    def _setywzx(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.w=v[1]
        self.z=v[2]
        self.x=v[3]
    ywzx = property( _getywzx , _setywzx )
    def _getywzy(self):
        return vec4 ( self.y,self.w,self.z,self.y )
    ywzy = property( _getywzy )
    def _getywzz(self):
        return vec4 ( self.y,self.w,self.z,self.z )
    ywzz = property( _getywzz )
    def _getyx(self):
        return vec2 ( self.y,self.x )
    def _setyx(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
    yx = property( _getyx , _setyx )
    def _getyxw(self):
        return vec3 ( self.y,self.x,self.w )
    def _setyxw(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
        self.w=v[2]
    yxw = property( _getyxw , _setyxw )
    def _getyxww(self):
        return vec4 ( self.y,self.x,self.w,self.w )
    yxww = property( _getyxww )
    def _getyxwx(self):
        return vec4 ( self.y,self.x,self.w,self.x )
    yxwx = property( _getyxwx )
    def _getyxwy(self):
        return vec4 ( self.y,self.x,self.w,self.y )
    yxwy = property( _getyxwy )
    def _getyxwz(self):
        return vec4 ( self.y,self.x,self.w,self.z )
    def _setyxwz(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
        self.w=v[2]
        self.z=v[3]
    yxwz = property( _getyxwz , _setyxwz )
    def _getyxx(self):
        return vec3 ( self.y,self.x,self.x )
    yxx = property( _getyxx )
    def _getyxxw(self):
        return vec4 ( self.y,self.x,self.x,self.w )
    yxxw = property( _getyxxw )
    def _getyxxx(self):
        return vec4 ( self.y,self.x,self.x,self.x )
    yxxx = property( _getyxxx )
    def _getyxxy(self):
        return vec4 ( self.y,self.x,self.x,self.y )
    yxxy = property( _getyxxy )
    def _getyxxz(self):
        return vec4 ( self.y,self.x,self.x,self.z )
    yxxz = property( _getyxxz )
    def _getyxy(self):
        return vec3 ( self.y,self.x,self.y )
    yxy = property( _getyxy )
    def _getyxyw(self):
        return vec4 ( self.y,self.x,self.y,self.w )
    yxyw = property( _getyxyw )
    def _getyxyx(self):
        return vec4 ( self.y,self.x,self.y,self.x )
    yxyx = property( _getyxyx )
    def _getyxyy(self):
        return vec4 ( self.y,self.x,self.y,self.y )
    yxyy = property( _getyxyy )
    def _getyxyz(self):
        return vec4 ( self.y,self.x,self.y,self.z )
    yxyz = property( _getyxyz )
    def _getyxz(self):
        return vec3 ( self.y,self.x,self.z )
    def _setyxz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
        self.z=v[2]
    yxz = property( _getyxz , _setyxz )
    def _getyxzw(self):
        return vec4 ( self.y,self.x,self.z,self.w )
    def _setyxzw(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
        self.z=v[2]
        self.w=v[3]
    yxzw = property( _getyxzw , _setyxzw )
    def _getyxzx(self):
        return vec4 ( self.y,self.x,self.z,self.x )
    yxzx = property( _getyxzx )
    def _getyxzy(self):
        return vec4 ( self.y,self.x,self.z,self.y )
    yxzy = property( _getyxzy )
    def _getyxzz(self):
        return vec4 ( self.y,self.x,self.z,self.z )
    yxzz = property( _getyxzz )
    def _getyy(self):
        return vec2 ( self.y,self.y )
    yy = property( _getyy )
    def _getyyw(self):
        return vec3 ( self.y,self.y,self.w )
    yyw = property( _getyyw )
    def _getyyww(self):
        return vec4 ( self.y,self.y,self.w,self.w )
    yyww = property( _getyyww )
    def _getyywx(self):
        return vec4 ( self.y,self.y,self.w,self.x )
    yywx = property( _getyywx )
    def _getyywy(self):
        return vec4 ( self.y,self.y,self.w,self.y )
    yywy = property( _getyywy )
    def _getyywz(self):
        return vec4 ( self.y,self.y,self.w,self.z )
    yywz = property( _getyywz )
    def _getyyx(self):
        return vec3 ( self.y,self.y,self.x )
    yyx = property( _getyyx )
    def _getyyxw(self):
        return vec4 ( self.y,self.y,self.x,self.w )
    yyxw = property( _getyyxw )
    def _getyyxx(self):
        return vec4 ( self.y,self.y,self.x,self.x )
    yyxx = property( _getyyxx )
    def _getyyxy(self):
        return vec4 ( self.y,self.y,self.x,self.y )
    yyxy = property( _getyyxy )
    def _getyyxz(self):
        return vec4 ( self.y,self.y,self.x,self.z )
    yyxz = property( _getyyxz )
    def _getyyy(self):
        return vec3 ( self.y,self.y,self.y )
    yyy = property( _getyyy )
    def _getyyyw(self):
        return vec4 ( self.y,self.y,self.y,self.w )
    yyyw = property( _getyyyw )
    def _getyyyx(self):
        return vec4 ( self.y,self.y,self.y,self.x )
    yyyx = property( _getyyyx )
    def _getyyyy(self):
        return vec4 ( self.y,self.y,self.y,self.y )
    yyyy = property( _getyyyy )
    def _getyyyz(self):
        return vec4 ( self.y,self.y,self.y,self.z )
    yyyz = property( _getyyyz )
    def _getyyz(self):
        return vec3 ( self.y,self.y,self.z )
    yyz = property( _getyyz )
    def _getyyzw(self):
        return vec4 ( self.y,self.y,self.z,self.w )
    yyzw = property( _getyyzw )
    def _getyyzx(self):
        return vec4 ( self.y,self.y,self.z,self.x )
    yyzx = property( _getyyzx )
    def _getyyzy(self):
        return vec4 ( self.y,self.y,self.z,self.y )
    yyzy = property( _getyyzy )
    def _getyyzz(self):
        return vec4 ( self.y,self.y,self.z,self.z )
    yyzz = property( _getyyzz )
    def _getyz(self):
        return vec2 ( self.y,self.z )
    def _setyz(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.z=v[1]
    yz = property( _getyz , _setyz )
    def _getyzw(self):
        return vec3 ( self.y,self.z,self.w )
    def _setyzw(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.z=v[1]
        self.w=v[2]
    yzw = property( _getyzw , _setyzw )
    def _getyzww(self):
        return vec4 ( self.y,self.z,self.w,self.w )
    yzww = property( _getyzww )
    def _getyzwx(self):
        return vec4 ( self.y,self.z,self.w,self.x )
    def _setyzwx(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.z=v[1]
        self.w=v[2]
        self.x=v[3]
    yzwx = property( _getyzwx , _setyzwx )
    def _getyzwy(self):
        return vec4 ( self.y,self.z,self.w,self.y )
    yzwy = property( _getyzwy )
    def _getyzwz(self):
        return vec4 ( self.y,self.z,self.w,self.z )
    yzwz = property( _getyzwz )
    def _getyzx(self):
        return vec3 ( self.y,self.z,self.x )
    def _setyzx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.z=v[1]
        self.x=v[2]
    yzx = property( _getyzx , _setyzx )
    def _getyzxw(self):
        return vec4 ( self.y,self.z,self.x,self.w )
    def _setyzxw(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.z=v[1]
        self.x=v[2]
        self.w=v[3]
    yzxw = property( _getyzxw , _setyzxw )
    def _getyzxx(self):
        return vec4 ( self.y,self.z,self.x,self.x )
    yzxx = property( _getyzxx )
    def _getyzxy(self):
        return vec4 ( self.y,self.z,self.x,self.y )
    yzxy = property( _getyzxy )
    def _getyzxz(self):
        return vec4 ( self.y,self.z,self.x,self.z )
    yzxz = property( _getyzxz )
    def _getyzy(self):
        return vec3 ( self.y,self.z,self.y )
    yzy = property( _getyzy )
    def _getyzyw(self):
        return vec4 ( self.y,self.z,self.y,self.w )
    yzyw = property( _getyzyw )
    def _getyzyx(self):
        return vec4 ( self.y,self.z,self.y,self.x )
    yzyx = property( _getyzyx )
    def _getyzyy(self):
        return vec4 ( self.y,self.z,self.y,self.y )
    yzyy = property( _getyzyy )
    def _getyzyz(self):
        return vec4 ( self.y,self.z,self.y,self.z )
    yzyz = property( _getyzyz )
    def _getyzz(self):
        return vec3 ( self.y,self.z,self.z )
    yzz = property( _getyzz )
    def _getyzzw(self):
        return vec4 ( self.y,self.z,self.z,self.w )
    yzzw = property( _getyzzw )
    def _getyzzx(self):
        return vec4 ( self.y,self.z,self.z,self.x )
    yzzx = property( _getyzzx )
    def _getyzzy(self):
        return vec4 ( self.y,self.z,self.z,self.y )
    yzzy = property( _getyzzy )
    def _getyzzz(self):
        return vec4 ( self.y,self.z,self.z,self.z )
    yzzz = property( _getyzzz )
    def _getzw(self):
        return vec2 ( self.z,self.w )
    def _setzw(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.w=v[1]
    zw = property( _getzw , _setzw )
    def _getzww(self):
        return vec3 ( self.z,self.w,self.w )
    zww = property( _getzww )
    def _getzwww(self):
        return vec4 ( self.z,self.w,self.w,self.w )
    zwww = property( _getzwww )
    def _getzwwx(self):
        return vec4 ( self.z,self.w,self.w,self.x )
    zwwx = property( _getzwwx )
    def _getzwwy(self):
        return vec4 ( self.z,self.w,self.w,self.y )
    zwwy = property( _getzwwy )
    def _getzwwz(self):
        return vec4 ( self.z,self.w,self.w,self.z )
    zwwz = property( _getzwwz )
    def _getzwx(self):
        return vec3 ( self.z,self.w,self.x )
    def _setzwx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.w=v[1]
        self.x=v[2]
    zwx = property( _getzwx , _setzwx )
    def _getzwxw(self):
        return vec4 ( self.z,self.w,self.x,self.w )
    zwxw = property( _getzwxw )
    def _getzwxx(self):
        return vec4 ( self.z,self.w,self.x,self.x )
    zwxx = property( _getzwxx )
    def _getzwxy(self):
        return vec4 ( self.z,self.w,self.x,self.y )
    def _setzwxy(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.w=v[1]
        self.x=v[2]
        self.y=v[3]
    zwxy = property( _getzwxy , _setzwxy )
    def _getzwxz(self):
        return vec4 ( self.z,self.w,self.x,self.z )
    zwxz = property( _getzwxz )
    def _getzwy(self):
        return vec3 ( self.z,self.w,self.y )
    def _setzwy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.w=v[1]
        self.y=v[2]
    zwy = property( _getzwy , _setzwy )
    def _getzwyw(self):
        return vec4 ( self.z,self.w,self.y,self.w )
    zwyw = property( _getzwyw )
    def _getzwyx(self):
        return vec4 ( self.z,self.w,self.y,self.x )
    def _setzwyx(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.w=v[1]
        self.y=v[2]
        self.x=v[3]
    zwyx = property( _getzwyx , _setzwyx )
    def _getzwyy(self):
        return vec4 ( self.z,self.w,self.y,self.y )
    zwyy = property( _getzwyy )
    def _getzwyz(self):
        return vec4 ( self.z,self.w,self.y,self.z )
    zwyz = property( _getzwyz )
    def _getzwz(self):
        return vec3 ( self.z,self.w,self.z )
    zwz = property( _getzwz )
    def _getzwzw(self):
        return vec4 ( self.z,self.w,self.z,self.w )
    zwzw = property( _getzwzw )
    def _getzwzx(self):
        return vec4 ( self.z,self.w,self.z,self.x )
    zwzx = property( _getzwzx )
    def _getzwzy(self):
        return vec4 ( self.z,self.w,self.z,self.y )
    zwzy = property( _getzwzy )
    def _getzwzz(self):
        return vec4 ( self.z,self.w,self.z,self.z )
    zwzz = property( _getzwzz )
    def _getzx(self):
        return vec2 ( self.z,self.x )
    def _setzx(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.x=v[1]
    zx = property( _getzx , _setzx )
    def _getzxw(self):
        return vec3 ( self.z,self.x,self.w )
    def _setzxw(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.x=v[1]
        self.w=v[2]
    zxw = property( _getzxw , _setzxw )
    def _getzxww(self):
        return vec4 ( self.z,self.x,self.w,self.w )
    zxww = property( _getzxww )
    def _getzxwx(self):
        return vec4 ( self.z,self.x,self.w,self.x )
    zxwx = property( _getzxwx )
    def _getzxwy(self):
        return vec4 ( self.z,self.x,self.w,self.y )
    def _setzxwy(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.x=v[1]
        self.w=v[2]
        self.y=v[3]
    zxwy = property( _getzxwy , _setzxwy )
    def _getzxwz(self):
        return vec4 ( self.z,self.x,self.w,self.z )
    zxwz = property( _getzxwz )
    def _getzxx(self):
        return vec3 ( self.z,self.x,self.x )
    zxx = property( _getzxx )
    def _getzxxw(self):
        return vec4 ( self.z,self.x,self.x,self.w )
    zxxw = property( _getzxxw )
    def _getzxxx(self):
        return vec4 ( self.z,self.x,self.x,self.x )
    zxxx = property( _getzxxx )
    def _getzxxy(self):
        return vec4 ( self.z,self.x,self.x,self.y )
    zxxy = property( _getzxxy )
    def _getzxxz(self):
        return vec4 ( self.z,self.x,self.x,self.z )
    zxxz = property( _getzxxz )
    def _getzxy(self):
        return vec3 ( self.z,self.x,self.y )
    def _setzxy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.x=v[1]
        self.y=v[2]
    zxy = property( _getzxy , _setzxy )
    def _getzxyw(self):
        return vec4 ( self.z,self.x,self.y,self.w )
    def _setzxyw(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.x=v[1]
        self.y=v[2]
        self.w=v[3]
    zxyw = property( _getzxyw , _setzxyw )
    def _getzxyx(self):
        return vec4 ( self.z,self.x,self.y,self.x )
    zxyx = property( _getzxyx )
    def _getzxyy(self):
        return vec4 ( self.z,self.x,self.y,self.y )
    zxyy = property( _getzxyy )
    def _getzxyz(self):
        return vec4 ( self.z,self.x,self.y,self.z )
    zxyz = property( _getzxyz )
    def _getzxz(self):
        return vec3 ( self.z,self.x,self.z )
    zxz = property( _getzxz )
    def _getzxzw(self):
        return vec4 ( self.z,self.x,self.z,self.w )
    zxzw = property( _getzxzw )
    def _getzxzx(self):
        return vec4 ( self.z,self.x,self.z,self.x )
    zxzx = property( _getzxzx )
    def _getzxzy(self):
        return vec4 ( self.z,self.x,self.z,self.y )
    zxzy = property( _getzxzy )
    def _getzxzz(self):
        return vec4 ( self.z,self.x,self.z,self.z )
    zxzz = property( _getzxzz )
    def _getzy(self):
        return vec2 ( self.z,self.y )
    def _setzy(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.y=v[1]
    zy = property( _getzy , _setzy )
    def _getzyw(self):
        return vec3 ( self.z,self.y,self.w )
    def _setzyw(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.y=v[1]
        self.w=v[2]
    zyw = property( _getzyw , _setzyw )
    def _getzyww(self):
        return vec4 ( self.z,self.y,self.w,self.w )
    zyww = property( _getzyww )
    def _getzywx(self):
        return vec4 ( self.z,self.y,self.w,self.x )
    def _setzywx(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.y=v[1]
        self.w=v[2]
        self.x=v[3]
    zywx = property( _getzywx , _setzywx )
    def _getzywy(self):
        return vec4 ( self.z,self.y,self.w,self.y )
    zywy = property( _getzywy )
    def _getzywz(self):
        return vec4 ( self.z,self.y,self.w,self.z )
    zywz = property( _getzywz )
    def _getzyx(self):
        return vec3 ( self.z,self.y,self.x )
    def _setzyx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.y=v[1]
        self.x=v[2]
    zyx = property( _getzyx , _setzyx )
    def _getzyxw(self):
        return vec4 ( self.z,self.y,self.x,self.w )
    def _setzyxw(self,v):
        if type(v) != vec4:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.y=v[1]
        self.x=v[2]
        self.w=v[3]
    zyxw = property( _getzyxw , _setzyxw )
    def _getzyxx(self):
        return vec4 ( self.z,self.y,self.x,self.x )
    zyxx = property( _getzyxx )
    def _getzyxy(self):
        return vec4 ( self.z,self.y,self.x,self.y )
    zyxy = property( _getzyxy )
    def _getzyxz(self):
        return vec4 ( self.z,self.y,self.x,self.z )
    zyxz = property( _getzyxz )
    def _getzyy(self):
        return vec3 ( self.z,self.y,self.y )
    zyy = property( _getzyy )
    def _getzyyw(self):
        return vec4 ( self.z,self.y,self.y,self.w )
    zyyw = property( _getzyyw )
    def _getzyyx(self):
        return vec4 ( self.z,self.y,self.y,self.x )
    zyyx = property( _getzyyx )
    def _getzyyy(self):
        return vec4 ( self.z,self.y,self.y,self.y )
    zyyy = property( _getzyyy )
    def _getzyyz(self):
        return vec4 ( self.z,self.y,self.y,self.z )
    zyyz = property( _getzyyz )
    def _getzyz(self):
        return vec3 ( self.z,self.y,self.z )
    zyz = property( _getzyz )
    def _getzyzw(self):
        return vec4 ( self.z,self.y,self.z,self.w )
    zyzw = property( _getzyzw )
    def _getzyzx(self):
        return vec4 ( self.z,self.y,self.z,self.x )
    zyzx = property( _getzyzx )
    def _getzyzy(self):
        return vec4 ( self.z,self.y,self.z,self.y )
    zyzy = property( _getzyzy )
    def _getzyzz(self):
        return vec4 ( self.z,self.y,self.z,self.z )
    zyzz = property( _getzyzz )
    def _getzz(self):
        return vec2 ( self.z,self.z )
    zz = property( _getzz )
    def _getzzw(self):
        return vec3 ( self.z,self.z,self.w )
    zzw = property( _getzzw )
    def _getzzww(self):
        return vec4 ( self.z,self.z,self.w,self.w )
    zzww = property( _getzzww )
    def _getzzwx(self):
        return vec4 ( self.z,self.z,self.w,self.x )
    zzwx = property( _getzzwx )
    def _getzzwy(self):
        return vec4 ( self.z,self.z,self.w,self.y )
    zzwy = property( _getzzwy )
    def _getzzwz(self):
        return vec4 ( self.z,self.z,self.w,self.z )
    zzwz = property( _getzzwz )
    def _getzzx(self):
        return vec3 ( self.z,self.z,self.x )
    zzx = property( _getzzx )
    def _getzzxw(self):
        return vec4 ( self.z,self.z,self.x,self.w )
    zzxw = property( _getzzxw )
    def _getzzxx(self):
        return vec4 ( self.z,self.z,self.x,self.x )
    zzxx = property( _getzzxx )
    def _getzzxy(self):
        return vec4 ( self.z,self.z,self.x,self.y )
    zzxy = property( _getzzxy )
    def _getzzxz(self):
        return vec4 ( self.z,self.z,self.x,self.z )
    zzxz = property( _getzzxz )
    def _getzzy(self):
        return vec3 ( self.z,self.z,self.y )
    zzy = property( _getzzy )
    def _getzzyw(self):
        return vec4 ( self.z,self.z,self.y,self.w )
    zzyw = property( _getzzyw )
    def _getzzyx(self):
        return vec4 ( self.z,self.z,self.y,self.x )
    zzyx = property( _getzzyx )
    def _getzzyy(self):
        return vec4 ( self.z,self.z,self.y,self.y )
    zzyy = property( _getzzyy )
    def _getzzyz(self):
        return vec4 ( self.z,self.z,self.y,self.z )
    zzyz = property( _getzzyz )
    def _getzzz(self):
        return vec3 ( self.z,self.z,self.z )
    zzz = property( _getzzz )
    def _getzzzw(self):
        return vec4 ( self.z,self.z,self.z,self.w )
    zzzw = property( _getzzzw )
    def _getzzzx(self):
        return vec4 ( self.z,self.z,self.z,self.x )
    zzzx = property( _getzzzx )
    def _getzzzy(self):
        return vec4 ( self.z,self.z,self.z,self.y )
    zzzy = property( _getzzzy )
    def _getzzzz(self):
        return vec4 ( self.z,self.z,self.z,self.z )
    zzzz = property( _getzzzz )
class vec3:
    def __init__(self,*args):
        if len(args)==0:
            args=[ 0,0,0 ]

        L=[]
        for a in args:
            if type(a) == float:
                L.append(a)
            elif type(a) == int:
                L.append(a)
            elif type(a) == vec2:
                L += (a.x,a,y)
            elif type(a) == vec3:
                L += (a.x,a.y,a.z)
            elif type(a) == vec4:
                L += (a.x,a.y,a.z,a.w)
            elif type(a) == list or type(a) == tuple:
                L += a
            elif type(a) == array.array:
                L += [q for q in a]
            else:
                raise RuntimeError("Bad argument to vec constructor: "+str(type(a)))
        if len(L) == 1:
            L=L[0]*3
        if len(L) != 3:
            raise RuntimeError("Bad number of items to vec constructor")
        self._v = array.array("f",L)

    def tobytes(self):
        return self._v.tobytes()
        
    def __getitem__(self,key):
        return self._v[key]
            
    def __setitem__(self,key,value):
        self._v[key]=value
        
    def __str__(self):
        return "vec3(" + ",".join([str(q) for q in self._v])+")"
        
    def __repr__(self):
        return str(self)
      
    def __len__(self):
        return 3
        
    def __add__(self,o):
        if not type(o) == type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._v)):
            L.append( self._v[i]+o._v[i])
        return vec3(L)
        
    def __sub__(self,o):
        if type(o) != type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._v)):
            L.append( self._v[i]-o._v[i])
        return vec3(L)
        
    def __mul__(self,o):
        if type(o) == type(self):
            R=vec3()
            for i in range(3):
                R[i] = self[i]*o[i]
            return R
        elif type(o) == mat3:
            R=vec3()
            
            for i in range(3):
                total=0
                for j in range(3):
                    total += self[j]*o[j][i]
                R[i]=total
            return R
        elif type(o) == float or type(o) == int:
            R=vec3( [q*o for q in self._v] )
            return R
        else:
            return NotImplemented
    
    def __rmul__(self,o):
        # o * self
        if type(o) == type(self):
            assert 0        #should never happen
        elif type(o) == mat3:
            assert 0        #should not happen
        elif type(o) == float or type(o) == int:
            R=vec3( [q*o for q in self._v] )
            return R
        else:
            return NotImplemented
    
    def __neg__(self):
        return vec3( [-q for q in self._v] )

    def __pos__(self):
        return vec3( [q for q in self._v] )
     
    def __iter__(self):
        return self._v.__iter__()
      
    def __eq__(self,o):
        if type(o) != type(self):
            return False
        for i in range(3):
            if self._v[i] != o._v[i]:
                return False
        return True
        
    def __ne__(self,o):
        return not self==o
        
        
        
    
    def _getx(self):
        return self._v[ 0 ]
    def _setx(self,v):
        self._v[ 0 ]=v
    x = property(_getx , _setx )
    def _gety(self):
        return self._v[ 1 ]
    def _sety(self,v):
        self._v[ 1 ]=v
    y = property(_gety , _sety )
    def _getz(self):
        return self._v[ 2 ]
    def _setz(self,v):
        self._v[ 2 ]=v
    z = property(_getz , _setz )
    def _getxx(self):
        return vec2 ( self.x,self.x )
    xx = property( _getxx )
    def _getxxx(self):
        return vec3 ( self.x,self.x,self.x )
    xxx = property( _getxxx )
    def _getxxxx(self):
        return vec4 ( self.x,self.x,self.x,self.x )
    xxxx = property( _getxxxx )
    def _getxxxy(self):
        return vec4 ( self.x,self.x,self.x,self.y )
    xxxy = property( _getxxxy )
    def _getxxxz(self):
        return vec4 ( self.x,self.x,self.x,self.z )
    xxxz = property( _getxxxz )
    def _getxxy(self):
        return vec3 ( self.x,self.x,self.y )
    xxy = property( _getxxy )
    def _getxxyx(self):
        return vec4 ( self.x,self.x,self.y,self.x )
    xxyx = property( _getxxyx )
    def _getxxyy(self):
        return vec4 ( self.x,self.x,self.y,self.y )
    xxyy = property( _getxxyy )
    def _getxxyz(self):
        return vec4 ( self.x,self.x,self.y,self.z )
    xxyz = property( _getxxyz )
    def _getxxz(self):
        return vec3 ( self.x,self.x,self.z )
    xxz = property( _getxxz )
    def _getxxzx(self):
        return vec4 ( self.x,self.x,self.z,self.x )
    xxzx = property( _getxxzx )
    def _getxxzy(self):
        return vec4 ( self.x,self.x,self.z,self.y )
    xxzy = property( _getxxzy )
    def _getxxzz(self):
        return vec4 ( self.x,self.x,self.z,self.z )
    xxzz = property( _getxxzz )
    def _getxy(self):
        return vec2 ( self.x,self.y )
    def _setxy(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
    xy = property( _getxy , _setxy )
    def _getxyx(self):
        return vec3 ( self.x,self.y,self.x )
    xyx = property( _getxyx )
    def _getxyxx(self):
        return vec4 ( self.x,self.y,self.x,self.x )
    xyxx = property( _getxyxx )
    def _getxyxy(self):
        return vec4 ( self.x,self.y,self.x,self.y )
    xyxy = property( _getxyxy )
    def _getxyxz(self):
        return vec4 ( self.x,self.y,self.x,self.z )
    xyxz = property( _getxyxz )
    def _getxyy(self):
        return vec3 ( self.x,self.y,self.y )
    xyy = property( _getxyy )
    def _getxyyx(self):
        return vec4 ( self.x,self.y,self.y,self.x )
    xyyx = property( _getxyyx )
    def _getxyyy(self):
        return vec4 ( self.x,self.y,self.y,self.y )
    xyyy = property( _getxyyy )
    def _getxyyz(self):
        return vec4 ( self.x,self.y,self.y,self.z )
    xyyz = property( _getxyyz )
    def _getxyz(self):
        return vec3 ( self.x,self.y,self.z )
    def _setxyz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
        self.z=v[2]
    xyz = property( _getxyz , _setxyz )
    def _getxyzx(self):
        return vec4 ( self.x,self.y,self.z,self.x )
    xyzx = property( _getxyzx )
    def _getxyzy(self):
        return vec4 ( self.x,self.y,self.z,self.y )
    xyzy = property( _getxyzy )
    def _getxyzz(self):
        return vec4 ( self.x,self.y,self.z,self.z )
    xyzz = property( _getxyzz )
    def _getxz(self):
        return vec2 ( self.x,self.z )
    def _setxz(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.z=v[1]
    xz = property( _getxz , _setxz )
    def _getxzx(self):
        return vec3 ( self.x,self.z,self.x )
    xzx = property( _getxzx )
    def _getxzxx(self):
        return vec4 ( self.x,self.z,self.x,self.x )
    xzxx = property( _getxzxx )
    def _getxzxy(self):
        return vec4 ( self.x,self.z,self.x,self.y )
    xzxy = property( _getxzxy )
    def _getxzxz(self):
        return vec4 ( self.x,self.z,self.x,self.z )
    xzxz = property( _getxzxz )
    def _getxzy(self):
        return vec3 ( self.x,self.z,self.y )
    def _setxzy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.z=v[1]
        self.y=v[2]
    xzy = property( _getxzy , _setxzy )
    def _getxzyx(self):
        return vec4 ( self.x,self.z,self.y,self.x )
    xzyx = property( _getxzyx )
    def _getxzyy(self):
        return vec4 ( self.x,self.z,self.y,self.y )
    xzyy = property( _getxzyy )
    def _getxzyz(self):
        return vec4 ( self.x,self.z,self.y,self.z )
    xzyz = property( _getxzyz )
    def _getxzz(self):
        return vec3 ( self.x,self.z,self.z )
    xzz = property( _getxzz )
    def _getxzzx(self):
        return vec4 ( self.x,self.z,self.z,self.x )
    xzzx = property( _getxzzx )
    def _getxzzy(self):
        return vec4 ( self.x,self.z,self.z,self.y )
    xzzy = property( _getxzzy )
    def _getxzzz(self):
        return vec4 ( self.x,self.z,self.z,self.z )
    xzzz = property( _getxzzz )
    def _getyx(self):
        return vec2 ( self.y,self.x )
    def _setyx(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
    yx = property( _getyx , _setyx )
    def _getyxx(self):
        return vec3 ( self.y,self.x,self.x )
    yxx = property( _getyxx )
    def _getyxxx(self):
        return vec4 ( self.y,self.x,self.x,self.x )
    yxxx = property( _getyxxx )
    def _getyxxy(self):
        return vec4 ( self.y,self.x,self.x,self.y )
    yxxy = property( _getyxxy )
    def _getyxxz(self):
        return vec4 ( self.y,self.x,self.x,self.z )
    yxxz = property( _getyxxz )
    def _getyxy(self):
        return vec3 ( self.y,self.x,self.y )
    yxy = property( _getyxy )
    def _getyxyx(self):
        return vec4 ( self.y,self.x,self.y,self.x )
    yxyx = property( _getyxyx )
    def _getyxyy(self):
        return vec4 ( self.y,self.x,self.y,self.y )
    yxyy = property( _getyxyy )
    def _getyxyz(self):
        return vec4 ( self.y,self.x,self.y,self.z )
    yxyz = property( _getyxyz )
    def _getyxz(self):
        return vec3 ( self.y,self.x,self.z )
    def _setyxz(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
        self.z=v[2]
    yxz = property( _getyxz , _setyxz )
    def _getyxzx(self):
        return vec4 ( self.y,self.x,self.z,self.x )
    yxzx = property( _getyxzx )
    def _getyxzy(self):
        return vec4 ( self.y,self.x,self.z,self.y )
    yxzy = property( _getyxzy )
    def _getyxzz(self):
        return vec4 ( self.y,self.x,self.z,self.z )
    yxzz = property( _getyxzz )
    def _getyy(self):
        return vec2 ( self.y,self.y )
    yy = property( _getyy )
    def _getyyx(self):
        return vec3 ( self.y,self.y,self.x )
    yyx = property( _getyyx )
    def _getyyxx(self):
        return vec4 ( self.y,self.y,self.x,self.x )
    yyxx = property( _getyyxx )
    def _getyyxy(self):
        return vec4 ( self.y,self.y,self.x,self.y )
    yyxy = property( _getyyxy )
    def _getyyxz(self):
        return vec4 ( self.y,self.y,self.x,self.z )
    yyxz = property( _getyyxz )
    def _getyyy(self):
        return vec3 ( self.y,self.y,self.y )
    yyy = property( _getyyy )
    def _getyyyx(self):
        return vec4 ( self.y,self.y,self.y,self.x )
    yyyx = property( _getyyyx )
    def _getyyyy(self):
        return vec4 ( self.y,self.y,self.y,self.y )
    yyyy = property( _getyyyy )
    def _getyyyz(self):
        return vec4 ( self.y,self.y,self.y,self.z )
    yyyz = property( _getyyyz )
    def _getyyz(self):
        return vec3 ( self.y,self.y,self.z )
    yyz = property( _getyyz )
    def _getyyzx(self):
        return vec4 ( self.y,self.y,self.z,self.x )
    yyzx = property( _getyyzx )
    def _getyyzy(self):
        return vec4 ( self.y,self.y,self.z,self.y )
    yyzy = property( _getyyzy )
    def _getyyzz(self):
        return vec4 ( self.y,self.y,self.z,self.z )
    yyzz = property( _getyyzz )
    def _getyz(self):
        return vec2 ( self.y,self.z )
    def _setyz(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.z=v[1]
    yz = property( _getyz , _setyz )
    def _getyzx(self):
        return vec3 ( self.y,self.z,self.x )
    def _setyzx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.z=v[1]
        self.x=v[2]
    yzx = property( _getyzx , _setyzx )
    def _getyzxx(self):
        return vec4 ( self.y,self.z,self.x,self.x )
    yzxx = property( _getyzxx )
    def _getyzxy(self):
        return vec4 ( self.y,self.z,self.x,self.y )
    yzxy = property( _getyzxy )
    def _getyzxz(self):
        return vec4 ( self.y,self.z,self.x,self.z )
    yzxz = property( _getyzxz )
    def _getyzy(self):
        return vec3 ( self.y,self.z,self.y )
    yzy = property( _getyzy )
    def _getyzyx(self):
        return vec4 ( self.y,self.z,self.y,self.x )
    yzyx = property( _getyzyx )
    def _getyzyy(self):
        return vec4 ( self.y,self.z,self.y,self.y )
    yzyy = property( _getyzyy )
    def _getyzyz(self):
        return vec4 ( self.y,self.z,self.y,self.z )
    yzyz = property( _getyzyz )
    def _getyzz(self):
        return vec3 ( self.y,self.z,self.z )
    yzz = property( _getyzz )
    def _getyzzx(self):
        return vec4 ( self.y,self.z,self.z,self.x )
    yzzx = property( _getyzzx )
    def _getyzzy(self):
        return vec4 ( self.y,self.z,self.z,self.y )
    yzzy = property( _getyzzy )
    def _getyzzz(self):
        return vec4 ( self.y,self.z,self.z,self.z )
    yzzz = property( _getyzzz )
    def _getzx(self):
        return vec2 ( self.z,self.x )
    def _setzx(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.x=v[1]
    zx = property( _getzx , _setzx )
    def _getzxx(self):
        return vec3 ( self.z,self.x,self.x )
    zxx = property( _getzxx )
    def _getzxxx(self):
        return vec4 ( self.z,self.x,self.x,self.x )
    zxxx = property( _getzxxx )
    def _getzxxy(self):
        return vec4 ( self.z,self.x,self.x,self.y )
    zxxy = property( _getzxxy )
    def _getzxxz(self):
        return vec4 ( self.z,self.x,self.x,self.z )
    zxxz = property( _getzxxz )
    def _getzxy(self):
        return vec3 ( self.z,self.x,self.y )
    def _setzxy(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.x=v[1]
        self.y=v[2]
    zxy = property( _getzxy , _setzxy )
    def _getzxyx(self):
        return vec4 ( self.z,self.x,self.y,self.x )
    zxyx = property( _getzxyx )
    def _getzxyy(self):
        return vec4 ( self.z,self.x,self.y,self.y )
    zxyy = property( _getzxyy )
    def _getzxyz(self):
        return vec4 ( self.z,self.x,self.y,self.z )
    zxyz = property( _getzxyz )
    def _getzxz(self):
        return vec3 ( self.z,self.x,self.z )
    zxz = property( _getzxz )
    def _getzxzx(self):
        return vec4 ( self.z,self.x,self.z,self.x )
    zxzx = property( _getzxzx )
    def _getzxzy(self):
        return vec4 ( self.z,self.x,self.z,self.y )
    zxzy = property( _getzxzy )
    def _getzxzz(self):
        return vec4 ( self.z,self.x,self.z,self.z )
    zxzz = property( _getzxzz )
    def _getzy(self):
        return vec2 ( self.z,self.y )
    def _setzy(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.y=v[1]
    zy = property( _getzy , _setzy )
    def _getzyx(self):
        return vec3 ( self.z,self.y,self.x )
    def _setzyx(self,v):
        if type(v) != vec3:
            raise RuntimeError('Bad argument type')
        self.z=v[0]
        self.y=v[1]
        self.x=v[2]
    zyx = property( _getzyx , _setzyx )
    def _getzyxx(self):
        return vec4 ( self.z,self.y,self.x,self.x )
    zyxx = property( _getzyxx )
    def _getzyxy(self):
        return vec4 ( self.z,self.y,self.x,self.y )
    zyxy = property( _getzyxy )
    def _getzyxz(self):
        return vec4 ( self.z,self.y,self.x,self.z )
    zyxz = property( _getzyxz )
    def _getzyy(self):
        return vec3 ( self.z,self.y,self.y )
    zyy = property( _getzyy )
    def _getzyyx(self):
        return vec4 ( self.z,self.y,self.y,self.x )
    zyyx = property( _getzyyx )
    def _getzyyy(self):
        return vec4 ( self.z,self.y,self.y,self.y )
    zyyy = property( _getzyyy )
    def _getzyyz(self):
        return vec4 ( self.z,self.y,self.y,self.z )
    zyyz = property( _getzyyz )
    def _getzyz(self):
        return vec3 ( self.z,self.y,self.z )
    zyz = property( _getzyz )
    def _getzyzx(self):
        return vec4 ( self.z,self.y,self.z,self.x )
    zyzx = property( _getzyzx )
    def _getzyzy(self):
        return vec4 ( self.z,self.y,self.z,self.y )
    zyzy = property( _getzyzy )
    def _getzyzz(self):
        return vec4 ( self.z,self.y,self.z,self.z )
    zyzz = property( _getzyzz )
    def _getzz(self):
        return vec2 ( self.z,self.z )
    zz = property( _getzz )
    def _getzzx(self):
        return vec3 ( self.z,self.z,self.x )
    zzx = property( _getzzx )
    def _getzzxx(self):
        return vec4 ( self.z,self.z,self.x,self.x )
    zzxx = property( _getzzxx )
    def _getzzxy(self):
        return vec4 ( self.z,self.z,self.x,self.y )
    zzxy = property( _getzzxy )
    def _getzzxz(self):
        return vec4 ( self.z,self.z,self.x,self.z )
    zzxz = property( _getzzxz )
    def _getzzy(self):
        return vec3 ( self.z,self.z,self.y )
    zzy = property( _getzzy )
    def _getzzyx(self):
        return vec4 ( self.z,self.z,self.y,self.x )
    zzyx = property( _getzzyx )
    def _getzzyy(self):
        return vec4 ( self.z,self.z,self.y,self.y )
    zzyy = property( _getzzyy )
    def _getzzyz(self):
        return vec4 ( self.z,self.z,self.y,self.z )
    zzyz = property( _getzzyz )
    def _getzzz(self):
        return vec3 ( self.z,self.z,self.z )
    zzz = property( _getzzz )
    def _getzzzx(self):
        return vec4 ( self.z,self.z,self.z,self.x )
    zzzx = property( _getzzzx )
    def _getzzzy(self):
        return vec4 ( self.z,self.z,self.z,self.y )
    zzzy = property( _getzzzy )
    def _getzzzz(self):
        return vec4 ( self.z,self.z,self.z,self.z )
    zzzz = property( _getzzzz )
class vec2:
    def __init__(self,*args):
        if len(args)==0:
            args=[ 0,0 ]

        L=[]
        for a in args:
            if type(a) == float:
                L.append(a)
            elif type(a) == int:
                L.append(a)
            elif type(a) == vec2:
                L += (a.x,a,y)
            elif type(a) == vec3:
                L += (a.x,a.y,a.z)
            elif type(a) == vec4:
                L += (a.x,a.y,a.z,a.w)
            elif type(a) == list or type(a) == tuple:
                L += a
            elif type(a) == array.array:
                L += [q for q in a]
            else:
                raise RuntimeError("Bad argument to vec constructor: "+str(type(a)))
        if len(L) == 1:
            L=L[0]*2
        if len(L) != 2:
            raise RuntimeError("Bad number of items to vec constructor")
        self._v = array.array("f",L)

    def tobytes(self):
        return self._v.tobytes()
        
    def __getitem__(self,key):
        return self._v[key]
            
    def __setitem__(self,key,value):
        self._v[key]=value
        
    def __str__(self):
        return "vec2(" + ",".join([str(q) for q in self._v])+")"
        
    def __repr__(self):
        return str(self)
      
    def __len__(self):
        return 2
        
    def __add__(self,o):
        if not type(o) == type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._v)):
            L.append( self._v[i]+o._v[i])
        return vec2(L)
        
    def __sub__(self,o):
        if type(o) != type(self):
            return NotImplemented
        L=[]
        for i in range(len(self._v)):
            L.append( self._v[i]-o._v[i])
        return vec2(L)

    def __truediv__(self, o):
        if type(o) == type(self):
            R=vec2()
            for i in range(2):
                R[i] = self[i]/o[i]
            return R
        
    def __mul__(self,o):
        if type(o) == type(self):
            R=vec2()
            for i in range(2):
                R[i] = self[i]*o[i]
            return R
        elif type(o) == mat2:
            R=vec2()
            
            for i in range(2):
                total=0
                for j in range(2):
                    total += self[j]*o[j][i]
                R[i]=total
            return R
        elif type(o) == float or type(o) == int:
            R=vec2( [q*o for q in self._v] )
            return R
        else:
            return NotImplemented
    
    def __rmul__(self,o):
        # o * self
        if type(o) == type(self):
            assert 0        #should never happen
        elif type(o) == mat2:
            assert 0        #should not happen
        elif type(o) == float or type(o) == int:
            R=vec2( [q*o for q in self._v] )
            return R
        else:
            return NotImplemented
    
    def __neg__(self):
        return vec2( [-q for q in self._v] )

    def __pos__(self):
        return vec2( [q for q in self._v] )
     
    def __iter__(self):
        return self._v.__iter__()
      
    def __eq__(self,o):
        if type(o) != type(self):
            return False
        for i in range(2):
            if self._v[i] != o._v[i]:
                return False
        return True
        
    def __ne__(self,o):
        return not self==o
        
        
        
    
    def _getx(self):
        return self._v[ 0 ]
    def _setx(self,v):
        self._v[ 0 ]=v
    x = property(_getx , _setx )
    def _gety(self):
        return self._v[ 1 ]
    def _sety(self,v):
        self._v[ 1 ]=v
    y = property(_gety , _sety )
    def _getxx(self):
        return vec2 ( self.x,self.x )
    xx = property( _getxx )
    def _getxxx(self):
        return vec3 ( self.x,self.x,self.x )
    xxx = property( _getxxx )
    def _getxxxx(self):
        return vec4 ( self.x,self.x,self.x,self.x )
    xxxx = property( _getxxxx )
    def _getxxxy(self):
        return vec4 ( self.x,self.x,self.x,self.y )
    xxxy = property( _getxxxy )
    def _getxxy(self):
        return vec3 ( self.x,self.x,self.y )
    xxy = property( _getxxy )
    def _getxxyx(self):
        return vec4 ( self.x,self.x,self.y,self.x )
    xxyx = property( _getxxyx )
    def _getxxyy(self):
        return vec4 ( self.x,self.x,self.y,self.y )
    xxyy = property( _getxxyy )
    def _getxy(self):
        return vec2 ( self.x,self.y )
    def _setxy(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.x=v[0]
        self.y=v[1]
    xy = property( _getxy , _setxy )
    def _getxyx(self):
        return vec3 ( self.x,self.y,self.x )
    xyx = property( _getxyx )
    def _getxyxx(self):
        return vec4 ( self.x,self.y,self.x,self.x )
    xyxx = property( _getxyxx )
    def _getxyxy(self):
        return vec4 ( self.x,self.y,self.x,self.y )
    xyxy = property( _getxyxy )
    def _getxyy(self):
        return vec3 ( self.x,self.y,self.y )
    xyy = property( _getxyy )
    def _getxyyx(self):
        return vec4 ( self.x,self.y,self.y,self.x )
    xyyx = property( _getxyyx )
    def _getxyyy(self):
        return vec4 ( self.x,self.y,self.y,self.y )
    xyyy = property( _getxyyy )
    def _getyx(self):
        return vec2 ( self.y,self.x )
    def _setyx(self,v):
        if type(v) != vec2:
            raise RuntimeError('Bad argument type')
        self.y=v[0]
        self.x=v[1]
    yx = property( _getyx , _setyx )
    def _getyxx(self):
        return vec3 ( self.y,self.x,self.x )
    yxx = property( _getyxx )
    def _getyxxx(self):
        return vec4 ( self.y,self.x,self.x,self.x )
    yxxx = property( _getyxxx )
    def _getyxxy(self):
        return vec4 ( self.y,self.x,self.x,self.y )
    yxxy = property( _getyxxy )
    def _getyxy(self):
        return vec3 ( self.y,self.x,self.y )
    yxy = property( _getyxy )
    def _getyxyx(self):
        return vec4 ( self.y,self.x,self.y,self.x )
    yxyx = property( _getyxyx )
    def _getyxyy(self):
        return vec4 ( self.y,self.x,self.y,self.y )
    yxyy = property( _getyxyy )
    def _getyy(self):
        return vec2 ( self.y,self.y )
    yy = property( _getyy )
    def _getyyx(self):
        return vec3 ( self.y,self.y,self.x )
    yyx = property( _getyyx )
    def _getyyxx(self):
        return vec4 ( self.y,self.y,self.x,self.x )
    yyxx = property( _getyyxx )
    def _getyyxy(self):
        return vec4 ( self.y,self.y,self.x,self.y )
    yyxy = property( _getyyxy )
    def _getyyy(self):
        return vec3 ( self.y,self.y,self.y )
    yyy = property( _getyyy )
    def _getyyyx(self):
        return vec4 ( self.y,self.y,self.y,self.x )
    yyyx = property( _getyyyx )
    def _getyyyy(self):
        return vec4 ( self.y,self.y,self.y,self.y )
    yyyy = property( _getyyyy )

def dot(v,w):
    assert type(v) == type(w)
    assert type(v) == vec2 or type(v) == vec3 or type(v) == vec4
    return sum( [v[i]*w[i] for i in range(len(v)) ] )
    
def cross(v,w):
    assert type(v) == type(w)
    assert type(v) == vec3 or type(v) == vec4
    if type(v) == vec3:
        return vec3(
            v.y*w.z - w.y*v.z,
            w.x*v.z - v.x*w.z,
            v.x*w.y - w.x*v.y
        )
    else:
        assert v[3]==0 and w[3]==0
        return vec4(
            v.y*w.z - w.y*v.z,
            w.x*v.z - v.x*w.z,
            v.x*w.y - w.x*v.y,
            0
        )

def length(v):
    assert type(v) in (vec4,vec3,vec2)
    return dot(v,v)**0.5
    
def normalize(v):
    assert type(v) in (vec4,vec3,vec2)
    le=length(v)
    return 1.0/le * v

def transpose(m):
    return m.transpose()
    
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
        tmp_0 = m[2][2] * m[3][3]
        tmp_1 = m[3][2] * m[2][3]
        tmp_2 = m[1][2] * m[3][3]
        tmp_3 = m[3][2] * m[1][3]
        tmp_4 = m[1][2] * m[2][3]
        tmp_5 = m[2][2] * m[1][3]
        tmp_6 = m[0][2] * m[3][3]
        tmp_7 = m[3][2] * m[0][3]
        tmp_8 = m[0][2] * m[2][3]
        tmp_9 = m[2][2] * m[0][3]
        tmp_10 = m[0][2] * m[1][3]
        tmp_11 = m[1][2] * m[0][3]
        tmp_12 = m[2][0] * m[3][1]
        tmp_13 = m[3][0] * m[2][1]
        tmp_14 = m[1][0] * m[3][1]
        tmp_15 = m[3][0] * m[1][1]
        tmp_16 = m[1][0] * m[2][1]
        tmp_17 = m[2][0] * m[1][1]
        tmp_18 = m[0][0] * m[3][1]
        tmp_19 = m[3][0] * m[0][1]
        tmp_20 = m[0][0] * m[2][1]
        tmp_21 = m[2][0] * m[0][1]
        tmp_22 = m[0][0] * m[1][1]
        tmp_23 = m[1][0] * m[0][1]

        t0 = (tmp_0 * m[1][1] + tmp_3 * m[2][1] + tmp_4 * m[3][1]) -        (tmp_1 * m[1][1] + tmp_2 * m[2][1] + tmp_5 * m[3][1])
        t1 = (tmp_1 * m[0][1] + tmp_6 * m[2][1] + tmp_9 * m[3][1]) -        (tmp_0 * m[0][1] + tmp_7 * m[2][1] + tmp_8 * m[3][1])
        t2 = (tmp_2 * m[0][1] + tmp_7 * m[1][1] + tmp_10 * m[3][1]) -        (tmp_3 * m[0][1] + tmp_6 * m[1][1] + tmp_11 * m[3][1])
        t3 = (tmp_5 * m[0][1] + tmp_8 * m[1][1] + tmp_11 * m[2][1]) -        (tmp_4 * m[0][1] + tmp_9 * m[1][1] + tmp_10 * m[2][1])
        d = 1.0 / (m[0][0] * t0 + m[1][0] * t1 + m[2][0] * t2 + m[3][0] * t3)

        return mat4(d * t0, d * t1, d * t2, d * t3,
           d * ((tmp_1 * m[1][0] + tmp_2 * m[2][0] + tmp_5 * m[3][0]) -
              (tmp_0 * m[1][0] + tmp_3 * m[2][0] + tmp_4 * m[3][0])),
           d * ((tmp_0 * m[0][0] + tmp_7 * m[2][0] + tmp_8 * m[3][0]) -
              (tmp_1 * m[0][0] + tmp_6 * m[2][0] + tmp_9 * m[3][0])),
           d * ((tmp_3 * m[0][0] + tmp_6 * m[1][0] + tmp_11 * m[3][0]) -
              (tmp_2 * m[0][0] + tmp_7 * m[1][0] + tmp_10 * m[3][0])),
           d * ((tmp_4 * m[0][0] + tmp_9 * m[1][0] + tmp_10 * m[2][0]) -
              (tmp_5 * m[0][0] + tmp_8 * m[1][0] + tmp_11 * m[2][0])),
           d * ((tmp_12 * m[1][3] + tmp_15 * m[2][3] + tmp_16 * m[3][3]) -
              (tmp_13 * m[1][3] + tmp_14 * m[2][3] + tmp_17 * m[3][3])),
           d * ((tmp_13 * m[0][3] + tmp_18 * m[2][3] + tmp_21 * m[3][3]) -
              (tmp_12 * m[0][3] + tmp_19 * m[2][3] + tmp_20 * m[3][3])),
           d * ((tmp_14 * m[0][3] + tmp_19 * m[1][3] + tmp_22 * m[3][3]) -
              (tmp_15 * m[0][3] + tmp_18 * m[1][3] + tmp_23 * m[3][3])),
           d * ((tmp_17 * m[0][3] + tmp_20 * m[1][3] + tmp_23 * m[2][3]) -
              (tmp_16 * m[0][3] + tmp_21 * m[1][3] + tmp_22 * m[2][3])),
           d * ((tmp_14 * m[2][2] + tmp_17 * m[3][2] + tmp_13 * m[1][2]) -
              (tmp_16 * m[3][2] + tmp_12 * m[1][2] + tmp_15 * m[2][2])),
           d * ((tmp_20 * m[3][2] + tmp_12 * m[0][2] + tmp_19 * m[2][2]) -
              (tmp_18 * m[2][2] + tmp_21 * m[3][2] + tmp_13 * m[0][2])),
           d * ((tmp_18 * m[1][2] + tmp_23 * m[3][2] + tmp_15 * m[0][2]) -
              (tmp_22 * m[3][2] + tmp_14 * m[0][2] + tmp_19 * m[1][2])),
           d * ((tmp_22 * m[2][2] + tmp_16 * m[0][2] + tmp_21 * m[1][2]) -
              (tmp_20 * m[1][2] + tmp_23 * m[2][2] + tmp_17 * m[0][2])))

#from TDL
def axisRotation(axis,angle):
    axis=normalize(axis)
    x = axis[0]
    y = axis[1]
    z = axis[2]
    xx = x * x
    yy = y * y
    zz = z * z
    c = math.cos(angle)
    s = math.sin(angle)
    oneMinusCosine = 1 - c
    return mat4(
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
    )
    
#from TDL
def scaling(v):
    return mat4( 
        v[0], 0,0,0,
        0,v[1],0,0,
        0,0,v[2],0,
        0,0,0,1)

#from TDL
def translation(v):
    return mat4(
        1,0,0,0,
        0,1,0,0,
        0,0,1,0,
        v[0],v[1],v[2],1)
        


class float4(vec4):
    pass
    
class float3(vec3):
    pass
    
class float2(vec2):
    pass
    
class float4x4(mat4):
    pass
    
class float3x3(mat3):
    pass

class float2x2(mat2):
    pass
    

def mul(a,b):
    return a*b
    
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


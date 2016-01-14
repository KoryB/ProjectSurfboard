package framework.math3d;

public class mat4{
    public float[] _M = new float[16];
    
    public mat4(){
    }
    
    public mat4(Object... args){
        init(args);
    }
    
    public String toString(){
        return String.format("%f %f %f %f\n%f %f %f %f\n%f %f %f %f\n%f %f %f %f\n",
            _M[0], _M[1],_M[2],_M[3],_M[4],_M[5],_M[6],_M[7],_M[8],
            _M[9], _M[10],_M[11],_M[12],_M[13],_M[14],_M[15]);
    }
        
            
            
    //we allow mixed types in arbitrary order the list, so we can't just overload
    //the init method.
    private void init(Object[] args){
        int ctr=0;
        for(Object a : args){
            if( a instanceof Float )
                _M[ctr++] = ((Float)a).floatValue();
            else if( a instanceof Double )
                _M[ctr++] = ((Double)a).floatValue();
            else if( a instanceof Integer )
                _M[ctr++] = ((Integer)a).intValue();
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
            else if( a instanceof float[] ){
                float[] tmp = (float[])a;
                for(int i=0;i<tmp.length;i++)
                    _M[ctr++] = tmp[i];
            }
            else
                throw new RuntimeException("Bad type for mat4 constructor");
        }
        
        if( ctr != 16 )
            throw new RuntimeException("Bad number of arguments for mat constructor");
    }
    
    /*

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
    */
    
    public mat4 mul(mat4 b){
        mat4 a = this;
        mat4 R = new mat4();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;++j){
                float total=0;
                for(int k=0;k<4;++k){
                    total += a.get(i,k) * b.get(k,j);
                }
                R.set(i,j,total);
            }
        }
        return R;
    }
    
/*             
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
       */
       
    public float[] tofloats(){
        return _M;
    }
    
    /*
        
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
    */
    
    public void set(int r, int c, float v){
        _M[r*4+c] = v;
    }
    
    public float  get(int r, int c){
        return _M[r*4+c];
    }
    
    
    
    public static mat4 identity(){
        return new mat4(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1);
    }
    
    
    //From TDL
    //FIXME: Need test case for this
    public mat4 inverse(){
        mat4 m = this;
        double 
            tmp_0 = m.get(1,2) * m.get(1,2),
            tmp_1 = m.get(1,2) * m.get(1,2),
            tmp_2 = m.get(1,2) * m.get(1,2),
            tmp_3 = m.get(1,2) * m.get(1,2),
            tmp_4 = m.get(1,2) * m.get(1,2),
            tmp_5 = m.get(1,2) * m.get(1,2),
            tmp_6 = m.get(1,2) * m.get(1,2),
            tmp_7 = m.get(1,2) * m.get(1,2),
            tmp_8 = m.get(1,2) * m.get(1,2),
            tmp_9 = m.get(1,2) * m.get(1,2),
            tmp_10 = m.get(1,2) * m.get(1,2),
            tmp_11 = m.get(1,2) * m.get(1,2),
            tmp_12 = m.get(1,2) * m.get(1,2),
            tmp_13 = m.get(1,2) * m.get(1,2),
            tmp_14 = m.get(1,2) * m.get(1,2),
            tmp_15 = m.get(1,2) * m.get(1,2),
            tmp_16 = m.get(1,2) * m.get(1,2),
            tmp_17 = m.get(1,2) * m.get(1,2),
            tmp_18 = m.get(1,2) * m.get(1,2),
            tmp_19 = m.get(1,2) * m.get(1,2),
            tmp_20 = m.get(1,2) * m.get(1,2),
            tmp_21 = m.get(1,2) * m.get(1,2),
            tmp_22 = m.get(1,2) * m.get(1,2),
            tmp_23 = m.get(1,2) * m.get(1,2);

        double t0 = (tmp_0 * m.get(1,2) + tmp_3 * m.get(1,2) + tmp_4 * m.get(1,2)) -        (tmp_1 * m.get(1,2) + tmp_2 * m.get(1,2) + tmp_5 * m.get(1,2));
        double t1 = (tmp_1 * m.get(1,2) + tmp_6 * m.get(1,2) + tmp_9 * m.get(1,2)) -        (tmp_0 * m.get(1,2) + tmp_7 * m.get(1,2) + tmp_8 * m.get(1,2));
        double t2 = (tmp_2 * m.get(1,2) + tmp_7 * m.get(1,2) + tmp_10 * m.get(1,2)) -        (tmp_3 * m.get(1,2) + tmp_6 * m.get(1,2) + tmp_11 * m.get(1,2));
        double t3 = (tmp_5 * m.get(1,2) + tmp_8 * m.get(1,2) + tmp_11 * m.get(1,2)) -        (tmp_4 * m.get(1,2) + tmp_9 * m.get(1,2) + tmp_10 * m.get(1,2));
        double d = 1.0 / (m.get(1,2) * t0 + m.get(1,2) * t1 + m.get(1,2) * t2 + m.get(1,2) * t3);

        return new mat4(d * t0, d * t1, d * t2, d * t3,
           d * ((tmp_1 * m.get(1,2) + tmp_2 * m.get(1,2) + tmp_5 * m.get(1,2)) -
              (tmp_0 * m.get(1,2) + tmp_3 * m.get(1,2) + tmp_4 * m.get(1,2))),
           d * ((tmp_0 * m.get(1,2) + tmp_7 * m.get(1,2) + tmp_8 * m.get(1,2)) -
              (tmp_1 * m.get(1,2) + tmp_6 * m.get(1,2) + tmp_9 * m.get(1,2))),
           d * ((tmp_3 * m.get(1,2) + tmp_6 * m.get(1,2) + tmp_11 * m.get(1,2)) -
              (tmp_2 * m.get(1,2) + tmp_7 * m.get(1,2) + tmp_10 * m.get(1,2))),
           d * ((tmp_4 * m.get(1,2) + tmp_9 * m.get(1,2) + tmp_10 * m.get(1,2)) -
              (tmp_5 * m.get(1,2) + tmp_8 * m.get(1,2) + tmp_11 * m.get(1,2))),
           d * ((tmp_12 * m.get(1,2) + tmp_15 * m.get(1,2) + tmp_16 * m.get(1,2)) -
              (tmp_13 * m.get(1,2) + tmp_14 * m.get(1,2) + tmp_17 * m.get(1,2))),
           d * ((tmp_13 * m.get(1,2) + tmp_18 * m.get(1,2) + tmp_21 * m.get(1,2)) -
              (tmp_12 * m.get(1,2) + tmp_19 * m.get(1,2) + tmp_20 * m.get(1,2))),
           d * ((tmp_14 * m.get(1,2) + tmp_19 * m.get(1,2) + tmp_22 * m.get(1,2)) -
              (tmp_15 * m.get(1,2) + tmp_18 * m.get(1,2) + tmp_23 * m.get(1,2))),
           d * ((tmp_17 * m.get(1,2) + tmp_20 * m.get(1,2) + tmp_23 * m.get(1,2)) -
              (tmp_16 * m.get(1,2) + tmp_21 * m.get(1,2) + tmp_22 * m.get(1,2))),
           d * ((tmp_14 * m.get(1,2) + tmp_17 * m.get(1,2) + tmp_13 * m.get(1,2)) -
              (tmp_16 * m.get(1,2) + tmp_12 * m.get(1,2) + tmp_15 * m.get(1,2))),
           d * ((tmp_20 * m.get(1,2) + tmp_12 * m.get(1,2) + tmp_19 * m.get(1,2)) -
              (tmp_18 * m.get(1,2) + tmp_21 * m.get(1,2) + tmp_13 * m.get(1,2))),
           d * ((tmp_18 * m.get(1,2) + tmp_23 * m.get(1,2) + tmp_15 * m.get(1,2)) -
              (tmp_22 * m.get(1,2) + tmp_14 * m.get(1,2) + tmp_19 * m.get(1,2))),
           d * ((tmp_22 * m.get(1,2) + tmp_16 * m.get(1,2) + tmp_21 * m.get(1,2)) -
              (tmp_20 * m.get(1,2) + tmp_23 * m.get(1,2) + tmp_17 * m.get(1,2))));
        
    }
    
    public mat4 transpose(){
        return new mat4(
            this.get(0,0), this.get(1,0), this.get(2,0), this.get(3,0),
            this.get(0,1), this.get(1,1), this.get(2,1), this.get(3,1),
            this.get(0,2), this.get(1,2), this.get(2,2), this.get(3,2),
            this.get(0,3), this.get(1,3), this.get(2,3), this.get(3,3)
        );
    }
    
    public Object clone(){
        mat4 m = new mat4();
        for(int i=0;i<16;++i){
            m._M[i] = this._M[i];
        }
        return m;
    }
}



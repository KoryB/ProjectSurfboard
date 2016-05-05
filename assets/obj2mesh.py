#!/usr/bin/env python3

import sys
import array
import itertools
import os.path 
import os

try:
    import tkinter.filedialog
    import tkinter
except ImportError:
    pass

WITH_ADJACENCY=False
WITH_TANGENTS=False
WITH_SKELETON=False

class vec4:
    def __init__(self,x,y,z,w):
        self.x=x
        self.y=y
        self.z=z
        self.w=w
    def __getitem__(self,idx):
        if idx == 0 : return self.x
        elif idx == 1 : return self.y
        elif idx == 2 : return self.z
        elif idx == 3: return self.w
        else: assert 0
    #def __sub__(self,o):
    #    return vec3(self.x-o.x,self.y-o.y,self.z-o.z)
    #def __add__(self,o):
    #    return vec3(self.x+o.x,self.y+o.y,self.z+o.z)


class vec3:
    def __init__(self,x,y,z):
        self.x=x
        self.y=y
        self.z=z
    def __getitem__(self,idx):
        if idx == 0 : return self.x
        elif idx == 1 : return self.y
        elif idx == 2 : return self.z
        else: assert 0
    def __sub__(self,o):
        return vec3(self.x-o.x,self.y-o.y,self.z-o.z)
    def __add__(self,o):
        return vec3(self.x+o.x,self.y+o.y,self.z+o.z)
        
class vec2:
    def __init__(self,x,y):
        self.x=x
        self.y=y
    def __getitem__(self,idx):
        if idx == 0 : return self.x
        elif idx == 1 : return self.y
        else: assert 0
    def __sub__(self,o):
        return vec2(self.x-o.x,self.y-o.y)

class Face:
    def __init__(self):
        self.vertices=[]    #list of Vertex objects. vi,ti,ni refer to original file verts
        self.outputindex=[] #indices in output vertex data
        self.neighborvertexindex=[]  #only used if we are doing adjacency
        
class Vertex:
    def __init__(self,vi,ti,ni):
        self.vi=vi
        self.ti=ti
        self.ni=ni
        
DX=False
def main(infile):
    print("Processing",infile)
    
    outfile = infile+".mesh"
    
    #vertex data will be a list of vec3's
    vertexdata = [] 
    
    #list of vec2's
    texturedata = [] 
    
    #list of vec3's
    normaldata = []
    
    #list of Face objects
    faces = [] 
    
    #materials. Key = material name; value = dictionary
    mdict = { None: {"facecount":0} } 
    
    #current material
    currmtl=None

    #if doing skeleton meshes
    numbones=0
    numframes=0
    maxdepth=0
    boneheads=[]
    bonetails=[]
    matrices=[]
    quaternions=[]
    weights=[]      #matches entries in vertexdata
    influences=[]   #matches entries in vertexdata
    bonenames=[]
    
    vertices_per_face=None
    
    infp = open(infile)
    for L in infp:
        L=L.strip()
        
        if len(L) == 0:
            continue
            
        L = L.split(" ")
        
        if L[0].startswith("#"):
            pass
        elif L[0] == "o" :
            currobj = L[1]
        elif L[0] == "bone":
            pass
        elif L[0] == "translation":
            pass
        elif L[0] == "bonenames":
            bonenames = L[1:]
        elif L[0] == "head":
            pt = [float(q) for q in L[2:]]
            boneheads.append( vec4(pt[0],pt[1],pt[2],pt[3]) )
        elif L[0] == "tail":
            pt = [float(q) for q in L[2:]]
            bonetails.append( vec4(pt[0],pt[1],pt[2],pt[3]) )
        elif L[0] == "numframes":
            numframes = int(L[1])
        elif L[0] == "numbones":
            numbones = int(L[1])
        elif L[0] == "maxdepth":
            maxdepth = int(L[1])
        elif L[0] == "matrix":
            framenum = int(L[1])
            bonenum = int(L[2])
            mdata = [float(q) for q in L[3:]]
            while len(matrices) <= framenum:
                matrices.append([])
            while len(matrices[framenum]) <= bonenum:
                matrices[framenum].append(None)
            matrices[framenum][bonenum] = mdata
        elif L[0] == "quaternion":
            framenum = int(L[1])
            bonenum = int(L[2])
            qdata = [float(q) for q in L[3:]]
            while len(quaternions) <= framenum:
                quaternions.append([])
            while len(quaternions[framenum]) <= bonenum:
                quaternions[framenum].append(None)
            quaternions[framenum][bonenum] = qdata
        elif L[0] == "v" :
            #a vertex (xyz) specification
            pt = [float(q) for q in L[1:]]
            if DX: pt[2]=-pt[2]
            vertexdata.append(vec3(pt[0],pt[1],pt[2]))
            if len(pt) == 11:
                weights.append(vec4( pt[3],pt[4],pt[5],pt[6]))
                influences.append(vec4(pt[7],pt[8],pt[9],pt[10]))
        elif L[0] == "vt" :
            #texture coordinate
            pt = [float(q) for q in L[1:]]
            if DX: pt[1] = 1-pt[1]
            texturedata.append(vec2(pt[0],pt[1]))
        elif L[0] == "vn":
            #normal
            pt = [float(q) for q in L[1:]]
            if DX: pt[2]=-pt[2]
            normaldata.append( vec3(pt[0],pt[1],pt[2]) )
        elif L[0] == "f" :
            #face (triangle)
            
            if currmtl in mdict:
                mdict[currmtl]["facecount"] += 1
            else:
                print(currmtl,"not in")
            V = L[1:]
            
            if vertices_per_face == None:
                vertices_per_face = len(V)
            
            if len(V) != vertices_per_face:
                print("Warning: Face size mismatch")
                continue 
            
            f=Face()
            
            for vspec in V:
                #four formats possible: vi  vi//ni  vi/ti  vi/ti/ni
                tmp = vspec.split("/") 
                
                #obj uses one-based indexing, so we must subtract one here
                vi=int(tmp[0])-1 
                
                #if no texture coordinate, make one up
                if len(tmp) < 2 or len(tmp[1]) == 0:
                    ti=0
                else:
                    ti = int(tmp[1])-1 
                    
                if len(tmp) < 3 or len(tmp[2]) == 0:
                    ni=0
                else:
                    ni = int(tmp[2])-1
      
                f.vertices.append( Vertex(vi=vi,ti=ti,ni=ni) )
            
            if DX:
                assert 0
                t=list(reversed(t))
                
            faces.append(f) 
            
        elif L[0] == "mtllib" :
            #material library; must parse it
            ML = open(L[1]).readlines()
            
            #look at each material and store information about it
            for m in ML:
                m=m.strip()
                if len(m) == 0:
                    continue
                m=m.strip().split(" ",1)
                if m[0].startswith("#"):
                    pass
                elif m[0] == "newmtl" :
                    mname = m[1] 
                    mdict[mname]={ "facecount":0 }
                else:
                    mdict[mname][m[0]] = m[1]
        elif L[0] == "usemtl" :
            #change currently active material
            currmtl = L[1] 
        elif L[0] == "s":
            pass
        else:
            print("Note: Skipping",L)
    
    #if object lacks texture coordinates, make sure we don't
    #get an out-of-bounds error
    if len(texturedata) == 0 :
        texturedata = [vec2(0,0)]

    if WITH_TANGENTS:
        #compute tangents
        tangents=[]
        for i in range(len(vertexdata)):
            tangents.append(vec3(0,0,0))
            
        for face in faces:
            q = vertexdata[face.vertices[0].vi]
            r = vertexdata[face.vertices[1].vi]
            s = vertexdata[face.vertices[2].vi]
            qtex = texturedata[face.vertices[0].ti]
            rtex = texturedata[face.vertices[1].ti]
            stex = texturedata[face.vertices[2].ti]
            r_ = r-q
            s_ = s-q
            r_tex = rtex-qtex
            s_tex = stex-qtex
            try:
                tmp = 1.0/(r_tex[0]*s_tex[1]-s_tex[0]*r_tex[1])
            except ZeroDivisionError:
                print("Warning: Bad texture coordinates; bump mapping will be bad")
                tmp=1
            R00 = tmp*s_tex[1]
            R01 = tmp*-r_tex[1]
            T = vec3( 
                R00*r_[0]+R01*s_[0],
                R00*r_[1]+R01*s_[1],
                R00*r_[2]+R01*s_[2]
            )
            for i in range(3):
                tangents[face.vertices[i].vi] = tangents[face.vertices[i].vi] + T
            
                
        #normalize
        for i in range(len(tangents)):
            tmp=tangents[i]
            le=(tmp[0]*tmp[0]+tmp[1]*tmp[1]+tmp[2]*tmp[2])**0.5
            try:
                tmp.x /= le
                tmp.y /= le
                tmp.z /= le
            except ZeroDivisionError:
                print("Warning: Bad texture coordinates; bump mapping will be bad")
                tmp=vec3(0,0,1)
            tangents[i] = tmp
        
    
    #first, determine how many unique vertices we'll have
    vmap={}     #key=vi,ti,ni  Value=index in vdata
    #vdata=[]    #list of (x,y,z,s,t,nx,ny,nz) tuples
    
    numverts=0
    #vertex, texcoord, normal, tangent data
    vdata = []
    tcdata = []
    ndata = []
    tdata = []
    wdata=[]        #weights
    infdata=[]      #influences
    
    idata=[]    #triangle indices: Refers to entries in vdata


    for T in faces:
        #T will be a list of three vi,ti,ni tuples
        for vee in T.vertices:
            vi,ti,ni = vee.vi,vee.ti,vee.ni
            key = (vi,ti,ni)
            if key not in vmap:
                #first time we've seen this vertex, so add it
                #to our vertex list and our dictionary
                vmap[key]=numverts
                numverts += 1
                vdata += [vertexdata[vi][0],
                    vertexdata[vi][1],
                    vertexdata[vi][2]
                ]
                tcdata += [
                    texturedata[ti][0],
                    texturedata[ti][1] 
                ]
                ndata += [normaldata[ni][0],
                    normaldata[ni][1],
                    normaldata[ni][2]
                ]
                
                if WITH_TANGENTS:
                    tdata += [tangents[vi].x,
                        tangents[vi].y,
                        tangents[vi].z
                    ]
                if WITH_SKELETON:
                    wdata += [weights[vi].x,weights[vi].y,weights[vi].z,weights[vi].w]
                    infdata += [influences[vi].x,influences[vi].y,influences[vi].z,influences[vi].w]
                    
            T.outputindex.append( vmap[key] )
    
    if not WITH_ADJACENCY:
        for T in faces:
            for oi in T.outputindex:
                idata.append( oi )
    else:
        #key = edge (ie, pair of indices from the OBJ file)
        #value = list of faces that share that edge
        adjacent={}
        for T in faces:
            assert len(T.vertices) == 3
            for i in range(3):
                vi1 = T.vertices[i].vi
                vi2 = T.vertices[(i+1)%3].vi
                key = frozenset( [vi1,vi2] )
                if key not in adjacent:
                    adjacent[key] = []
                adjacent[key].append(T)

        for T in faces:
            for i in range(3):
                vi1 = T.vertices[i].vi
                vi2 = T.vertices[(i+1)%3].vi
                key = frozenset( [vi1,vi2] )
                adj = adjacent[key]
                if len(adj) != 2:
                    #bare edge. Punt.
                    T.neighborvertexindex.append(T.outputindex[(i+2)%3])
                else:
                    if adj[0] == T:
                        other = adj[1]
                    else:
                        other = adj[0]
                    
                    for j in range(3):
                        i1 = other.vertices[j].vi
                        i2 = other.vertices[(j+1)%3].vi
                        i3 = other.vertices[(j+2)%3].vi
                        key2 = frozenset([i1,i2])
                        if key2 == key:
                            T.neighborvertexindex.append(other.outputindex[(j+2)%3])
                            break
                    else:
                        assert 0
                
        for T in faces:
            for i in range(3):
                idata.append( T.outputindex[i])
                idata.append( T.neighborvertexindex[i])
                

    
    #information for the user
    print(len(vdata),"vertices,",len(idata),"indices") 
    
    #output data
    ofp = open(outfile,"wb")
    ofp.write(b"mesh_07\n")
    
    ofp.write( ("num_vertices "+str(len(vdata))+"\n").encode() ) 
    #ofp.write( ("floats_per_vertex "+str(len(vdata[0]))+"\n").encode())
    #ofp.write( ("total_vertex_floats "+str(len(vdata)*len(vdata[0]))+"\n").encode())
    ofp.write( ("num_indices "+str(len(idata))+"\n").encode() ) 
    ofp.write( ("with_adjacency "+str(WITH_ADJACENCY)+"\n").encode())
    ofp.write( ("numbones "+str(numbones)+"\n").encode())
    ofp.write( ("numframes "+str(numframes)+"\n").encode())
    ofp.write( ("maxdepth "+str(maxdepth)+"\n").encode())
    
    #if we have a texture, write its name
    #select the material with the most faces
    maxf=0
    maxm=None
    for m in mdict:
        if mdict[m]["facecount"] > maxf:
            maxf=mdict[m]["facecount"]
            maxm=m
        
    
    for k in mdict[maxm]:
        if k != "facecount":
            ofp.write( (k+" "+mdict[maxm][k]+"\n").encode() )
        
    def write_floats(s,ofp,a):
        if len(a) == 0:
            return
            
        #b = array.array("f",itertools.chain.from_iterable(vdata))
        #b = b.tobytes()
        b = array.array("f",a)
        ofp.write( (s+" "+str(len(b)*4)).encode() )
        #must pad to 4 byte boundary
        while( (ofp.tell()+1) % 4 != 0 ):
            ofp.write(b" ") 
        ofp.write(b"\n") 
        ofp.write(b) 
        ofp.write(b"\n")
        
    #write the vertex data
    write_floats( "vertex_data", ofp, vdata)
    write_floats( "texcoord_data", ofp, tcdata)
    write_floats( "normal_data", ofp, ndata)
    write_floats( "tangent_data", ofp, tdata)
    write_floats( "weight_data",ofp,wdata)
    write_floats( "influence_data",ofp,infdata)
    
    
    #write index data. DX doesn't support byte indices
    if False and not DX and len(vdata) < 256:
        b = array.array("B",idata)
        ofp.write(b"bits_per_index 8\n")
    elif len(vdata) < 65536:
        b = array.array("H",idata)
        ofp.write(b"bits_per_index 16\n")
    else:
        b = array.array("I",idata)
        ofp.write(b"bits_per_index 32\n")
    
    
    b = b.tobytes()
    ofp.write( ("index_data "+str(len(b))).encode() )
    
    while( (ofp.tell()+1) % 4 != 0 ):
        ofp.write(b" ") 
    ofp.write(b"\n") 
    ofp.write(b) 
    ofp.write(b"\n")

    tmp=[]
    for b in boneheads:
        tmp.append( b.x )
        tmp.append( b.y )
        tmp.append( b.z )
        tmp.append( b.w )
    write_floats("boneheads",ofp,tmp)
    
    
    tmp=[]
    for b in bonetails:
        tmp.append( b.x )
        tmp.append( b.y )
        tmp.append( b.z )
        tmp.append( b.w )
    write_floats("bonetails",ofp,tmp)

    tmp=[]
    for framedata in matrices:
        for bonedata in framedata:
            tmp += bonedata
    write_floats("matrices",ofp,tmp)
        

    tmp=[]
    for framedata in quaternions:
        for bonedata in framedata:
            tmp += bonedata
    write_floats("quaternions",ofp,tmp)

    
    ofp.write(b"\nend\n") 
    ofp.close()


def do_it(infiles):
    print("Adjacency:",WITH_ADJACENCY)
    print("Tangents:",WITH_TANGENTS)
    print("Skeleton:",WITH_SKELETON)
    
    olddir=os.getcwd()
    for fname in infiles:
        dirname,fname = os.path.split(fname)
        if dirname:
            os.chdir(dirname)
        main(fname)
        os.chdir(olddir)
    sys.exit(0)


if len(sys.argv) > 1:
    if sys.argv[1] == "--dx":
        DX=True
        del sys.argv[1]
        
#get input and output file names
if len(sys.argv) == 1:
    infiles=[]
    def choose_files(win):
        infiles[:] = tkinter.filedialog.askopenfilenames(parent=win)
        if len(infiles) == 1:
            txt="1 file selected"
        else:
            txt=str(len(infiles))+" files selected"
        flist.configure(text=txt)
    def ok_clicked():
        global WITH_ADJACENCY,WITH_TANGENTS,WITH_SKELETON
        WITH_ADJACENCY = adj_var.get()
        WITH_TANGENTS = tan_var.get()
        WITH_SKELETON = skel_var.get()
        do_it(infiles)
        
    win = tkinter.Tk()
    fr = tkinter.Frame(win)
    fr.pack(side=tkinter.TOP,expand=tkinter.NO,fill=tkinter.X)
    cf = tkinter.Button(fr,text="Choose files...",command=lambda: choose_files(win))
    cf.pack(side=tkinter.LEFT)
    flist = tkinter.Label(fr,text="(No files selected)")
    flist.pack(side=tkinter.RIGHT)
    
    adj_var = tkinter.IntVar()
    with_adj_cb = tkinter.Checkbutton(win,text="Use adjacency",variable=adj_var)
    with_adj_cb.pack()
    
    tan_var = tkinter.IntVar()
    with_tan_cb = tkinter.Checkbutton(win,text="Include tangents",variable=tan_var)
    with_tan_cb.pack()
    
    skel_var = tkinter.IntVar()
    with_skel_cb = tkinter.Checkbutton(win,text="Include skeleton",variable=skel_var)
    with_skel_cb.pack()
    
    fr = tkinter.Frame(win)
    fr.pack()
    ok=tkinter.Button(fr,text="OK",command=ok_clicked)
    ok.pack(side=tkinter.LEFT)
    cancel = tkinter.Button(fr,text="Cancel",command=lambda: sys.exit(0))
    cancel.pack(side=tkinter.RIGHT)
    
    win.mainloop()
else:
    a=sys.argv[1:]
    while a[0].startswith("-"):
        opt = a.pop(0)
        if opt == "-a":
            WITH_ADJACENCY=True
        elif opt == "-t":
            WITH_TANGENTS=True
        elif opt == "-s":
            WITH_SKELETON=True
        else:
            assert 0
    do_it(a)
   

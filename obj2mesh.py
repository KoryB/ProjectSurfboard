#!/usr/bin/env python3

import sys
import array
import itertools
from math3d import *
try:
    import tkinter.filedialog
except ImportError:
    pass


def main():
    DX=False

    if len(sys.argv) > 1:
        if sys.argv[1] == "--dx":
            DX=True
            del sys.argv[1]
            
    #get input and output file names
    if len(sys.argv) == 1:
        infile = tkinter.filedialog.askopenfilename()
        if not infile:
            return
    else:
        infile = sys.argv[1]
        
    outfile = infile+".mesh"
    
    #vertex data will be a list of vec3's
    vertexdata = [] 
    
    #list of vec2's
    texturedata = [] 
    
    #list of vec3's
    normaldata = []
    
    #list of triangles. Each triangle consists of three 
    #(vertex index, texture index, normal index) tuples
    triangles = [] 
    
    #materials. Key = material name; value = dictionary
    mdict = { None: {"facecount":0} } 
    
    #current material
    currmtl=None

    for L in open(infile):
        L=L.strip()
        
        if len(L) == 0:
            continue
            
        L = L.split(" ")
        
        if L[0].startswith("#"):
            pass
        elif L[0] == "o" :
            currobj = L[1]
        elif L[0] == "v" :
            #a vertex (xyz) specification
            pt = [float(q) for q in L[1:]]
            if DX: pt[2]=-pt[2]
            vertexdata.append(vec3(pt[0],pt[1],pt[2]))
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
                
            V = L[1:]
            if len(V) != 3:
                print("Warning: Non-triangles present:",len(V),"vertices") 
                continue 
            
            #t=the current triangle
            t=[] 
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
                    
                t.append( (vi,ti,ni) )
            
            if DX:
                t=list(reversed(t))
                
            triangles.append(t) 
            
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
        else:
            print("Note: Skipping",L)
    
    #if object lacks texture coordinates, make sure we don't
    #get an out-of-bounds error
    if len(texturedata) == 0 :
        texturedata += [[0,0]]
    
    #first, determine how many unique vertices we'll have
    vmap={}     #key=vi,ti,ni  Value=index in vdata
    vdata=[]    #list if (x,y,z,s,t,nx,ny,nz) tuples
    idata=[]    #triangle indices: Refers to entries in vdata
    
    for T in triangles:
        #T will be a list of three vi,ti,ni tuples
        
        for vi,ti,ni in T:
            key = (vi,ti,ni)
            if key not in vmap:
                #first time we've seen this vertex, so add it
                #to our vertex list and our dictionary
                vmap[key]=len(vdata)
                tmp1=len(vdata)
                vdata.append(  (
                    vertexdata[vi][0],
                    vertexdata[vi][1],
                    vertexdata[vi][2],
                    texturedata[ti][0],
                    texturedata[ti][1],
                    normaldata[ni][0],
                    normaldata[ni][1],
                    normaldata[ni][2],
                ) )
            idata.append( vmap[key] ) 
    
    #information for the user
    print(len(vdata),"vertices") 
    print(len(idata)//3,"triangles") 
    
    #output data
    ofp = open(outfile,"wb")
    ofp.write(b"mesh_01") 
    if not DX:
        ofp.write(b"_GL\n")
    else:
        ofp.write(b"_DX\n")
        
    
    ofp.write( ("num_vertices "+str(len(vdata))+"\n").encode() ) 
    ofp.write( ("floats_per_vertex "+str(len(vdata[0]))+"\n").encode())
    ofp.write( ("total_vertex_floats "+str(len(vdata)*len(vdata[0]))+"\n").encode())
    ofp.write( ( "num_indices "+str(len(idata))+"\n").encode() ) 
    
    #if we have a texture, write its name
    #select the material with the most faces
    maxf=0
    maxm=None
    for m in mdict:
        if mdict[m]["facecount"] > maxf:
            maxf=mdict[m]["facecount"]
            maxm=m
            
    if "map_Kd" in mdict[maxm]:
        ofp.write( ("texture_file "+mdict[maxm]["map_Kd"]+"\n").encode()  ) 
    if "map_Ks" in mdict[maxm]:
        ofp.write( ("specular_map "+mdict[maxm]["map_Ks"]+"\n").encode()  ) 
    if "map_Ke" in mdict[maxm]:
        ofp.write( ("emission_map "+mdict[maxm]["map_Ke"]+"\n").encode()  ) 
        
    #write the vertex data
    b = array.array("f",itertools.chain.from_iterable(vdata))
    b = b.tobytes()
    ofp.write( ("vertex_data "+str(len(b))).encode() )

    #must pad to 4 byte boundary
    while( (ofp.tell()+1) % 4 != 0 ):
        ofp.write(b" ") 
    ofp.write(b"\n") 
    ofp.write(b) 
    ofp.write(b"\n")
    
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
    
    ofp.write(b"\nend\n") 
    ofp.close()


main()


import yacc 
import lex
import sys
import re

class Definition:
    def __init__(self,name,value,braceblock):
        self.name=name
        assert value == None or type(value) == list
        self.value = value
        assert braceblock == None or type(braceblock) == list
        self.braceblock = braceblock
        
    def __repr__(self):
        lst=[ "<"+self.name]
        
        if self.value != None:
            lst.append(" val="+str(self.value))
        
        if self.braceblock:
            l2=[]    
            for c in self.braceblock:
                l2.append(repr(c))
            lst.append(" {")
            lst.append(",".join(l2))
            lst.append("}")
        
        lst.append(">")
        return "".join(lst)


tokens = (
  'BIGLIST',
  'num','id','COLON',"LBRACE","RBRACE","string","COMMA","specialkeyword"
)

def t_comment(t):
    r';[^\n]*($|\n)'
    t.lexer.lineno += t.value.count("\n")
    
    pass
def t_whitespace(t):
    r'\s+'
    t.lexer.lineno += t.value.count("\n")
    pass
    
def t_string(t):
    r'"[^"]*"'
    t.value = t.value[1:-1]
    return t

t_BIGLIST = r"""(?x)
    \b( Vertices|Normals|PolygonVertexIndex|
        Smoothing|ColorIndex|UV|UVIndex|
        Indexes|Weights):\s*
        ([\-.0-9Ee+]+(\s*,\s*[\-.0-9Ee+]+)+)"""
t_num = r"-?\d+(\.\d+)?([Ee][+-]?\d+)?"
t_id = r"\w+"
t_COLON = ":"
t_LBRACE = r"\{"
t_RBRACE = r"\}"
#t_string = '"[^"]*"'
t_COMMA = ","
t_specialkeyword = r"\b([LWY])\b"

def t_error(t):
    print("Invalid input:",t)
    assert 0

start="S"

    
def p_epsilon(p):
    'epsilon : '
    pass

def p_error(p):
    print("Error:",p)
    print("Next token:",yacc.token())
    assert 0
    
def p_S(p):
    "S : DefList"
    p[0] = p[1]
    
def p_DefList(p):
    """DefList : Definition DefList 
            | epsilon"""
    if len(p) > 2:
        if p[2]:
            #graft it on backwards...
            if type(p[2]) == list:
                p[0] = p[2]
                p[0].append(p[1])
            else:
                p[0] = [p[1],p[2]]
        else:
            p[0] = [p[1]]

def p_Definition(p):
    """Definition : id COLON OptionalThings OptionalBraceBlock"""
    if p[3]:
        #accounts for the backwards grafting in OptionalThings
        p[3] = list(reversed(p[3]))
    p[0] = Definition(p[1],p[3],p[4])


spacerex = re.compile(r"\s+")
#to make handling large lists a lot faster...
def p_Definition2(p):
    """Definition :  BIGLIST"""
    st = p[1]
    idx = st.find(":")
    name = st[:idx].strip()
    value = spacerex.sub("",st[idx+1:]).split(",")
    p[0] = Definition(name,value,None)
    
#this reverses the list, so the user of OptionalThings
#must reverse the list if it wants them in the original order.
def p_OptionalThings(p):
    """OptionalThings : string OptionalThings2 
        | num OptionalThings2 
        | specialkeyword
        | epsilon"""
    if len(p) > 2:
        if p[2]:
            p[0] = p[2]
            p[0].append(p[1])
        else:
            p[0]=[p[1]]

def p_OptionalThings2(p):
    """OptionalThings2 : COMMA string OptionalThings2 
            | COMMA num OptionalThings2
            | COMMA specialkeyword OptionalThings2
            | epsilon"""
    if len(p) > 2:
        if p[3]:
            p[0] = p[3]
            p[0].append(p[2])
        else:
            p[0] = [p[2]]

def p_OptionalBraceBlock(p):
    """OptionalBraceBlock : LBRACE DefList RBRACE
                | epsilon"""
    if len(p) > 2:
        p[0] = p[2]
        

def parse(s):

    lexer = lex.lex()
    lexer.input(s)
    parser = yacc.yacc() #debug=1
    print("Parsing...")
    root = parser.parse(lexer=lexer)    #debug=1
    return root

#we take a list of bones from the bvh file
#so we ensure we're using the same numbers here as the converted
#bvh file
def fbx2obj(infbx,ofp,mfname,bone_name_to_index):
    fp=open(infbx)
    #ofp=open(outobj,"w")
    print("# converted from",infbx,file=ofp)
    
    dat = fp.read()
    toplevel_nodes = parse(dat)
    print("Parsed input")                    
    
    meshmaterials={}
    meshtextures={}
    materialnames=set()
    texturenames=set()
    
    #gather mesh/material information
    for c in toplevel_nodes:
        if c.name == "Connections":
            for d in c.braceblock:
                if d.name == "Connect":
                    if d.value[1].startswith("Material::"):
                        mtlname = d.value[1][10:]
                        meshname = d.value[2]
                        if meshname.startswith("Model::"):
                            meshname = meshname[7:]
                            
                        if meshname in meshmaterials:
                            print("Warning: Mesh",meshname,"uses more than one material (",
                                mtlname,meshmaterials[meshname],")")
                        meshmaterials[meshname]=mtlname
                        materialnames.add(mtlname)
                    elif d.value[1].startswith("Texture::"):
                        texname = d.value[1][9:]
                        meshname = d.value[2]
                        if meshname.startswith("Model::"):
                            meshname = meshname[7:]
                            
                        if meshname in meshtextures:
                            print("Warning: Mesh",meshname,"uses more than one texture (",
                            texname,meshtextures[meshname],")")
                        meshtextures[meshname]=texname
                        texturenames.add(texname)
                        
    
    #look for objects node
    for c in toplevel_nodes:
        if c.name == "Objects":
            obnode = c
            break
    else:
        print("Objects not found")
        assert 0
       
       
 
    #get material and information
    mtlinfo={}
    texinfo={}
    
    for c in obnode.braceblock:
        if c.name == "Material":
            mname = c.value[0][10:]
            if mname in materialnames:
                mp={}   #material properties
                for c2 in c.braceblock:
                    if c2.name == "Properties60":
                        for c3 in c2.braceblock:
                            if c3.name == "Property":
                                if c3.value[1] == "ColorRGB":
                                    v=[float(q) for q in c3.value[3:6]]
                                elif c3.value[1] == "double":
                                    v=float(c3.value[3])
                                else:
                                    v=None
                            if v != None:
                                mp[c3.value[0]]=v
                mtlinfo[mname]=mp
        elif c.name == "Texture":
            tname = c.value[0][9:]
            if tname in texturenames:
                tp={}
                for c2 in c.braceblock:
                    if c2.value:    #not None and not empty list
                        tp[c2.name]=c2.value[0]
                texinfo[tname]=tp
                
   
    mfp=open(mfname,"w")
    print("mtllib",mfname,file=ofp)
    
    def mul(a,b):
        return " ".join([str(a*q) for q in b])
                            

    #obj mixes texture + material properties in one entry, so we must
    #do the same here.
    known_materials=set()
    mesh_materials={}       #map mesh name to material name
    tmp = set(meshmaterials.keys()).union(set(meshtextures.keys()))
    for meshname in tmp:
        mtlname = meshmaterials.get(meshname,"")
        texname = meshtextures.get(meshname,"")
        mp = mtlinfo.get(mtlname,{})
        tp = texinfo.get(texname,{})
        generatedname = mtlname+"_"+texname
        if generatedname not in known_materials:
            print("newmtl",generatedname,file=mfp)
            print("Ka",mul(mp.get("AmbientFactor",1),
                    mp.get("AmbientColor",[0,0,0])),file=mfp)
            print("Kd",mul(mp.get("DiffuseFactor",1),
                    mp.get("DiffuseColor",[0,0,0])),file=mfp)
            print("Ks",mul(mp.get("SpecularFactor",1),
                    mp.get("SpecularColor",[0,0,0])),file=mfp)
            print("d",mp.get("Opacity",1.0),file=mfp)
            if "FileName" in tp:
                print("map_Kd",tp.get("FileName"),file=mfp)  
            #print("map_Ks",??)
            #print("map_Ke",??)
            #print("map_Ns",??)
            #print("map_Bump",?)
            
        mesh_materials[meshname]=generatedname
                
    mfp.close()
    
    vbones = getBoneInfo(obnode,bone_name_to_index)
   
    print("#bone bone_name bone_index",file=ofp)
    for b in bone_name_to_index:
        print("bone",b,bone_name_to_index[b],file=ofp)
        
    meshes={}
    
    vbase=0
    tbase=0
    nbase=0
    
    #look for objects
    for c in obnode.braceblock:
        if c.name == "Model":
            objname = c.value[0]
            if objname.startswith("Model::"):
                objname = objname[7:]

            if len(c.value) == 2 and c.value[1] == "Mesh":
                print("Found mesh",c.value[0])
                    
                V=None      #vertex data: list of x,y,z,x,y,z,...
                I=None      #indices: list of tuples, one per face
                N=None      #normals: list of x,y,z,x,y,z,...
                T=None      #texcoord: list of s,t,s,t,s,t,...
                TI=None     #texture indices: list of ints
                
                NMode = None
                ni=0        #total number of indices in all faces
                
                
                for definition in c.braceblock:
                    #print("DEF:",definition)
                    if definition.name == "Properties60":
                        #mesh properties
                        pass
                    elif definition.name == "Vertices":
                        #vertex data is stored in value
                        V=[float(q) for q in definition.value]
                    elif definition.name == "PolygonVertexIndex":
                        tmp=[int(q) for q in definition.value]
                        I=[[]]
                        for i in tmp:
                            if i < 0:
                                I[-1].append(-int(i)-1)
                                I.append([])
                            else:
                                I[-1].append(int(i))
                            ni+=1
                            
                    elif definition.name == "LayerElementNormal":
                        rit = getv(definition.braceblock,"ReferenceInformationType")[0]
                        mit = getv(definition.braceblock,"MappingInformationType")[0]
                        tmp = rit+mit
                        if tmp == "DirectByVertice":
                            N = [float(q) for q in getv(definition.braceblock,"Normals")]
                            NMode=0
                        elif tmp == "DirectByPolygonVertex":
                            N = [float(q) for q in getv(definition.braceblock,"Normals")]
                            NMode=1
                        else:
                            print("Implement normal:",mit,rit)
                            assert 0
                    elif definition.name == "LayerElementUV":
                        rit = getv(definition.braceblock,"ReferenceInformationType")[0]
                        mit = getv(definition.braceblock,"MappingInformationType")[0]
                        if rit =="IndexToDirect" and mit == "ByPolygonVertex":
                            T=[float(q) for q in getv(definition.braceblock,"UV")]
                            TI = [int(q) for q in getv(definition.braceblock,"UVIndex")]
                        else:
                            #FIXME: need to implement
                            print("Not implemented uv",mit,rit)
                            assert 0
                
                if TI == None and T == None:
                    print("Warning: Object does not have texture")
                    T=[0,0]
                    TI=[0 for x in range(ni)]
                      
                assert len(TI) == ni
                
                if NMode == 0:
                    assert len(N) == len(V)
                elif NMode == 1:
                    assert len(N)/3 == ni
                
                print("o",objname,file=ofp)
                if objname in mesh_materials:
                    print("usemtl",mesh_materials[objname],file=ofp)
                else:
                    print("Warning: Mesh",objname,"has no material")
                    print(mesh_materials)
                    
                print("#vertex data: x y z, then four weights, then four bone indices",file=ofp)
                for i in range(0,len(V),3):
                    vi=int(i/3)
                    print("v",V[i],V[i+1],V[i+2],end="",file=ofp)
                    tmp=[]
                    if vi in vbones:
                        print("",end=" ",file=ofp)
                        
                        for bonename,weight in vbones[vi]:
                            tmp.append((bone_name_to_index[bonename],float(weight)))
                        while len(tmp) < 4:
                            tmp.append( (0,0) )
                        tmp=tmp[:4]
                        tmp.sort(key=lambda x: (-x[1],x[0]) )
                        for bi,wt in tmp:
                            print(wt,end=" ",file=ofp)
                        for bi,wt in tmp:
                            print(bi,end=" ",file=ofp)
                    print("",file=ofp)
                for i in range(0,len(N),3):
                    print("vn",N[i],N[i+1],N[i+2],file=ofp)
                for i in range(0,len(T),2):
                    print("vt",T[i],T[i+1],file=ofp)
                    
                j=0 #running total of all indices seen
                for face in I:
                    if len(face) == 0:
                        continue
                    if len(face) > 3:
                        print("Non-triangle at (x,y,z):",
                                V[face[0]*3],V[face[0]*3+1],V[face[0]*3+2])
                                
                    print("f",end="",file=ofp)
                    for i in range(len(face)):
                        vi = face[i]
                        
                        if NMode == 0:
                            ni = vi
                        elif NMode == 1:
                            ni = j
                        else:
                            assert 0
                        
                        ti = TI[j]
                    
                        print(" "+str(vi+1+vbase)+"/"+str(ti+1+tbase)+"/"+str(ni+1+nbase),end="",file=ofp)
                    
                        j+=1
                    print(file=ofp)
                    
                        
                  
                vbase += int(len(V)/3)
                nbase += int(len(N)/3)
                tbase += int(len(T)/2)
            #else:
            #    print("Not mesh:",c.value)
                
        #else:
        #    print("Not model:",c.name)
 
def getBoneInfo(obnode,boneindices):
    vbones={}
    
    for c in obnode.braceblock:
        if c.name == "Model" and len(c.value) == 2 and c.value[1] == "Limb":
            bonename = c.value[0]
            if bonename.startswith("Model::"):
                bonename = bonename[7:]
            if bonename not in boneindices:
                boneindices[bonename]=len(boneindices)
                print("Found new bone",bonename,"->",boneindices[bonename])
            else:
                print("Matched bone",bonename,"->",boneindices[bonename])
        elif c.name == "Deformer" and len(c.value) == 2 and c.value[1] == "Cluster":
            tmp=c.value[0]
            tmp=tmp.split()
            bonename = tmp[2]
            bb = c.braceblock
            #FIXME: We could have several objects with bone referring to one of them.
            #Will the vertex indices be correct in this case?
            indexes = getv(bb,"Indexes")
            weights = getv(bb,"Weights")
            if indexes == None:
                #print("No deformer indices for",c)
                indexes=[]
                weights=[]
                
            for i in range(len(indexes)):
                idx = int(indexes[i])
                if idx not in vbones:
                    vbones[idx]=[]
                vbones[idx].append( (bonename,weights[i]) )
                
    #print(vbones.keys())
    return vbones
    
    


def getv(lst,name):
    for x in lst:
        if x.name == name:
            return x.value
                
    

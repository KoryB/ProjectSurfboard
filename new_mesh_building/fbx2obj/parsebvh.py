#!/usr/bin/env python3

#IMPORTANT! The mesh + armature should NOT have
#any overall translation, rotation, or scale -- else, this won't work properly.


#for bvh files:
# head position is where the head of the bone is when in rest position, expressed
#   in absolute (world space) coordinates.
# {X,Y,Z}position is where the head of the bone is *relative to its parent's head*
# for a given frame. This allows prismatic joints. However, the "relative to its parent's head"
# is in transformed coordinates -- it's not an offset in absolute world xyz units but rather
# in the transformed xyz axes for that frame

from mmath import *
import sys
import traceback
import re
import sys
    
class TokenList:
    def __init__(self,L):
        self.L=L
        self.i=0
    def peek(self):
        if self.i == len(self.L):
            return None
        return self.L[self.i]
    def get(self):
        self.i+=1
        return self.L[self.i-1]
    def num(self):
        return len(self.L)
        
#a single Bone: One node in the tree
class Node:
    def __init__(self,name,parent):
        
        self.name=name      #bone name
        
        self.ch=[]          #children
        
        self.head=None      #where the bone's head is (absolute xyz coordinates,
                            #not relative to its parent)
                            #when the armature is in rest position
                            
        self.channels=[]    #From bvh file: what data we have in framedata[]
                            #This will be strings {X,Y,Z}{position,rotation}
                            
        self.endeff=None    #where the end effector is. Only defined
                            #if this bone has no children. 
                            #Value is absolute coordinates.
        
        self.parent=parent  #parent bone, or None
        
        #how deep in the tree are we (root=1)
        if parent:
            self.depth = parent.depth+1
        else:
            self.depth=1    
            
        #list of animation data from the bvh
        #framedata[i] has data for frame i
        #Each data item is a list of tuples
        #which are the rotations/translations for the channels, in the order
        #defined in self.channels, above.
        self.framedata=[]
        
        #quaternion data: We compute this from framedata's {XYZ}rotation channels
        #one Quaternion per frame
        self.qframedata=[]
        
        #translation data: We compute this from framedata's {XYZ}position channels
        #One translation per frame.
        self.tframedata=[]
        
        #matrix data: Same as quaternions (just rotations; not translations), but
        #stored as matrix data
        self.mframedata=[]
        
        #this node's unique index.
        #This will get set later.
        self.idx=None
        
        
    #dat = input data (TokenList type)
    #bonelist = a list which will have each bone added to it
    #as the bone is encountered, since that will be needed
    #for the frame-by-frame animation data
    def parse(self,dat,bonelist):
        #dat will be the opening { of this node's definition
        #assume fp is sitting just before the opening { of this
        #item's definition
        x=dat.get()
        assert x == "{"
        while 1:
            x=dat.get()
            if x == "OFFSET":
                #this is relative to the parent, not an
                #absolute position
                x=dat.get()
                y=dat.get()
                z=dat.get()
                self.head=Vector4( float(x),float(y),float(z),0.0)
                if self.parent != None:
                    self.head = self.head + self.parent.head
            elif x == "CHANNELS":
                nc = int(dat.get())
                for i in range(nc):
                    self.channels.append( dat.get() )
            elif x == "JOINT":
                name = dat.get()
                name=name.replace(" ","_").replace(".","_")
                b = Node(name,self)
                bonelist.append(b)
                b.parse(dat,bonelist)
                self.ch.append(b)
            elif x == "End" and dat.peek() == "Site":
                #location of the end effector
                dat.get()  #Site
                dat.get() # {
                assert dat.get() == "OFFSET"
                x=dat.get()
                y=dat.get()
                z=dat.get()
                self.endeff=self.head + Vector4(float(x),float(y),float(z),0.0)
                dat.get()   #}
            elif x == "}":
                return
            else:
                print("Got",x)
                assert 0

        assert len(self.ch) != 0
        
fixme_note=0
def compute_quaternions(n):
    # if we have quaternions a and b:
    # a*b = rotate first by b, then by a
    framedata = n.framedata
    
    for j in range(len(framedata)):
            
        Q=Quaternion(Vector4(0,0,0,0), 1.0)
        M=Matrix4()
        T=[0,0,0]
        for i in range(len(n.channels)):
            if n.channels[i].find("position") != -1:
                if n.channels[i] == "Xposition":
                    T[0] = framedata[j][i]
                elif n.channels[i] == "Yposition":
                    T[1] = framedata[j][i]
                elif n.channels[i] == "Zposition":
                    T[2] = framedata[j][i]
                else:
                    assert 0
            elif n.channels[i].find("rotation") != -1:
                if n.channels[i] == "Xrotation":
                    axis=Vector4(1,0,0,0)
                elif n.channels[i] == "Yrotation":
                    axis=Vector4(0,1,0,0)
                elif n.channels[i] == "Zrotation":
                    axis=Vector4(0,0,1,0)
                else:
                    assert 0
                    
                    
                #this is in degrees
                angle = framedata[j][i]
            
                M=axis_rotation(axis,angle/180.0*3.14159265358979323)*M;
                
                
                #FIXME: Do we need to reverse the order Q*q? We had
                #to for the matrices above... Apparently not?
                #global fixme_note
                #if not fixme_note:
                    #fixme_note=1
                    #print("FIXME: Check for quats: Order?")
                    
                q=quat_for_rot( 
                    angle/180.0 * 3.14159265358979323,
                    axis)
                #Q=q*Q
                Q=Q*q
            else:
                assert 0
        
        #Q = matrix_to_quat(M)        
        n.tframedata.append(T)
        n.qframedata.append(Q)
        n.mframedata.append(M)
        
    for c in n.ch:
        compute_quaternions(c)
        
        
def bvh2rig(bvhfile,rigfile,ascii=True,debug=False):
    #parse a biovision bvh file
    
    fp=open(bvhfile)
    dat=fp.read()
    dat=dat.replace("\r"," ")
    dat=dat.replace("\n"," ")
    dat=dat.strip().split()
    dat = TokenList(dat)
    
    assert dat.get() == "HIERARCHY"
    assert dat.get() == "ROOT"
    
    print("Parsing bone structure")
    
    ws_rex = re.compile(r"\W")
    
    bonelist=[]
    
    name = dat.get()
    name = ws_rex.sub("_",name)
    root = Node(name,None)
    bonelist.append(root)
    root.parse(dat,bonelist)
    
        
    assert dat.get() == "MOTION"
    assert dat.get() == "Frames:"
    numframes=int(dat.get())
    assert dat.get() == "Frame"
    assert dat.get() == "Time:"
    dat.get()   #frame time

    print("Getting frame data")
    #data follows
    #one line per frame
    for i in range(numframes):
        for b in bonelist:
            numitems = len(b.channels)
            tmp=[]
            for k in range(numitems):
                tmp.append( float(dat.get()) )
            b.framedata.append(tmp)
    
    assert dat.peek() == None
    
    print("Computing quaternions")
    compute_quaternions(root)
    
    #sort according to names
    bonelist.sort(key=lambda x:x.name)
    
    #map node names to node indices and find
    #max depth
    maxdepth=0
    namemap={}
    for i in range(len(bonelist)):
        bonelist[i].idx=i
        namemap[bonelist[i].name] = i
        if bonelist[i].depth > maxdepth:
            maxdepth = bonelist[i].depth

    namemap[None]=-1

    #now to write out the data
    
    print("Writing data")
    fp = rigfile
    
    #if ascii:
    #    fp=open(rigfile,"w")
    #    fp.write( "RIG0001\n")
    #else:
    #    fp=open(rigfile,"wb")
    #    fp.write(b"BRIG001\n")
    
    def write(*args):
        if ascii:
            for z in args:
                fp.write(str(z))
                fp.write(" ")
            fp.write("\n")
        else:
            for z in args:
                fp.write(str(z).encode())
                fp.write(b" ")
            fp.write(b"\n")
            
    def padwrite(*args):
        for z in args:
            if(ascii):
                fp.write(str(z))
                fp.write(" ")
            else:
                fp.write(str(z).encode())
                fp.write(b" ")
                
        if ascii:
            fp.write("\n")
        else:
            while (fp.tell()+1) % 4:
                fp.write(b" ")
            fp.write(b"\n")
            assert fp.tell() % 4 == 0


    write("numbones",len(bonelist))
    write("numframes",numframes)
    write("maxdepth",maxdepth)
    
    write("bonenames"," ".join([b.name for b in bonelist]))
    
    write("#bone heads: 4 floats per bone: x,y,z coordinates of head, then parent index (-1 if none)")
    #padwrite("heads")
    
    #for each bone, we write data in format:
    #head x,y,z; parent index
    for bi in range(len(bonelist)):
        b = bonelist[bi]
        p=b.parent
        if p == None:
            pidx=-1
        else:
            pidx = namemap[p.name]
        if ascii:
            write("head",bi,b.head.x,b.head.y,b.head.z,pidx)
        else:
            fp.write(struct.pack("ffff",b.head.x,b.head.y,b.head.z,pidx))
    
    #write("")
    write("#bone tails: 4 floats per bone: x,y,z coordinates of tail; then flag: 1 if bone has end effector; 0 if not")
    #padwrite("tails")
    
    #write tails of bones.
    for bi in range(len(bonelist)):
        b=bonelist[bi]
        if b.endeff != None:
            if ascii:
                write( "tail",bi,b.endeff.x,b.endeff.y,b.endeff.z,1.0 )
            else:
                fp.write( struct.pack("ffff",b.endeff.x,b.endeff.y,b.endeff.z,1.0))
        else:
            #not necessarily correct if child is not connected to this bone
            #So we set w=0.0 to indicate this.
            if ascii:
                write("tail",bi,b.ch[0].head.x,b.ch[0].head.y,b.ch[0].head.z,0.0)
            else:
                fp.write( struct.pack("ffff",b.ch[0].head.x,b.ch[0].head.y,b.ch[0].head.z,0.0))
    #write("")
    
    write("#quaternions: for each frame, for each bone: 4 floats per bone")
    #padwrite("quaternions")
    
    #we write frame-by-frame data:
    #for each frame, write the quaternion of each bone
    for i in range(numframes):
        for bi in range(len(bonelist)):
            b=bonelist[bi]
            if ascii:
                write("quaternion",i,bi,b.qframedata[i].x,
                    b.qframedata[i].y, b.qframedata[i].z,
                    b.qframedata[i].a)
            else:
                fp.write(struct.pack("ffff",b.qframedata[i].x,
                    b.qframedata[i].y, b.qframedata[i].z,
                    b.qframedata[i].a))

    #write("")
    write("#matrices: for each frame, for each bone: 16 floats per bone: COLUMN major order, including translation by headpos")
    #padwrite("matrices")
    
    #we write frame-by-frame data:
    #for each frame, write the matrix of each bone
    #NOTE: These include the translation by headpos!
    for i in range(numframes):
        for bi in range(len(bonelist)):
            b=bonelist[bi]
            a = []
            
            M=b.mframedata[i]
            M = translation(-1*b.head) * M * translation(b.head)
            for col in range(4):
                for row in range(4):
                    a.append(M.M[row][col])
                    
            if ascii:
                write("matrix",i,bi,*a)
            else:
                fp.write(struct.pack("16f",*a))
 
    #write("")
    write("#translations: for each frame: 4 floats per bone")
    #padwrite("translations")
    
    #frame-by-frame data for translations
    for i in range(numframes):
        for bi in range(len(bonelist)):
            b=bonelist[bi]
            if ascii:
                write("translation",i,bi,b.tframedata[i][0],b.tframedata[i][1],b.tframedata[i][2],0)
            else:
                fp.write(struct.pack("ffff",
                    b.tframedata[i][0],b.tframedata[i][1],b.tframedata[i][2],0))
      
    #write("")
    #write("end")
    
    filesize=fp.tell()
    #fp.close()
    
    print("Depth=",maxdepth,"; numbones=",len(bonelist),"; numframes=",numframes,
        "; file size for animation=",filesize//1024,"KB")
        
        
    
        
    B={}
    for i in range(len(bonelist)):
        assert bonelist[i].idx == i
        B[bonelist[i].name] = i
        pn=None
        pi=None
        if bonelist[i].parent:
            pn=bonelist[i].parent.name
            pi=bonelist[i].parent.idx
        print(bonelist[i].name,"is index",bonelist[i].idx,"and has parent",pn,"=",pi)
        
        
        
    return B

#!/usr/bin/env python3

import sys
import re
import os
import cProfile
import parsefbx 
import parsebvh

try:
    import tkinter.filedialog
    import tkinter
except ImportError:
    pass

#Input: A bvh file with the animation data and an fbx file
#with the mesh data.
#Output: An obj file with the animation information

def main():
    if len(sys.argv) > 1:
        infile = sys.argv[1]
    else:
        infile = tkinter.filedialog.askopenfilename()
        
    di = infile.rfind(".")
    if di == -1:
        print("No dot")
        assert 0

    stem = infile[:di]
    
    infbx = stem+".fbx"
    inbvh = stem+".bvh"
    
    
    if not os.access(infbx,os.F_OK):
        print("No fbx file",infbx,"; stopping")
        return
    if not os.access(inbvh,os.F_OK):
        print("No bvh file; stopping")
        
    outobj = stem+".obj"
    outmtl = stem+".mtl"
    
    ofp = open(outobj,"w")
    
    B = parsebvh.bvh2rig(inbvh,ofp)
    
    #always output obj file
    parsefbx.fbx2obj(infbx,ofp,outmtl,B)
    ofp.close()
    print("Wrote obj to",outobj)
   
    

#if sys.argv[1] == "-p":
#    print("Profiling active")
#    del sys.argv[1]
#    cProfile.run("main()")
#else:
main()

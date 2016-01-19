package framework;

import framework.math3d.vec3;
import framework.math3d.mat4;
import java.util.Set;
import java.util.TreeSet;
import static JGL.JGL.*;
import static JSDL.JSDL.*;

public class Main{
    
    public static void main(String[] args){
        Game ProjectSurfboard;
        ProjectSurfboard = new Game();
        ProjectSurfboard.run();
    }//end main
}
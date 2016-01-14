package framework;


import framework.math3d.vec3;
import framework.math3d.mat4;
import framework.math3d.vec4;
import static java.lang.Math.*;
import static framework.math3d.math3d.*;

public class Camera{
    float fov_h = 45;
    float hither = 0.1f;
    float yon = 1000;
    float aspect_ratio = 1.0f;
    float fov_v = fov_h;
    mat4 projMatrix;
    mat4 viewMatrix;
    
    vec4 eye = new vec4(0,0,0,1);
    vec4 U = new vec4(1,0,0,0);
    vec4 V = new vec4(0,1,0,0);
    vec4 W = new vec4(0,0,1,0);
    
    public Camera(){
        compute_proj_matrix();
        compute_view_matrix();
    }

    public void compute_proj_matrix(){
        projMatrix = new mat4( 
            1/tan(toRadians(fov_h)),0,0,0,
            0,1/tan(toRadians(fov_v)),0,0,
            0,0,1+2.0*yon/(hither-yon),-1,
            0,0,2.0*hither*yon/(hither-yon),0);
    }
    
    public void compute_view_matrix(){
        viewMatrix = mul(
            translation(mul(-1.0f,eye)),
            new mat4( U.x,V.x,W.x,0,  
                      U.y,V.y,W.y,0,
                      U.z,V.z,W.z,0,
                      0,0,0,1)
        );
    }
        
    public void draw(Program prog){
        prog.setUniform("projMatrix",projMatrix);
        prog.setUniform("viewMatrix",viewMatrix);
        prog.setUniform("cameraU",this.U.xyz());
        prog.setUniform("cameraV",this.V.xyz());
        prog.setUniform("cameraW",this.W.xyz());
        prog.setUniform("eyePos",this.eye.xyz());
    }
    
    public void turn( float a){
        mat4 M = axisRotation( V, a );
        U = mul(U,M);
        W = mul(W,M);
        compute_view_matrix();
    }
    
    public void pitch(float a){
        mat4 M = axisRotation( U, a );
        V = mul(V,M);
        W = mul(W,M);
        compute_view_matrix();
    }
    
    public void axisTurn( vec3 axis, float a){
        mat4 M = axisRotation( axis, a );
        U = mul(U,M);
        V = mul(V,M);
        W = mul(W,M);
        compute_view_matrix();
    }
        
    public void tilt( float a){
        mat4 M = axisRotation( W, a );
        V = mul(V,M);
        U = mul(U,M);
        compute_view_matrix();
    }
        
    public void walk( float a){
        eye = add( eye , mul(-a , W ) );
        compute_view_matrix();
    }
        
    public void strafe( vec3 v){
        eye = add( eye , mul(v.x , U ) , mul( v.y , V ) ,  mul(-v.z , W ) );
        compute_view_matrix();
    }
        
    public void lookAt( vec3 eye1, vec3 coi1, vec3 up1){
        this.eye = new vec4(eye1,1.0);
        vec4 coi = new vec4(coi1,1.0);
        vec4 up = new vec4(up1,0.0);
        vec4 look = normalize( sub( coi,eye) );
        W = mul(-1.0,look);
        U = cross(look,up);
        V = cross(U,look);
        compute_view_matrix();
    }
}
    

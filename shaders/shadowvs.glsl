//GLSL Vertex Shader
#version 150

in vec3 a_position;
in vec2 a_texcoord;
in vec3 a_normal;

uniform mat4 worldMatrix;
uniform mat4 viewMatrix;
uniform mat4 projMatrix;

out float v_viewPosz;

void main(){
    vec4 p = vec4(a_position.xyz, 1.0);
    p = p * worldMatrix * viewMatrix;

    v_viewPosz = -p.z; //negate because RHS
    gl_Position = p * projMatrix;
}
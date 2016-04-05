#version 150

in vec3 a_position;
in vec2 a_texcoord;
in vec3 a_normal;

out vec2 v_texcoord;
out vec3 v_normal;
out vec3 v_pw;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;


void main(){

    v_texcoord = a_texcoord;

    vec4 p = vec4( a_position.xyz, 1.0 );
    p = p * worldMatrix;
    v_pw = p.xyz;
    p = p * viewMatrix;
    p = p * projMatrix;
    gl_Position = p;
    v_normal = (vec4(a_normal,0.0) * worldMatrix).xyz;
}

//GLSL: Fragment shader
#version 150

in float v_viewPosz;
out vec4 color;

//hither, yon, yon-hither
uniform vec3 hitheryon;

void main()
{
    float z = v_viewPosz;
    z = (z - hitheryon[0]) / hitheryon[2];
    color=vec4(z,z,z,1.0);
}
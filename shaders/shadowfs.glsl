//GLSL: Fragment shader
#version 150

in float v_viewPosz;
out vec4 color;

//hither, yon, yon-hither, scale
uniform vec4 hitheryon;

void main()
{
    float z = v_viewPosz;
    z = (z - hitheryon[0]) / hitheryon[2];
    z *= hitheryon[3];
    color=vec4(z,z,z,1.0);
}
#version 120

in vec3 a_direction;
in vec2 a_texel;

uniform vec3 cameraU;
uniform vec3 cameraV;
uniform vec3 cameraW;

uniform mat4 worldMatrix;
uniform mat4 projMatrix;
uniform mat4 viewMatrix;

void main()
{
        vec2 t = (a_texel - vec2(.5)) * 2.0;
        vec4 p = vec4(0, 0, 0, 1);

        p = p + vec4(t.x*cameraU + t.y*cameraV, 0);

        //p = p * axisRotation(-cameraW, radians(billboardRotation));

        p = p * worldMatrix;

        p = p * viewMatrix * projMatrix;
        gl_Position = p;
}

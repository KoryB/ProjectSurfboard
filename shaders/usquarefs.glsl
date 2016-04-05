#version 150
uniform sampler2D toDisplay;
in vec2 v_texcoord;
out vec4 color;
void main()
{
    color = texture(toDisplay, v_texcoord);
    if (color.a < .05)
    {
        discard;
    }
}
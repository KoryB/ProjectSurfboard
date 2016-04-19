#version 120

in vec2 v_texel;
out vec4 color;

uniform Sampler2D diffuse_texture;

void main()
{
    vec4 textureColor = texture2D(diffuse_texture, v_texel);

    if (textureColor.a <= 0.05)
    {
        discard;
    }

    color = vec4(textureColor.rgb, textureColor.a);
}

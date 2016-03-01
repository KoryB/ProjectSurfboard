#version 150

uniform mat4 lightViewMatrix;
uniform mat4 lightProjMatrix;
uniform vec3 lightHitherYon;
uniform sampler2D shadow_texture;

uniform vec3 lightPos;
uniform vec3 eyePos;
uniform sampler2D diffuse_texture;

in vec2 v_texcoord;
in vec3 v_normal;
in vec3 v_pw;

out vec4 color;

void main(){
    vec4 tc = texture(diffuse_texture,v_texcoord);
    vec3 ambient = vec3(0.05, 0.05, 0.05);
    vec3 N = normalize(v_normal);
    vec3 V = normalize(eyePos - v_pw);
    vec3 L = (lightPos - v_pw);
    float Ldist = length(L);
    L = 1.0/Ldist * L;
    float dp = dot(L,N);
    dp = clamp(dp,0.0,1.0);
    vec3 R = reflect(-L,N);
    float sp = dot(V,R);
    sp *= sign(dp);
    sp = pow(sp,32.0);
    sp = clamp(sp,0.0,1.0);
    color = vec4( ambient*tc.rgb + dp*tc.rgb + vec3(sp) ,tc.a );

    vec4 pe = vec4(v_pw.xyz, 1) * lightViewMatrix;
    pe /= pe.w;
    vec4 ps = pe * lightProjMatrix;
    ps /= ps.w;
    ps += 1;
    ps /= 2; // Map from 0..1 instead of -1..1
    float t = texture(shadow_texture, ps.xy).r;
    t *= lightHitherYon[2];
    t += lightHitherYon[0]; // remap from 0..1 to hither..yon

    if (-pe.z < 0 || -pe.z > t + 0.005)
    {
        color.rgb *= 0.4;
    }
}


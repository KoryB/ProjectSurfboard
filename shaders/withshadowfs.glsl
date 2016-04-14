#version 150
#define EXPONENTIAL_CONSTANT 15.0

uniform mat4 lightViewMatrix;
uniform mat4 lightProjMatrix;
uniform vec4 lightHitherYon;
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

    float r = 16.0;
    vec3 one = vec3(1.0);
    vec3 H = normalize(0.5 * (L+V));
    vec3 sp = vec3(0.95);
    vec3 sqrtk = sqrt(sp);
    vec3 n = (-one - sqrtk) / (sqrtk - one);
    vec3 cos_a = vec3(dot(N,V));
    vec3 cos_b = vec3(dot(N,L));
    vec3 cos_c = vec3(dot(V,H));
    vec3 cos_d = vec3(dot(N,H));
    vec3 q = sqrt(cos_c*cos_c - one + n*n);
    vec3 f1 = q - cos_c;
    vec3 f2 = q + cos_c;
    vec3 f3 = (f2 * cos_c) - one;
    vec3 f4 = (f1 * cos_c) + one;
    vec3 Q1 = f1 / f2;
    Q1 *= Q1;
    vec3 Q2 = f3 / f4;
    Q2 *= Q2;
    vec3 F = vec3(0.5) * Q1 * (one + Q2);
    float cos2d = cos_d[0] * cos_d[0];
    float t = r * (1.0 - 1.0 / cos2d);
    float M = r * exp(t) / (4.0 * cos2d * cos2d);
    float A = clamp(2.0 * cos_d[0] * min(cos_a[0], cos_b[0]) / cos_c[0], 0.0, 1.0);
    vec3 specular = vec3(M) * F * vec3(A) / (cos_a * cos_b * vec3(3.14159265358979323));
    specular *= sign(dp);

    color = vec4( ambient*tc.rgb + dp*tc.rgb + specular ,tc.a );

    vec4 pe = vec4(v_pw.xyz, 1) * lightViewMatrix;
    pe /= pe.w;
    vec4 ps = pe * lightProjMatrix;
    ps /= ps.w;
    ps += 1;
    ps /= 2; // Map from 0..1 instead of -1..1
    float z2 = texture(shadow_texture, ps.xy).r;

    float z1 = -pe.z;
    z1 = (z1-lightHitherYon[0]) / lightHitherYon[2];
    z1 *= lightHitherYon[3];

    //the receiver
    /*vec4 tmp = v_worldPos * light_viewMatrix;
    float z1 = tmp.z;
    z1 = -z1;
    z1 = (z1-light_hitheryon[0]) / light_hitheryon[2];
    z1 *= scale_factor;*/

    /*tmp = tmp * light_projMatrix;
    tmp.xy /= tmp.w;
    tmp.xy = 0.5*(tmp.xy + vec2(1.0));*/

    //the occluder
    /*float z2 = texture2D( shadowbuffer , tmp.xy ).r;
    z2 = abs(z2);*/

    float litpct;
    litpct = exp(EXPONENTIAL_CONSTANT*(z2-z1));
    //litpct = z2 / exp(c*z1);
    litpct = clamp(litpct,0.3,1.0);

    color.rgb *= litpct;

    if (litpct == .3)
    {
       // color.rgb *= 100000000;
    }
}


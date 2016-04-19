#version 150
#define EXPONENTIAL_CONSTANT 15.0
#define OVERLAY_RADIUS_2 .02

uniform mat4 lightViewMatrix;
uniform mat4 lightProjMatrix;
uniform vec4 lightHitherYon;
uniform sampler2D shadow_texture;

uniform vec3 lightPos;
uniform vec3 eyePos;
uniform sampler2D diffuse_texture;

uniform vec4 overlayCenter;

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

    if (overlayCenter.w == 1.0)
    {
        vec3 Q = overlayCenter.xyz - v_pw.xyz;
        if (dot(Q, Q) <= OVERLAY_RADIUS_2)
        {
            color = vec4(1.0);
        }
    }
}


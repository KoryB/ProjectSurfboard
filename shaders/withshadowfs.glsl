#version 150
#define EXPONENTIAL_CONSTANT 15.0
#define OVERLAY_RADIUS_2 .2
#define BASE_NOISESCALE 20

uniform mat4 lightViewMatrix;
uniform mat4 lightProjMatrix;
uniform vec4 lightHitherYon;
uniform sampler2D shadow_texture;

uniform vec3 lightPos;
uniform vec3 eyePos;
uniform sampler2D diffuse_texture;

uniform vec4 overlayCenter;

uniform sampler2D Ptex;
uniform sampler2D Gtex;
uniform float noiseScale;

in vec2 v_texcoord;
in vec3 v_normal;
in vec3 v_pw;

out vec4 color;


//NOISE FUNCTIONS
int P(int idx){
    return int(texelFetch( Ptex, ivec2( idx & 255 , 0 ),0).r);
}
vec4 G(int idx){
    return texelFetch( Gtex, ivec2( idx & 255 , 0 ),0);
}

float gradient(int v){
    return G( P(v) ).x;
}

vec2 gradient(ivec2 v){
    return G( P(v.x + P(v.y)) ).xy;
}

vec3 gradient(ivec3 v){
    return G( P(v.x + P( v.y + P(v.z)))).xyz;
}

vec4 gradient(ivec4 v){
    return G( P(v.x+ P( v.y+ P( v.z + P(v.w)))));
}


float noise(float p){
    int a = int(floor(p));
    int b = int(ceil(p));
    float ga = gradient(a);
    float gb = gradient(b);
    float va = p-a;
    float vb = p-b;
    float da = dot(ga,va);
    float db = dot(gb,vb);
    float pct = fract(p);
    pct = pct*pct*(3.0-2.0*pct);
    float avg = mix(da,db,pct);
    return avg;
}


float noise(vec2 p){
    //  c---d
    //  |   |
    //  a---b
    ivec2 a = ivec2(floor(p));
    ivec2 d = ivec2(ceil(p));
    ivec2 b = ivec2(d.x,a.y);
    ivec2 c = ivec2(a.x,d.y);
    vec2 ga = gradient(a);
    vec2 gb = gradient(b);
    vec2 gc = gradient(c);
    vec2 gd = gradient(d);
    vec2 va = (p-a);
    vec2 vb = (p-b);
    vec2 vc = (p-c);
    vec2 vd = (p-d);
    float da = dot(ga,va);
    float db = dot(gb,vb);
    float dc = dot(gc,vc);
    float dd = dot(gd,vd);
    float pct = fract(p.x);
    pct = pct*pct*(3.0-2.0*pct);
    float avg_ab = mix(da,db,pct);
    float avg_cd = mix(dc,dd,pct);
    pct = fract(p.y);
    pct = pct*pct*(3.0-2.0*pct);
    float avg = mix(avg_ab,avg_cd,pct);
    return avg;
}


float noise(vec3 p){
    //  e____f
    //  /___/|
    //a|   b||h
    // |____|
    // c    d
    ivec3 b = ivec3(ceil(p));
    ivec3 g = ivec3(floor(p));

    ivec3 a = ivec3(g.x,b.y,b.z);
    ivec3 c = ivec3(g.x,g.y,b.z);
    ivec3 d = ivec3(b.x,g.y,b.z);
    ivec3 e = ivec3(g.x,b.y,g.z);
    ivec3 f = ivec3(b.x,b.y,g.z);
    ivec3 h = ivec3(b.x,g.y,g.z);

    vec3 ga = gradient(a);
    vec3 gb = gradient(b);
    vec3 gc = gradient(c);
    vec3 gd = gradient(d);
    vec3 ge = gradient(e);
    vec3 gf = gradient(f);
    vec3 gg = gradient(g);
    vec3 gh = gradient(h);
    vec3 va = p-a;
    vec3 vb = p-b;
    vec3 vc = p-c;
    vec3 vd = p-d;
    vec3 ve = p-e;
    vec3 vf = p-f;
    vec3 vg = p-g;
    vec3 vh = p-h;
    float da = dot(ga,va);
    float db = dot(gb,vb);
    float dc = dot(gc,vc);
    float dd = dot(gd,vd);
    float de = dot(ge,ve);
    float df = dot(gf,vf);
    float dg = dot(gg,vg);
    float dh = dot(gh,vh);
    //  e____f
    //  /___/|
    //a|   b||h
    // |____|
    // c    d
    float pct = fract(p.x);
    pct = pct*pct*(3.0-2.0*pct);
    float avg_ab = mix(da,db,pct);
    float avg_cd = mix(dc,dd,pct);
    float avg_ef = mix(de,df,pct);
    float avg_gh = mix(dg,dh,pct);
    pct = fract(p.y);
    pct = pct*pct*(3.0-2.0*pct);
    float avg_front = mix(avg_cd,avg_ab,pct);
    float avg_back = mix(avg_gh,avg_ef,pct);
    pct = fract(p.z);
    pct = pct*pct*(3.0-2.0*pct);
    float avg = mix( avg_back, avg_front, pct );
    return avg;
}
//END NOISE

float getOverlayNoise()
{
    return noise(BASE_NOISESCALE*v_pw.xz) + 0.5*noise(BASE_NOISESCALE*2*v_pw.xz)  + 0.25*noise(BASE_NOISESCALE*4*v_pw.xz);
}


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

    float litpct;
    litpct = exp(EXPONENTIAL_CONSTANT*(z2-z1));
    //litpct = z2 / exp(c*z1);
    litpct = clamp(litpct,0.3,1.0);

    if (overlayCenter.w == 1.0)
    {
        vec3 Q = overlayCenter.xyz - v_pw.xyz;
        float d = length(Q);
        float val = pow((OVERLAY_RADIUS_2 - d) / OVERLAY_RADIUS_2, .5);
        if (d <= OVERLAY_RADIUS_2)
        {
            color.rgb = vec3((getOverlayNoise() + 1) / 2);
        }
    }

    color.rgb *= litpct;
}


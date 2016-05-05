//GLSL Vertex Shader
#version 150

in vec3 a_position;
in vec2 a_texcoord;
in vec3 a_normal;
in vec4 a_weight;
in vec4 a_boneidx;

uniform mat4 worldMatrix;
uniform mat4 viewMatrix;
uniform mat4 projMatrix;

out float v_viewPosz;
uniform float curframe;

uniform sampler2D bonetex;
uniform vec2 bonetex_size;

uniform sampler2D quattex;
uniform vec2 quattex_size;

vec4 qmul(vec4 q1, vec4 q2)
{
    vec3 v1 = q1.xyz;
    vec3 v2 = q2.xyz;
    return vec4(
        q1.w*v2 + q2.w*v1 + cross(v1,v2),
        q1.w*q2.w - dot(v1,v2)
    );
}

vec4 slerp(vec4 q1, vec4 q2, float t)
{
    //do slerp later, for now just nlerp
    float dp = dot(q1, q2);
    if (dp <= 0)
    {
        q1 = vec4(-q1.xyz,q1.w);
    }
    return normalize(mix(q1, q2, t));
}

vec4 getBone(float idx){
    return texture2D(
        bonetex,
        vec2( (idx+0.5)*bonetex_size[0],0.0)
    );
}

vec4 getQuaternion(float boneidx, float frame){
    vec2 t = vec2( (boneidx + 0.5) * quattex_size[0],
        (frame+0.5)*quattex_size[1] );
    return texture2D( quattex, t );
}

vec4 computePos(vec4 p, float boneidx, float frame){
     float ff = floor(frame);
     float fc = ceil(frame);
     float pct = fract(frame);


     while (boneidx != -1.0f)
     {
         vec4 bonedata = getBone(boneidx);
         vec4 q1 = getQuaternion(boneidx,ff);
         vec4 q2 = getQuaternion(boneidx,fc);
         //we must write slerp() and qmul
         vec4 q = slerp(q1,q2,pct);
         vec4 q_ = vec4(-q.xyz,q.w);
         //if p is a normal vector, don't translate
         p.xyz -= bonedata.xyz*p.w;
         vec4 p_ = vec4(p.xyz,0.0);
         p.xyz = (qmul(qmul(q,p_),q_)).xyz;
         p.xyz += bonedata.xyz*p.w;
         boneidx = bonedata[3];
     }
     return p;
 }

vec4 averagePosition(vec4 p, float frame)
{
    vec4 p0 = computePos(p, a_boneidx[0], frame);
    vec4 p1 = computePos(p, a_boneidx[1], frame);
    vec4 p2 = computePos(p, a_boneidx[2], frame);
    vec4 p3 = computePos(p, a_boneidx[3], frame);

    return a_weight[0]*p0 + a_weight[1]*p1 + a_weight[2]*p2 + a_weight[3]*p3;
}

vec4 interpolatePosition(vec4 p, float frame)
{
    return averagePosition(p, frame);
}

void main(){
    vec4 p = vec4(a_position.xyz, 1.0);
    p = interpolatePosition(p, curframe);
    p = p * worldMatrix * viewMatrix;

    v_viewPosz = -p.z; //negate because RHS
    gl_Position = p * projMatrix;
}
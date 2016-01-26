package framework;


import framework.math3d.*;
import framework.math3d.primitives.Ray;

import static java.lang.Math.*;
import static framework.math3d.math3d.*;

//TODO: Remove collisionObject as parent object

public class Camera
{
    float fov_h = 45;
    float hither = 0.1f;
    float yon = 1000;
    float aspect_ratio = 1.0f;
    float fov_v = fov_h * aspect_ratio;
    mat4 projMatrix;
    mat4 viewMatrix;

    float mRight = 4.0f;
    //    float mLeft = -1.0f;
    float mTop = 4.0f;
    //    float mBottom = -1.0f;
    float mNear = 0.1f;
    float mFar = 8.1f;

    vec4 mEye;

    vec4 U = new vec4(1, 0, 0, 0);
    vec4 V = new vec4(0, 1, 0, 0);
    vec4 W = new vec4(0, 0, 1, 0);

    vec4 mViewOrigin;
    //TODO: Give these better defaults
    float mHalfViewHeight = 0.0f;
    float mHalfViewWidth = 0.0f;
    float mVirtualHeightRatio = 0.0f;
    float mVirtualWidthRatio = 0.0f;

    public Camera()
    {
        mEye = new vec4(0, 0, 0, 1);

        compute_proj_matrix();
        compute_view_matrix();
    }

    public void compute_projp_matrix()
    {
        projMatrix = new mat4(
                1 / tan(toRadians(fov_h)), 0, 0, 0,
                0, 1 / tan(toRadians(fov_v)), 0, 0,
                0, 0, 1 + 2.0 * yon / (hither - yon), -1,
                0, 0, 2.0 * hither * yon / (hither - yon), 0);
    }

    public void compute_proj_matrix()
    {
        projMatrix = new mat4(
                1 / mRight, 0, 0, 0,
                0, 1 / mTop, 0, 0,
                0, 0, -2 / (mFar - mNear), 0,
                0, 0, -(mFar + mNear) / (mFar - mNear), 1
        );
    }

    public void compute_view_matrix()
    {
        viewMatrix = mul(
                translation(mul(-1.0f, mEye)),
                new mat4(U.x, V.x, W.x, 0,
                        U.y, V.y, W.y, 0,
                        U.z, V.z, W.z, 0,
                        0, 0, 0, 1)
        );

        compute_view_origin();
    }

    private void compute_view_origin()
    {
        mViewOrigin = add(mEye, W.mul(-mNear), V.mul(mHalfViewHeight), U.mul(-mHalfViewWidth));

        vec4 topleft = calculatePixelPosition(new vec2());
        vec4 bottomleft = calculatePixelPosition(new vec2(0, Util.WINDOW_HEIGHT));
    }

    public void draw(Program prog)
    {
        prog.setUniform("projMatrix", projMatrix);
        prog.setUniform("viewMatrix", viewMatrix);
        prog.setUniform("cameraU", this.U.xyz());
        prog.setUniform("cameraV", this.V.xyz());
        prog.setUniform("cameraW", this.W.xyz());
        prog.setUniform("eyePos", this.mEye.xyz());
    }

    public void turn(float a)
    {
        mat4 M = axisRotation(V, a);
        U = mul(U, M);
        W = mul(W, M);
        compute_view_matrix();
    }

    public void pitch(float a)
    {
        mat4 M = axisRotation(U, a);
        V = mul(V, M);
        W = mul(W, M);
        compute_view_matrix();
    }

    public void axisTurn(vec3 axis, float a)
    {
        mat4 M = axisRotation(axis, a);
        U = mul(U, M);
        V = mul(V, M);
        W = mul(W, M);
        compute_view_matrix();
    }

    public void tilt(float a)
    {
        mat4 M = axisRotation(W, a);
        V = mul(V, M);
        U = mul(U, M);
        compute_view_matrix();
    }

    public void walk(float a)
    {
        mEye = mEye.add(mul(-a, W));

        compute_view_matrix();
    }

    public void strafe(vec3 v)
    {
        mEye = add(mEye, mul(v.x, U), mul(v.y, V), mul(-v.z, W));

        compute_view_matrix();
    }

    public void lookAt(vec3 eye1, vec3 coi1, vec3 up1)
    {
        vec3 delta = eye1.sub(this.mEye.xyz());
        this.mEye = new vec4(eye1, 1.0);
        vec4 coi = new vec4(coi1, 1.0);
        vec4 up = new vec4(up1, 0.0);
        vec4 look = normalize(sub(coi, mEye));
        W = mul(-1.0, look);
        U = normalize(cross(look, up));
        V = normalize(cross(U, look));

        mHalfViewHeight = mTop;
        mHalfViewWidth = mRight;
        mVirtualHeightRatio = mHalfViewHeight / Util.WINDOW_HALF_HEIGHT;
        mVirtualWidthRatio = mHalfViewWidth / Util.WINDOW_HALF_WIDTH;

        compute_view_matrix();
    }

    public vec4 calculatePixelPosition(vec2 pixel)
    {
        return add(mViewOrigin, U.mul(pixel.x*mVirtualWidthRatio), V.mul(-mVirtualHeightRatio*pixel.y));
    }

    public Ray getRay(vec2 screenCoord)
    {
        return new Ray(calculatePixelPosition(screenCoord), W.neg());
    }
}
    

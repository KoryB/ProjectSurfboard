package framework.math3d.primitives;

import framework.Floor;
import framework.math3d.math3d;
import framework.math3d.vec2;
import framework.math3d.vec4;

/**
 * Created by kory on 1/22/16.
 */
public class BoundedPlane extends Plane
{
    protected vec4 mBottomLeft;
    private vec4 mTopLeft;
    private vec4 mTopRight;
    protected vec4 mBottomRight;

    private vec2 mExtents;
    private vec2 mHalfExtents;

    protected vec4 mU;
    protected vec4 mV;
    protected vec4 mUExtents;
    protected vec4 mVExtents;

    // TODO: Add valid rectangle test
    public BoundedPlane(vec4 bottomLeft, vec4 topLeft, vec4 topRight, vec4 bottomRight)
    {
        super(0.0f, new vec4());

        mBottomLeft = bottomLeft;
        mTopLeft = topLeft;
        mTopRight = topRight;
        mBottomRight = bottomRight;

        mUExtents = mBottomRight.sub(mBottomLeft);
        mVExtents = mTopLeft.sub(mBottomLeft);

        mExtents = new vec2(math3d.length(mUExtents), math3d.length(mVExtents));
        mHalfExtents = mExtents.mul(0.5f);

        mU = mUExtents.mul(1.0f / mExtents.x);
        mV = mVExtents.mul(1.0f / mExtents.y);

        mNormal = math3d.cross(mU, mV);
        mDValue = mNormal.dot(mTopRight);
    }

    @Override
    public void setDValue(float dValue)
    {
        //This is unsupported for boundedPlanes
        throw new RuntimeException("Direct DValue setting is not supported for BoundingPlanes");
    }

    public vec2 getExtents()
    {
        return mExtents;
    }

    public void setExtents(vec2 mExtents)
    {
        this.mExtents = mExtents;
    }

    public vec2 getHalfExtents()
    {
        return mHalfExtents;
    }

    public void setHalfExtents(vec2 mHalfExtents)
    {
        this.mHalfExtents = mHalfExtents;
        this.mExtents = mHalfExtents.mul(2.0f);
    }

    public vec4 getOrigin()
    {
        return mBottomLeft;
    }

    @Override
    public void translate(vec4 amount)
    {
        mBottomLeft = mBottomLeft.add(amount);
        mTopLeft = mTopLeft.add(amount);
        mTopRight = mTopRight.add(amount);
        mBottomRight = mBottomRight.add(amount);

        mDValue = mNormal.dot(mTopRight);
    }

    @Override
    public void printInfo()
    {
        System.out.println("Bounded Plane Info: ");
        System.out.println("D Value: " + mDValue);
        System.out.println("Normal: " + mNormal);
        System.out.println("Corners: ");
        System.out.println("\tBottom Left: " + mBottomLeft);
        System.out.println("\tTop Left: " + mTopLeft);
        System.out.println("\tTop Right: " + mTopRight);
        System.out.println("\tBottom Right: " + mBottomRight);
        System.out.println("Extents: " + mExtents);
    }
}

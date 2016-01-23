package framework.math3d.primitives;

import framework.Floor;
import framework.math3d.vec2;
import framework.math3d.vec4;

/**
 * Created by kory on 1/22/16.
 */
public class BoundedPlane extends Plane
{
    private vec4 mCenter;
    private vec2 mExtents;
    private vec2 mHalfExtents;

    public BoundedPlane(vec4 normal, vec4 center, vec2 extents)
    {
        super(Float.MIN_VALUE, normal);

        mDValue = mNormal.dot(center);

        mCenter = (vec4) center.clone();
        mExtents = (vec2) extents.clone();
        mHalfExtents = mExtents.mul(0.5f);
    }

    @Override
    public void setDValue(float dValue)
    {
        //This is unsupported for boundedPlanes
        throw new RuntimeException("Direct DValue setting is not supported for BoundingPlanes");
    }

    public vec4 getCenter()
    {
        return mCenter;
    }

    public void setCenter(vec4 mCenter)
    {
        this.mCenter = mCenter;
        this.mDValue = mNormal.dot(mCenter);
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
    }

    @Override
    public void translate(vec4 amount)
    {
        //This method can in face translate the bounding rect on the plane
        super.translate(amount);

        mCenter = mCenter.add(amount);
    }

    @Override
    public void printInfo()
    {
        System.out.println("Bounded Plane Info: ");
        System.out.println("D Value: " + mDValue);
        System.out.println("Normal: " + mNormal);
        System.out.println("Center: " + mCenter);
        System.out.println("Extents: " + mExtents);
    }
}

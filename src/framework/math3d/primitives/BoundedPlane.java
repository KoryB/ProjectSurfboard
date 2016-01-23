package framework.math3d.primitives;

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

    public BoundedPlane(float dValue, vec4 normal, vec4 center, vec2 extents)
    {
        super(dValue, normal);
        mCenter = (vec4) mCenter.clone();
        mExtents = (vec2) mExtents.clone();
        mHalfExtents = mExtents.mul(0.5f);
    }

    public vec4 getCenter()
    {
        return mCenter;
    }

    public void setCenter(vec4 mCenter)
    {
        this.mCenter = mCenter;
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

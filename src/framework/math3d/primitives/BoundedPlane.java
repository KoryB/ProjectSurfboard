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

    public BoundedPlane(float dValue, vec4 normal, vec4 mCenter, vec2 mExtents)
    {
        super(dValue, normal);
        mCenter = (vec4) mCenter.clone();
        mExtents = (vec2) mExtents.clone();
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

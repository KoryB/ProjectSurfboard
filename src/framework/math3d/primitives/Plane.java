package framework.math3d.primitives;

import framework.math3d.vec4;

import framework.math3d.math3d;

/**
 * Created by kory on 1/21/16.
 */

//dot(P, mNormal) + mDValue = 0; for all points P on the plane.
public class Plane implements Primitive
{
    protected float mDValue;
    protected vec4 mNormal;

    public Plane(float dValue, vec4 normal)
    {
        mDValue = dValue;
        mNormal = math3d.normalize(normal);
    }

    @Override
    public void translate(vec4 amount)
    {
        //This can only modify the D value of the plane, as the plane is infinite,
        mDValue += mNormal.dot(amount);
    }

    @Override
    public void printInfo()
    {
        System.out.println("Plane Info: ");
        System.out.println("D Value: " + mDValue);
        System.out.println("Normal: " + mNormal);
    }
}

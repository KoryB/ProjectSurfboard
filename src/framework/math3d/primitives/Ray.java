package framework.math3d.primitives;

import framework.math3d.math3d;
import framework.math3d.vec4;

/**
 * Created by kory on 1/22/16.
 */

//P = P0 + tV
public class Ray implements Primitive
{
    private vec4 mOrigin;
    private vec4 mDirection;

    public Ray(vec4 origin, vec4 direction)
    {
        mOrigin = (vec4) origin.clone();
        mDirection = math3d.normalize(direction);
    }

    public vec4 getOrigin()
    {
        return mOrigin;
    }

    public void setOrigin(vec4 mOrigin)
    {
        this.mOrigin = mOrigin;
    }

    public vec4 getDirection()
    {
        return mDirection;
    }

    public void setDirection(vec4 mDirection)
    {
        this.mDirection = mDirection;
    }

    public vec4 getPoint(float t)
    {
        return mOrigin.add(mDirection.mul(t));
    }

    @Override
    public void translate(vec4 amount)
    {
        mOrigin = mOrigin.add(amount);
    }

    @Override
    public void printInfo()
    {
        System.out.println("Ray Info: ");
        System.out.println("Origin: " + mOrigin);
        System.out.println("Direction: " + mDirection);
    }
}

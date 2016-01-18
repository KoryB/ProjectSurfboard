package framework.math3d.primitives;

import framework.Exceptions.TypeMismatchError;
import framework.math3d.vec4;

import java.awt.*;

/**
 * Created by kory on 1/18/16.
 */
public class AABB
{
    private vec4 mMin;
    private vec4 mMax;
    private vec4 mCenter;
    private vec4 mExtents;

    public AABB(vec4 vecA, vec4 vecB, AABBType typeA, AABBType typeB)
    {
        if (typeA == typeB)
        {
            throw new TypeMismatchError("typeA and typeB must be different types!");
        }
        
        switch (typeA)
        {
            case MIN:
                mMin = (vec4) vecA.clone();
                break;

            case MAX:
                mMax = (vec4) vecA.clone();
                break;

            case CENTER:
                mCenter = (vec4) vecA.clone();
                break;

            case EXTENTS:
                mExtents = (vec4) vecA.clone();
                break;
        }

        switch (typeB)
        {
            case MIN:
                mMin = (vec4) vecB.clone();
                break;

            case MAX:
                mMax = (vec4) vecB.clone();
                break;

            case CENTER:
                mCenter = (vec4) vecB.clone();
                break;

            case EXTENTS:
                mExtents = (vec4) vecB.clone();
                break;
        }

        if (mMin != null)
        {
            if (mMax != null)
            {
                mExtents = mMax.sub(mMin);
                mCenter = (mMax.add(mMin)).mul(0.5f);
            }
            else if (mCenter != null)
            {
                mExtents = mCenter.sub(mMin).mul(2.0f);
                mMax = mMin.add(mExtents);
            }
            else //mExtents must not equal null
            {
                mMax = mMin.add(mExtents);
                mCenter = (mMax.add(mMin)).mul(0.5f);
            }
        }
        else if (mMax != null)
        {
            if (mCenter != null)
            {
                mExtents = mMax.sub(mCenter);
                mMin = mMax.sub(mExtents);
            }
            else //mExtents must not equal null
            {
                mMin = mMax.sub(mExtents);
                mCenter = (mMax.add(mMin)).mul(0.5f);
            }
        }
        else //since mMin and mMax are both null, mExtents and mCenter must be non-null
        {
            vec4 halfExtents = mExtents.mul(0.5f);

            mMin = mCenter.sub(halfExtents);
            mMax = mCenter.add(halfExtents);
        }
    }

    public vec4 getMin()
    {
        return mMin;
    }

    public void setMin(vec4 mMin)
    {
        this.mMin = mMin;
    }

    public vec4 getMax()
    {
        return mMax;
    }

    public void setMax(vec4 mMax)
    {
        this.mMax = mMax;
    }

    public vec4 getCenter()
    {
        return mCenter;
    }

    public void setCenter(vec4 mCenter)
    {
        this.mCenter = mCenter;
    }

    public vec4 getExtents()
    {
        return mExtents;
    }

    public void setExtents(vec4 mExtents)
    {
        this.mExtents = mExtents;
    }
}

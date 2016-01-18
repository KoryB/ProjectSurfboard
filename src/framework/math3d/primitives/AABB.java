package framework.math3d.primitives;

import framework.exceptions.TypeMismatchError;
import framework.math3d.vec4;

public class AABB implements Primitive
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

    public void setMin(vec4 min)
    {
        mMin = min;

        mMax = mMin.add(mExtents);
        mCenter = mMin.add(mMax).mul(0.5f);
    }

    public vec4 getMax()
    {
        return mMax;
    }

    public void setMax(vec4 max)
    {
        mMax = max;

        mMin = mMax.sub(mExtents);
        mCenter = mMin.add(mMax).mul(0.5f);
    }

    public vec4 getCenter()
    {
        return mCenter;
    }

    public void setCenter(vec4 center)
    {
        mCenter = center;

        vec4 halfExtents = mExtents.mul(0.5f);
        mMin = mCenter.sub(halfExtents);
        mMax = mCenter.add(halfExtents);
    }

    public void translate(vec4 amount)
    {
        setMin(mMin.add(amount));
    }

    public float getLeft()
    {
        return mMin.x;
    }

    public float getRight()
    {
        return mMax.x;
    }

    public float getBottom()
    {
        return mMin.y;
    }

    public float getTop()
    {
        return mMax.y;
    }

    public float getBack()
    {
        return mMin.z;
    }

    public float getFront()
    {
        return mMax.z;
    }

    public vec4 getExtents()
    {
        return mExtents;
    }
}

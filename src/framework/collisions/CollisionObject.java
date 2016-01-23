package framework.collisions;

import framework.math3d.primitives.Primitive;
import framework.math3d.vec4;

public abstract class CollisionObject
{
    protected boolean mIsStatic = false; // tells whether the object should be pushed away if in a collision
    protected vec4 mPosition = new vec4();

    protected CollisionType mCollisionType;
    protected Primitive mCollisionPrimitive;

    public CollisionObject()
    {

    }

    public vec4 getPosition()
    {
        return mPosition;
    }

    public void setPosition(vec4 position)
    {
        this.mPosition = position;
    }

    public CollisionType getCollisionType()
    {
        return mCollisionType;
    }

    public Primitive getCollisionPrimitive()
    {
        return mCollisionPrimitive;
    }

    public void move(vec4 amount)
    {
        mPosition = mPosition.add(amount);
        mCollisionPrimitive.translate(amount);
    }
}

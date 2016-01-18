package framework.collisions;

import framework.math3d.vec4;

/**
 * Created by kory on 1/18/16.
 */
public class CollisionObject
{
    protected boolean mIsStatic = false; // tells whether the object should be pushed away if in a collision
    protected vec4 mPosition = new vec4();

    public CollisionObject()
    {

    }
}

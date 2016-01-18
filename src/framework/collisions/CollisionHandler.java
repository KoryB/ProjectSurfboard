package framework.collisions;

import framework.exceptions.TypeMismatchError;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.IntersectionHandler;
import framework.math3d.vec4;

public class CollisionHandler
{
    public static void pushApartAABB(CollisionObject objectA, CollisionObject objectB)
    {
        if (objectA.getCollisionType() != CollisionType.AABB || objectB.getCollisionType() != CollisionType.AABB)
        {
            throw new TypeMismatchError("objectA and objectB must both be of collision type CollisionType.AABB");
        }
        if (objectA.mIsStatic && objectB.mIsStatic)
        {
            //TODO: Should we throw an exception here?

            return;
        }

        AABB boxA = (AABB) objectA.getCollisionPrimitive();
        AABB boxB = (AABB) objectB.getCollisionPrimitive();

        // TODO: Optimize this section a bit more
        if (IntersectionHandler.AABBAABBIntersection(boxA, boxB))
        {
            float rightleft = boxA.getRight() - boxB.getLeft();
            float leftright = boxB.getRight() - boxA.getLeft();
            float topbottom = boxA.getTop() - boxB.getBottom();
            float bottomtop = boxB.getTop() - boxA.getBottom();
            float frontback = boxA.getFront() - boxB.getBack();
            float backfront = boxB.getFront() - boxA.getBack();


            if (rightleft >= leftright && rightleft >= topbottom && rightleft >= bottomtop && rightleft >= frontback && rightleft >= backfront)
            {

            }
        }

    }
}

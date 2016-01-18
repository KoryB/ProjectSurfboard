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
            // Calculate the smallest delta and push the boxes away that much along that axis
            float rightleft = boxA.getRight() - boxB.getLeft();
            float leftright = boxB.getRight() - boxA.getLeft();
            float topbottom = boxA.getTop() - boxB.getBottom();
            float bottomtop = boxB.getTop() - boxA.getBottom();
            float frontback = boxA.getFront() - boxB.getBack();
            float backfront = boxB.getFront() - boxA.getBack();
            vec4 delta;
            
            if (rightleft <= leftright && rightleft <= topbottom && rightleft <= bottomtop && rightleft <= frontback && rightleft <= backfront)
            {
                delta = new vec4(-rightleft, 0.0f, 0.0f, 0.0f);
            }
            else if (leftright <= rightleft && leftright <= topbottom && leftright <= bottomtop && leftright <= frontback && leftright <= backfront)
            {
                delta = new vec4(leftright, 0.0f, 0.0f, 0.0f);
            }
            else if (topbottom <= rightleft && topbottom <= leftright && topbottom <= bottomtop && topbottom <= frontback && topbottom <= backfront)
            {
                delta = new vec4(-topbottom, 0.0f, 0.0f, 0.0f);
            }
            else if (bottomtop <= rightleft && bottomtop <= leftright && bottomtop <= topbottom && bottomtop <= frontback && bottomtop <= backfront)
            {
                delta = new vec4(bottomtop, 0.0f, 0.0f, 0.0f);
            }
            else if (frontback <= rightleft && frontback <= leftright && frontback <= frontback && frontback <= backfront && frontback <= backfront)
            {
                delta = new vec4(-frontback, 0.0f, 0.0f, 0.0f);
            }
            else // backfront must be the smallest
            {
                delta = new vec4(backfront, 0.0f, 0.0f, 0.0f);
            }

            if (objectA.mIsStatic)
            {
                objectB.move(delta.mul(-1.0f));
            }
            else if (objectB.mIsStatic)
            {
                objectA.move(delta);
            }
            else //neither static
            {
                objectA.move(delta.mul(0.5f));
                objectB.move(delta.mul(-0.5f));
            }
        }

    }
}

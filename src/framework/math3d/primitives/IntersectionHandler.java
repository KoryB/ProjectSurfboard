package framework.math3d.primitives;

public class IntersectionHandler
{

    // Algorithm for intersection found here: https://developer.mozilla.org/en-US/docs/Games/Techniques/3D_collision_detection
    //TODO: Find a better name for this
    public static boolean AABBAABBIntersection(AABB boxA, AABB boxB)
    {
        return
                boxA.getTop() >= boxB.getBottom() && boxB.getTop() >= boxA.getBottom() &&
                boxA.getRight() >= boxB.getLeft() && boxB.getRight() >= boxA.getLeft() &&
                boxA.getFront() >= boxB.getBack() && boxB.getFront() >= boxA.getBack();
    }
}

package framework.math3d.primitives;

public class IntersectionHandler
{
    // Algorithm for intersection found here: https://developer.mozilla.org/en-US/docs/Games/Techniques/3D_collision_detection
    //TODO: Find a better name for this
    public static boolean AABBAABBIntersection(AABB boxA, AABB boxB)
    {
        return
                (boxA.getMin().x <= boxB.getMax().x && boxA.getMax().x >= boxB.getMin().x) &&
                (boxA.getMin().y <= boxB.getMax().y && boxA.getMax().y >= boxB.getMin().y) &&
                (boxA.getMin().z <= boxB.getMax().z && boxA.getMax().z >= boxB.getMin().z);
    }
}

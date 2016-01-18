package framework.math3d.primitives;

public class IntersectionHandler
{
    public static boolean AABBAABBIntersection(AABB boxA, AABB boxB)
    {
        return
                (boxA.getMin().x <= boxB.getMax().x && boxA.getMax().x >= boxB.getMin().x) &&
                (boxA.getMin().y <= boxB.getMax().y && boxA.getMax().y >= boxB.getMin().y) &&
                (boxA.getMin().z <= boxB.getMax().z && boxA.getMax().z >= boxB.getMin().z);
    }
}

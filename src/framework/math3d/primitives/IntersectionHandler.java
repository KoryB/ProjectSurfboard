package framework.math3d.primitives;

import framework.Util;
import framework.math3d.math3d;
import framework.math3d.vec2;
import framework.math3d.vec4;

public class IntersectionHandler
{
    //TODO: Make a primitive intersect function that tests any two primitives.
    /*TODO: Make a ray-primitive intersect function that test a ray against any primitive.
     *   This needs to be done because ray intersections should return the t value of an intersection, null otherwise.
     */

    // Algorithm for intersection found here: https://developer.mozilla.org/en-US/docs/Games/Techniques/3D_collision_detection
    //TODO: Find a better name for this
    public static boolean AABBAABBIntersection(AABB boxA, AABB boxB)
    {
        return
                boxA.getTop() >= boxB.getBottom() && boxB.getTop() >= boxA.getBottom() &&
                boxA.getRight() >= boxB.getLeft() && boxB.getRight() >= boxA.getLeft() &&
                boxA.getFront() >= boxB.getBack() && boxB.getFront() >= boxA.getBack();
    }

    /**
     *
     * @param ray
     * @param plane
     * @param frontOnly     boolean value if the ray should only intersect the front of the plane (the direction of the ray is opposite the normal.
     * @return              the t value of the ray, if there is no intersection returns -1.0f
     */
    public static Float RayPlaneIntersection(Ray ray, Plane plane, boolean frontOnly)
    {
        float dotRayPlane = ray.getDirection().dot(plane.getNormal());

        if (Math.abs(dotRayPlane) < Util.EPSILON)    // if roughly parallel
        {
            return null;
        }

        if (dotRayPlane > 0.0f && frontOnly)    // if dotRayPlane < 0.0f, it is facing the front of the plane
        {
            return null;
        }

        return (plane.getDValue() - math3d.dot(ray.getOrigin(), plane.getNormal())) / dotRayPlane;  // see page 724 in our 1803 book for details
    }

    // TODO: Should this have a different name?
    /**
     *
     * @param ray
     * @param boundedPlane
     * @param frontOnly     boolean value if the ray should only intersect the front of the plane (the direction of the ray is opposite the normal.
     * @return      the t value of the ray, if there is no intersection returns -1.0f
     */
    public static Float RayPlaneIntersection(Ray ray, BoundedPlane boundedPlane, boolean frontOnly)
    {
        Float t = RayPlaneIntersection(ray, (Plane) boundedPlane, frontOnly);

        if (t == null)
        {
            return null;
        }

        vec4 point = ray.getPoint(t);
        vec4 toPoint = point.sub(boundedPlane.getOrigin());
        float Pu = toPoint.dot(boundedPlane.mU);
        float Pv = toPoint.dot(boundedPlane.mV);

        System.out.println("Checking point: " + point);
        System.out.println("Pu: " + Pu);
        System.out.println("Pv: " + Pv);


        if (0.0f <= Pu && Pu <= boundedPlane.getExtents().x && 0.0f <= Pv && Pv <= boundedPlane.getExtents().y)
        {
            return t;
        }
        else
        {
            return null;
        }
    }
}

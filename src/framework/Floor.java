package framework;

import framework.collisions.CollisionObject;
import framework.collisions.CollisionType;
import framework.math3d.math3d;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.AABBType;
import framework.math3d.primitives.BoundedPlane;
import framework.math3d.vec2;
import framework.math3d.vec4;

/**
 * Created by kory on 1/22/16.
 */
public class Floor extends CollisionObject
{
    private static Mesh MESH;

    public Floor(vec4 position)
    {
        mPosition = (vec4) position.clone();
        mCollisionType = CollisionType.PLANE;
        mIsStatic = true;

        mCollisionPrimitive = new BoundedPlane(new vec4(-0.25, 0.0, 0.5, 1.0), new vec4(-0.25, 0.0, -0.5, 1.0), new vec4(0.25, 0.0, -0.5, 1.0), new vec4(0.25, 0.0, 0.5, 1.0));
        mCollisionPrimitive.translate(new vec4(mPosition.xyz(), 0.0f));

        if (MESH == null)
        {
            MESH = new Mesh("assets/testWall.obj.mesh");
        }
    }

    public void update(float elapsed)
    {

    }

    public void draw(Program program)
    {
        // For now just draw a test wall below the actual tile
        program.setUniform("worldMatrix", math3d.translation(mPosition.sub(new vec4(0.0f, 3.0f, 0.0f, 0.0f))));
        MESH.draw(program);
    }
}

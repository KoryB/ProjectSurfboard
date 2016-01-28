package framework;

import framework.collisions.CollisionHandler;
import framework.collisions.CollisionObject;
import framework.collisions.CollisionType;
import framework.math3d.mat4;
import framework.math3d.vec2;
import framework.math3d.vec4;
import framework.math3d.math3d;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.AABBType;

public class Wall extends CollisionObject
{
    private static Mesh MESH;

    private vec4 mExtents = new vec4(1.0f, 3.0f, 1.0f, 0.0f);

    public Wall(vec4 position, vec2 size)
    {
        mPosition = (vec4) position.clone();
        mCollisionType = CollisionType.AABB;
        mIsStatic = true;

        mExtents.x *= size.x;
        mExtents.z *= size.y;

        mCollisionPrimitive = new AABB(mExtents, mPosition.add(new vec4(0.0f, mExtents.y / 2.0f, 0.0f, 0.0f)), AABBType.EXTENTS, AABBType.CENTER);

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
//        mCollisionPrimitive.printInfo();
        program.setUniform("worldMatrix", math3d.scaling(mExtents.x, 1.0f, mExtents.z).mul(math3d.translation(mPosition)));
        MESH.draw(program);
    }
}

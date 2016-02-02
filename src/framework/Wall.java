package framework;

import framework.collisions.CollisionObject;
import framework.collisions.CollisionType;
import framework.drawing.Drawable;
import framework.drawing.Mesh;
import framework.drawing.Program;
import framework.math3d.vec4;
import framework.math3d.math3d;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.AABBType;

public class Wall extends CollisionObject implements Drawable
{
    private static Mesh MESH;

    private vec4 mExtents;

    public Wall(vec4 position, vec4 extents)
    {
        mPosition = (vec4) position.clone();
        mCollisionType = CollisionType.AABB;
        mIsStatic = true;

        mExtents = extents;

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
        program.setUniform("worldMatrix", math3d.scaling(mExtents).mul(math3d.translation(mPosition)));
        MESH.draw(program);
    }
}

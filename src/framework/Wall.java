package framework;

import framework.math3d.*;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.AABBType;

/**
 * Created by kory on 1/18/16.
 */
public class Wall
{
    private static Mesh MESH;

    private vec4 mPosition; // center of the bottom face of the wall

    private vec4 mExtents = new vec4(0.5f, 3.0f, 1.0f, 0.0f);
    private AABB mAABB;

    public Wall(vec4 position)
    {
        mPosition = position;
        mAABB = new AABB(mExtents, mPosition.add(new vec4(0.0f, mExtents.y / 2.0f, 0.0f, 0.0f)), AABBType.EXTENTS, AABBType.CENTER);

        if (MESH == null)
        {
            MESH = new Mesh("assets/testWall.obj.mesh");
        }
    }

    public void update()
    {

    }

    public void draw(Program program)
    {
//        System.out.println("Wall info:");
//        System.out.println("MIN: " + mAABB.getMin());
//        System.out.println("MAX: " + mAABB.getMax());
//        System.out.println("CENTER: " + mAABB.getCenter());
//        System.out.println("EXTENTS: " + mAABB.getExtents());
//        System.out.println();

        program.setUniform("worldMatrix", math3d.translation(mPosition));
        MESH.draw(program);
    }
}

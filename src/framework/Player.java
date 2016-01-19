package framework;

import framework.collisions.CollisionObject;
import framework.math3d.vec4;

public class Player extends CollisionObject
{
    private static Mesh MESH;

    public Player(vec4 position)
    {
        mPosition = (vec4) position.clone();

        if (MESH == null)
        {
            MESH = new Mesh("assets/testWall.obj.mesh");
        }
    }

    public void draw(Program program)
    {

    }
}

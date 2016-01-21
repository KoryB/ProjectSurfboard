package framework;

import framework.collisions.CollisionObject;
import framework.collisions.CollisionType;
import framework.math3d.math3d;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.AABBType;
import framework.math3d.vec4;

public class Player extends CollisionObject
{
    private static Mesh MESH;

    public Player(vec4 position)
    {
        mPosition = (vec4) position.clone();

        mCollisionPrimitive = new AABB((vec4) mPosition.clone(), new vec4(1, 2, 1, 0), AABBType.CENTER, AABBType.EXTENTS);
        mCollisionType = CollisionType.AABB;

        if (MESH == null)
        {
            MESH = new Mesh("assets/testPlayer.obj.mesh");
        }
    }

    public void update(float elapsed)
    {
        if (GameScreen.mInput.keyDown("PLAYER_MOVE_UP"))
        {
            move(new vec4(1.0*elapsed, 0, 0, 0));
        }
        if (GameScreen.mInput.keyDown("PLAYER_MOVE_DOWN"))
        {
            move(new vec4(-1.0*elapsed, 0, 0, 0));
        }
        if (GameScreen.mInput.keyDown("PLAYER_MOVE_LEFT"))
        {
            move(new vec4(0, 0, -1.0*elapsed, 0));
        }
        if (GameScreen.mInput.keyDown("PLAYER_MOVE_RIGHT"))
        {
            move(new vec4(0, 0, 1.0*elapsed, 0));
        }
    }

    public void draw(Program program)
    {
        program.setUniform("worldMatrix", math3d.translation(mPosition));
        MESH.draw(program);
    }
}

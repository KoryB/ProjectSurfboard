package framework;

import framework.collisions.CollisionObject;
import framework.collisions.CollisionType;
import framework.drawing.Drawable;
import framework.drawing.Mesh;
import framework.drawing.Program;
import framework.math3d.math3d;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.AABBType;
import framework.math3d.vec4;

public class Player extends CollisionObject implements Drawable
{
    private static Mesh MESH;
    private vec4 mGotoPoint;

    public Player(vec4 position)
    {
        mPosition = (vec4) position.clone();

        mCollisionPrimitive = new AABB((vec4) mPosition.clone(), new vec4(.2, 2, .2, 0), AABBType.CENTER, AABBType.EXTENTS);
        mCollisionType = CollisionType.AABB;

        if (MESH == null)
        {
            MESH = new Mesh("assets/testPlayer.obj.mesh");
        }
    }

    public vec4 getGotoPoint()
    {
        return mGotoPoint;
    }

    public void setGotoPoint(vec4 mGotoPoint)
    {
        this.mGotoPoint = mGotoPoint;
    }

    public void clearGotoPoint()
    {
        this.mGotoPoint = null;
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

        if (mGotoPoint != null)
        {
            vec4 gotoVector = mGotoPoint.sub(mPosition);
            gotoVector.y = 0.0f;
            gotoVector = math3d.normalize(gotoVector);

            move(gotoVector.mul(elapsed));

            vec4 deltaGoto = mGotoPoint.sub(mPosition);
            if (deltaGoto.x*deltaGoto.x + deltaGoto.z*deltaGoto.z <= Util.EPSILON)
            {
                mGotoPoint = null;
            }
        }
    }

    public void draw(Program program)
    {
        //TODO: Fix floating
        program.setUniform("worldMatrix", math3d.scaling(.5f, 1.0f, .5f).mul(math3d.translation(mPosition)));
        MESH.draw(program);
    }
}

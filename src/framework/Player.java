package framework;

import framework.collisions.CollisionObject;
import framework.collisions.CollisionType;
import framework.drawing.Drawable;
import framework.drawing.Mesh;
import framework.drawing.Program;
import framework.drawing.textures.ImageTexture;
import framework.math3d.mat4;
import framework.math3d.math3d;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.AABBType;
import framework.math3d.vec4;

public class Player extends CollisionObject implements Drawable
{
    private static Mesh MESH;
    private vec4 mGotoPoint;
    private float mCurrentFrame = 0.0f;
    private float mMaxFrames = 4.0f;

    public Player(vec4 position)
    {
        mPosition = (vec4) position.clone();

        mCollisionPrimitive = new AABB((vec4) mPosition.clone(), new vec4(.5, 2, .5, 0), AABBType.CENTER, AABBType.EXTENTS);
        mCollisionType = CollisionType.AABB;

        if (MESH == null)
        {
            System.out.println("Player:");
            MESH = new Mesh("assets/finished_meshes/newPlayer.obj.mesh");
            MESH.texture = new ImageTexture("assets/checker.png");
        }
    }

    public vec4 getPosition()
    {
        return (vec4) mPosition.clone();
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

        mCurrentFrame += elapsed;
        if (mCurrentFrame >= mMaxFrames)
        {
            mCurrentFrame = 0.0f;
        }
    }

    public void draw(Program program)
    {
        //TODO: Fix floating
        program.setUniform("worldMatrix", math3d.axisRotation(0, 1, 0, 0).mul(math3d.scaling(.5f, 1f, .5f).mul(math3d.translation(mPosition)).mul(math3d.translation(0, -1, 0))));
        program.setUniform("curframe", mCurrentFrame);
        MESH.draw(program);
    }
}

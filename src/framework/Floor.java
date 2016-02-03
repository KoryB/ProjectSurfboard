package framework;

import framework.collisions.CollisionObject;
import framework.collisions.CollisionType;
import framework.drawing.Drawable;
import framework.drawing.Mesh;
import framework.drawing.Program;
import framework.math3d.math3d;
import framework.math3d.primitives.BoundedPlane;
import framework.math3d.vec2;
import framework.math3d.vec4;

/**
 * Created by kory on 1/22/16.
 */
public class Floor extends CollisionObject implements Drawable
{
    public static Mesh MESH;
    private vec2 mSize;
    private vec2 mHalfSize;

    //Size only works if parallel to the Y-Axis currently
    public Floor(vec4 position, vec2 size)
    {
        mPosition = (vec4) position.clone();
        mCollisionType = CollisionType.PLANE;
        mIsStatic = true;
        mSize = size;
        mHalfSize = mSize.mul(0.5f);

        mCollisionPrimitive = new BoundedPlane(new vec4(-mHalfSize.x, 0.0, mHalfSize.y, 1.0), new vec4(-mHalfSize.x, 0.0, -mHalfSize.y, 1.0), new vec4(mHalfSize.x, 0.0, -mHalfSize.y, 1.0), new vec4(mHalfSize.x, 0.0, mHalfSize.y, 1.0));
        mCollisionPrimitive.translate(new vec4(mPosition.xyz(), 0.0f));

        if (MESH == null)
        {
            MESH = new Mesh("assets/floor.obj.mesh");
        }
    }

    public void update(float elapsed)
    {

    }

    public void draw(Program program)
    {
        // For now just draw a test wall below the actual tile
        program.setUniform("worldMatrix", math3d.scaling(mSize.x, 1.0f, mSize.y).mul(math3d.translation(mPosition)));
        MESH.draw(program);
    }
}

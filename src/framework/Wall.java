package framework;

import framework.math3d.*;

/**
 * Created by kory on 1/18/16.
 */
public class Wall
{
    private static Mesh MESH;

    private vec4 mPosition;

    public Wall(vec4 position)
    {
        mPosition = position;

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
        program.setUniform("worldMatrix", math3d.translation(mPosition));
        MESH.draw(program);
    }
}

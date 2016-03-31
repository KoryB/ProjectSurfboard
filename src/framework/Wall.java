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
            MESH = new Mesh("assets/finished_meshes/testWall.obj.mesh");
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

    public Float[] getVData(){
        Float[] data = {0.5f + mPosition.x, 1.0f + mPosition.y, 0.5f + mPosition.z, 0.857143f, 1.0f, 0.0f, 0.0f, 1.0f,        //0
                       -0.5f + mPosition.x, 1.0f + mPosition.y, 0.5f + mPosition.z, 0.714286f, 1.0f, 0.0f, 0.0f, 1.0f,        //1
                       -0.5f + mPosition.x, 0.0f + mPosition.y, 0.5f + mPosition.z, 0.714286f, 0.0f, 0.0f, 0.0f, 1.0f,        //2

                        0.5f + mPosition.x, 1.0f + mPosition.y, -0.5f + mPosition.z, 1.0f, 0.333333f, 0.0f, 1.0f, 0.0f,       //3
                       -0.5f + mPosition.x, 1.0f + mPosition.y, -0.5f + mPosition.z, 0.857143f, 0.333333f, 0.0f, 1.0f, 0.0f,  //4
                       -0.5f + mPosition.x, 1.0f + mPosition.y, 0.5f + mPosition.z, 0.857143f, 0.0f, 0.0f, 1.0f, 0.0f,        //5

                        0.5f + mPosition.x, 0.0f + mPosition.y, -0.5f + mPosition.z, 0.571429f, 0.0f, 0.0f, 0.0f, -1.0f,      //6
                       -0.5f + mPosition.x, 0.0f + mPosition.y, -0.5f + mPosition.z, 0.714286f, 0.0f, 0.0f, 0.0f, -1.0f,       //7
                       -0.5f + mPosition.x, 1.0f + mPosition.y, -0.5f + mPosition.z, 0.714286f, 1.0f, 0.0f, 0.0f, -1.0f,       //8

                        0.5f + mPosition.x, 0.0f + mPosition.y, 0.5f + mPosition.z, 1.0f, 0.666667f, 0.0f, -1.0f, 0.0f,        //9
                       -0.5f + mPosition.x, 0.0f + mPosition.y, 0.5f + mPosition.z, 0.857143f, 0.666667f, 0.0f, -1.0f, 0.0f,   //10
                       -0.5f + mPosition.x, 0.0f + mPosition.y, -0.5f + mPosition.z, 0.857143f, 0.333333f, 0.0f, -1.0f, 0.0f,  //11

                       -0.5f + mPosition.x, 1.0f + mPosition.y, 0.5f + mPosition.z, 0.285714f, 1.0f, -1.0f, 0.0f, 0.0f,        //12
                       -0.5f + mPosition.x, 1.0f + mPosition.y, -0.5f + mPosition.z, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,            //13
                       -0.5f + mPosition.x, 0.0f + mPosition.y, -0.5f + mPosition.z, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,            //14

                        0.5f + mPosition.x, 1.0f + mPosition.y, -0.5f + mPosition.z, 0.285714f, 0.0f, 1.0f, 0.0f, 0.0f,       //15
                        0.5f + mPosition.x, 1.0f + mPosition.y, 0.5f + mPosition.z, 0.571429f, 0.0f, 1.0f, 0.0f, 0.0f,        //16
                        0.5f + mPosition.x, 0.0f + mPosition.y, 0.5f + mPosition.z, 0.571429f, 1.0f, 1.0f, 0.0f, 0.0f,        //17

                        0.5f + mPosition.x, 0.0f + mPosition.y, 0.5f + mPosition.z, 0.857143f, 0.0f, 0.0f, 0.0f, 1.0f,        //18

                        0.5f + mPosition.x, 1.0f + mPosition.y, 0.5f + mPosition.z, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,             //19

                        0.5f + mPosition.x, 1.0f + mPosition.y, -0.5f + mPosition.z, 0.571429f, 1.0f, 0.0f, 0.0f, -1.0f,      //20

                        0.5f + mPosition.x, 0.0f + mPosition.y, -0.5f + mPosition.z, 1.0f, 0.333333f, 0.0f, -1.0f, 0.0f,      //21

                       -0.5f + mPosition.x, 0.0f + mPosition.y, 0.5f + mPosition.z, 0.285714f, 0.0f, -1.0f, 0.0f, 0.0f,       //22

                        0.5f + mPosition.x, 0.0f + mPosition.y, -0.5f + mPosition.z, 0.285714f, 1.0f, 1.0f, 0.0f, 0.0f};      //23

        return data;
    }

    public int[] getIData(){
        int[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 0, 2, 19, 3, 5, 20, 6, 8, 21, 9, 11, 22, 12, 14, 23, 15, 17};

        return data;
    }
}

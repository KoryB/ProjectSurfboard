package framework.collisions;

import framework.Player;
import framework.Wall;
import framework.math3d.primitives.AABB;

import java.util.ArrayList;

/**
 * Created by Michael on 4/24/2016.
 */
public class QTNode {
    protected float mULX;
    protected float mULZ;
    protected float mWidth;

    protected ArrayList<CollisionObject> mBucket;
    protected ArrayList<QTNode> mChildren;

    public QTNode(float x, float z, float width){
        mULX = x;
        mULZ = z;
        mWidth = width;

        mBucket = new ArrayList<>();
        mChildren = new ArrayList<>();

        /*
          Which Quads in mChildren are which
         /0//1/
         -----
         /2//3/
         */
        if(mWidth > 5){
            mChildren.add(new QTNode(mULX, mULZ, mWidth / 2));
            mChildren.add(new QTNode(mULX + (mWidth / 2), mULZ, mWidth / 2));
            mChildren.add(new QTNode(mULX, mULZ + (mWidth / 2), mWidth / 2));
            mChildren.add(new QTNode(mULX + (mWidth / 2), mULZ + (mWidth / 2), mWidth / 2));
        }
    }

    public ArrayList<QTNode> add(CollisionObject obj){
        ArrayList<QTNode> vals = new ArrayList<>();

        ((AABB)(obj.getCollisionPrimitive())).getXWidth();
        ((AABB)(obj.getCollisionPrimitive())).getZWidth();
        //If the node has children, just call the children's add.
        if(mChildren.size() > 0){
            if(obj.mPosition.x + ((AABB)(obj.getCollisionPrimitive())).getXWidth() > mULX
                    && obj.mPosition.x - ((AABB)(obj.getCollisionPrimitive())).getXWidth() < mULX + (mWidth / 2)
                    && obj.mPosition.z + ((AABB)(obj.getCollisionPrimitive())).getZWidth() > mULZ
                    && obj.mPosition.z  - ((AABB)(obj.getCollisionPrimitive())).getZWidth() < mULZ + (mWidth / 2)){
                vals.addAll(mChildren.get(0).add(obj));
            }
            if(obj.mPosition.x + ((AABB)(obj.getCollisionPrimitive())).getXWidth() > mULX + (mWidth / 2)
                    && obj.mPosition.x - ((AABB)(obj.getCollisionPrimitive())).getXWidth() < mULX + mWidth
                    && obj.mPosition.z + ((AABB)(obj.getCollisionPrimitive())).getZWidth() > mULZ
                    && obj.mPosition.z  - ((AABB)(obj.getCollisionPrimitive())).getZWidth() < mULZ + (mWidth / 2)){
                vals.addAll(mChildren.get(1).add(obj));
            }
            if(obj.mPosition.x + ((AABB)(obj.getCollisionPrimitive())).getXWidth() > mULX
                    && obj.mPosition.x - ((AABB)(obj.getCollisionPrimitive())).getXWidth() < mULX + (mWidth / 2)
                    && obj.mPosition.z + ((AABB)(obj.getCollisionPrimitive())).getZWidth() > mULZ + (mWidth / 2)
                    && obj.mPosition.z  - ((AABB)(obj.getCollisionPrimitive())).getZWidth() < mULZ + mWidth){
                vals.addAll(mChildren.get(2).add(obj));
            }
            if(obj.mPosition.x + ((AABB)(obj.getCollisionPrimitive())).getXWidth() > mULX + (mWidth / 2)
                    && obj.mPosition.x - ((AABB)(obj.getCollisionPrimitive())).getXWidth() < mULX + mWidth
                    && obj.mPosition.z + ((AABB)(obj.getCollisionPrimitive())).getZWidth() > mULZ + (mWidth / 2)
                    && obj.mPosition.z  - ((AABB)(obj.getCollisionPrimitive())).getZWidth() < mULZ + mWidth){
                vals.addAll(mChildren.get(3).add(obj));
            }
        }
        //If no children, add the obj to the bucket, and check to see if this is the node containing the player
        else{
            mBucket.add(obj);
            if(obj instanceof Player){
                vals.add(this);
            }
        }

        return vals;
    }

    public void handleCollisions(){
        Player pl = null;

        for(int i = 0; i < mBucket.size(); i++){
            if(mBucket.get(i) instanceof Player){
                pl = (Player)mBucket.get(i);
            }
        }

        for(int i = 0; i < mBucket.size(); i++){
            if(mBucket.get(i) instanceof Wall){
                CollisionHandler.pushApartAABB(pl, mBucket.get(i));
            }
        }
    }
}

package framework.collisions;

import framework.Player;

import java.util.ArrayList;

/**
 * Created by Michael on 4/24/2016.
 */
public class QuadTree {

    private QTNode mRoot;
    private ArrayList<QTNode> mNodesWithPlayer;

    public QuadTree(float x, float z, float width){
        mRoot = new QTNode(x, z, width);
        mNodesWithPlayer = new ArrayList<>();
    }

    public void add(CollisionObject obj){
        mNodesWithPlayer.addAll(mRoot.add(obj));
    }

    public void handleCollisions(){
        for(int i = 0; i < mNodesWithPlayer.size(); i++){
            mNodesWithPlayer.get(i).handleCollisions();
        }
    }

    public void update(Player player){
        for(int i = 0; i < mNodesWithPlayer.size(); i++){
            for(int j = 0; j < mNodesWithPlayer.get(i).mBucket.size(); j++){
                mNodesWithPlayer.get(i).mBucket.remove(player);
            }
        }
        mNodesWithPlayer.clear();
        this.add(player);
    }
}

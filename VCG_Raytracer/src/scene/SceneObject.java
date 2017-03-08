package scene;

import utils.algebra.Vec3;

public class SceneObject {

    protected Vec3 mPosition = new Vec3();

    public SceneObject(Vec3 pos){
        mPosition = pos;
    }

    public Vec3 getPosition(){
        return mPosition;
    }
}

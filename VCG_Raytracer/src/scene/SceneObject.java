package scene;

import utils.algebra.Vec3;

public class SceneObject {

    /**
        Standard constructor - SceneObject is the base of every component!
     **/
    public SceneObject(Vec3 pos){
        mPosition = pos;
    }

    public Vec3 getPosition(){
        return mPosition;
    }

    private Vec3 mPosition = new Vec3();
}

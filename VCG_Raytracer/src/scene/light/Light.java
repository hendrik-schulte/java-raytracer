package scene.light;

import scene.SceneObject;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Light extends SceneObject {

    private RgbColor color;


    public Light(Vec3 pos) {
        super(pos);
    }

    public RgbColor getColor(){
        return color;
    }
}

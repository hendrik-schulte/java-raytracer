package scene.light;

import scene.SceneObject;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Light extends SceneObject {

    private final RgbColor color;
    public final float intensity;

    public Light(Vec3 pos, RgbColor color, float intensity) {

        super(pos);

        this.color = color;
        this.intensity = intensity;
    }

    public RgbColor getColor(){
        return color;
    }

    public float getIntensity(){
        return intensity;
    }
}

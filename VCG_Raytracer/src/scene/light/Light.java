package scene.light;

import scene.SceneObject;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Light extends SceneObject {

    private RgbColor color;
    public float intensity;

    public Light(Vec3 pos, RgbColor color, float intensity) {

        super(pos);

        this.color = color;
        this.intensity = intensity;
    }

    public RgbColor getColor(){
        return color;
    }

    public float getIntensity(Vec3 point){
        return getIntensity(mPosition.sub(point).length());
    }

    public float getIntensity(float distance){
        return intensity;
    }
}

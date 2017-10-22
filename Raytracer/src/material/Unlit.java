package material;

import scene.Scene;
import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Unlit extends Material {

    public Unlit(RgbColor emission) {
        super(RgbColor.BLACK, RgbColor.BLACK, emission, 0, 1, 1, 1);
    }

    @Override
    public RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene){
        return emission;
    }

    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {
        return RgbColor.BLACK;
    }
}

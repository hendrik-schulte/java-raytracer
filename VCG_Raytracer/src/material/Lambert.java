package material;

import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Lambert extends Material {

    public Lambert(RgbColor ambient, RgbColor diffuse, RgbColor emission, float reflection, float smoothness, float opacity, float refractiveIndex) {
        super(ambient, diffuse, emission, reflection, smoothness, opacity, refractiveIndex);
    }

    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {
        return RgbColor.BLACK;
    }
}

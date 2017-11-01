package material;

import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Phong extends Material {

    private final RgbColor specular;
    private final float specularExp;
    private final float specularNormalFactor;

    public Phong(RgbColor ambient, RgbColor diffuse, RgbColor emission, RgbColor specular, float specularExp, float reflection, float smoothness, float opacity, float refractiveIndex) {
        super(ambient, diffuse, emission, reflection, smoothness, opacity, refractiveIndex);

        this.specular = specular;
        this.specularExp = specularExp;
        specularNormalFactor = (float) ((specularExp + 2) / 2 * Math.PI);
    }


    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {

        Vec3 reflectionVec = getReflectionVector(normal, lightVector);    //calculate reflection vector

        float tempSpec = Math.max(view.scalar(reflectionVec), 0f);

        if (tempSpec == 0) return RgbColor.BLACK;

        float specScalar = light.getIntensity() *                           //get light intensity multiplied with
                specularNormalFactor *                                      //normalised specular factor
                ((float) Math.pow(tempSpec, specularExp));      //dot of view and reflection vector to the power of specular exponent

        return specular.multRGB(
                light.getColor()).multScalar(
                specScalar);  //multiply intensity with light color and specular color
    }
}
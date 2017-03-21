package material;

import Main.Main;
import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Phong extends Material {

    private RgbColor specular;
    private float specularExp;
    private float specularNormalFactor;

    public Phong(RgbColor ambient, RgbColor diffuse, RgbColor emission, RgbColor specular, float specularExp, float reflection, float opacity, float refractiveIndex) {
        super(ambient, diffuse, emission, reflection, opacity, refractiveIndex);

        this.specular = specular;
        this.specularExp = specularExp;
        specularNormalFactor = (float) ((specularExp + 2) / 2 * Math.PI);
    }


    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {

        Vec3 reflectionVec = getReflectionVector(normal, lightVector);    //calculate reflection vector

        float dotProduct = view.scalar(reflectionVec);

        float specScalar = light.getIntensity() *                           //get light intensity multiplied with
                specularNormalFactor *                                      //normalised specular factor
                ((float) Math.pow((Math.max(0, dotProduct)), specularExp));      //dot of view and reflection vector to the power of specular exponent

        return specular.multRGB(
                light.getColor()).multScalar(
                specScalar);  //multiply intensity with light color and specular color
    }
}

package material;

import raytracer.Intersection;
import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Shape;
import utils.RgbColor;
import utils.algebra.Vec3;
import utils.io.Log;

public class Phong extends Material {

    private RgbColor specular;
    private float specularExp;
    private float specularNormalFactor;

    public Phong(RgbColor ambient, RgbColor diffus, RgbColor specular, float specularExp, float reflection, float refraction) {
        super(ambient, diffus, reflection, refraction);

        this.specular = specular;
        this.specularExp = specularExp;
        specularNormalFactor = (float) ((specularExp + 2) / 2 * Math.PI);
    }

    @Override
    public RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene) {

        RgbColor color = calcAmbient(scene);

        for (Light light : scene.lightList) {

            Vec3 lightVector = getLightVector(pos, light);      //getting light vector

//            Ray ray = new Ray(pos.add(lightVector.multScalar(0.001f)), lightVector);                               //create ray from intersection to light source
            Ray ray = new Ray(pos, lightVector);                               //create ray from intersection to light source

            //check if there is anything in the way to the light source
            if (ray.getIntersection(scene.shapeList, pos.DistanceTo(light.getPosition())) != null) {
                //in shadow -> continue to next light
                continue;
            }

            color = color.add(calcDiffus(light, normal, lightVector));
            color = color.add(calcSpecular(light, normal, view, lightVector));
        }

        return color;
    }

    private RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {

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

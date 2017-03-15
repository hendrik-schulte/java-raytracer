package material;

import raytracer.Intersection;
import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Shape;
import utils.RgbColor;
import utils.algebra.Vec3;

public abstract class Material {

    public RgbColor ambient;
    public RgbColor diffus;
    public float reflection;
    public float refraction;

    public Material(RgbColor ambient, RgbColor diffus, float reflection, float refraction) {

        this.ambient = ambient;
        this.diffus = diffus;
        this.reflection = reflection;
        this.refraction = refraction;
    }

    public abstract RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene);


    protected RgbColor calcAmbient(Scene scene) {
        return ambient.multScalar(scene.AmbientIntensity);
    }

    protected RgbColor calcDiffus(Light light, Vec3 normal, Vec3 lightVector) {

        return diffus.multRGB(                                   //color of light multiplicated with
                light.getColor()).multScalar(                               //light color
                light.getIntensity() *                                      //intensity of light
                        Math.max(0, normal.scalar(lightVector)));           //dot product of normal and light vector
    }

    protected Vec3 getLightVector(Vec3 pos, Light light) {
        return light.getPosition().sub(pos).normalize();
    }
}

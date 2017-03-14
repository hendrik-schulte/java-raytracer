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
    public float diffus;

    public Material(RgbColor ambient, float diffus) {

        this.ambient = ambient;
        this.diffus = diffus;
    }

    public abstract RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene);


    protected RgbColor calcAmbient(Scene scene) {
        return ambient.multScalar(scene.AmbientIntensity);
    }

    protected RgbColor calcDiffus(Vec3 pos, Vec3 normal, Scene scene) {

        RgbColor diffusColor = RgbColor.BLACK;

        for (Light light : scene.lightList) {

            Vec3 lightVector = getLightVector(pos, light);      //getting light vector

            Ray ray = new Ray(pos.add(lightVector.multScalar(0.0001f)), lightVector);                               //create ray from intersection to light source
            Intersection intersec = ray.getIntersection(scene.shapeList, pos.DistanceTo(light.getPosition()));       //check if there is anything in the way to the light source
//            Intersection intersec = ray.getIntersection(scene.shapeList, shape);       //check if there is anything in the way to the light source

            if(intersec != null) {
                continue;
            }

//            float dotProduct = normal.scalar(lightVector);



//            RgbColor result = light.getColor().multScalar(              //color of light multiplicated with
            RgbColor result = ambient.multScalar(                         //color of material multiplicated with
                            light.getIntensity(pos) *                       //intensity of light
                            diffus *                                        //diffus strength
                            Math.max(0, normal.scalar(lightVector)));       //dot product of normal and light vector

            diffusColor = diffusColor.add(result);
        }

        return diffusColor;
    }

    protected Vec3 getLightVector(Vec3 pos, Light light){
        return light.getPosition().sub(pos).normalize();
    }
}

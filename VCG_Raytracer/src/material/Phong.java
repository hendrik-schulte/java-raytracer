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

    public float specular;
    public float specularExp;
    private float specularNormalFactor;

    public Phong(RgbColor ambient, float diffus, float specular, float specularExp) {   //Todo: diffus = color, specular = color
        super(ambient, diffus);

        this.specular = specular;
        this.specularExp = specularExp;
        specularNormalFactor = (float) ((specularExp + 2) / 2 * Math.PI);
//        specularNormalFactor = 1;
    }

    @Override
    public RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene) {
        return calcAmbient(scene).add(calcDiffus(pos, normal, scene).add(calcSpecular(pos, normal, view, scene)));
    }

    private RgbColor calcSpecular(Vec3 pos, Vec3 normal, Vec3 view, Scene scene) {
        RgbColor specColor = RgbColor.BLACK;

        for (Light light : scene.lightList) {

            Vec3 lightVector = getLightVector(pos, light);      //getting light vector

            Ray ray = new Ray(pos.add(lightVector.multScalar(0.0001f)), lightVector);                               //create ray from intersection to light source
            Intersection intersec = ray.getIntersection(scene.shapeList, pos.DistanceTo(light.getPosition()));       //check if there is anything in the way to the light source
//            Intersection intersec = ray.getIntersection(scene.shapeList, shape);       //check if there is anything in the way to the light source

            if(intersec != null) {
                continue;
            }

//            Vec3 reflectionVec = (normal.sub(lightVector).multScalar(normal.scalar(lightVector) * 2)).normalize();    //calculate reflection vector
            Vec3 reflectionVec = normal.multScalar(2 * lightVector.scalar(normal)).sub(lightVector).normalize();    //calculate reflection vector

            float dotProduct = view.multScalar(-1).scalar(reflectionVec);

//            if(dotProduct <= 0) continue;

//            Log.print(this, "dot: " + dotProduct);

            float specScalar = light.getIntensity(pos) *        //get light intensity multiplied with
                    specular *                                  //specular factor
                    specularNormalFactor *                      //normalised specular factor
                    ((float) Math.pow((Math.max(0, dotProduct)), specularExp));      //dot of view and reflection vector to the power of specular exponent

            RgbColor result = light.getColor().multScalar(specScalar);  //multiply intensity with light color

            specColor = specColor.add(result);
        }

        return specColor;
    }
}

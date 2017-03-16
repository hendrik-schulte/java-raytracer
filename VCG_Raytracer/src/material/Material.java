package material;

import scene.Scene;
import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public abstract class Material {

    public RgbColor ambient;
    public RgbColor diffus;
    public float reflection;
    public float opacity;
    public float refractiveIndex;

    public Material(RgbColor ambient, RgbColor diffus, float reflection, float opacity, float refractiveIndex) {

        this.ambient = ambient;
        this.diffus = diffus;
        this.reflection = reflection;
        this.opacity = opacity;
        this.refractiveIndex = refractiveIndex;
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

    protected static Vec3 getLightVector(Vec3 pos, Light light) {

        return light.getPosition().sub(pos).normalize();
    }

    public static Vec3 getReflectionVector(Vec3 normal, Vec3 lightVector) {

        return normal.multScalar(2 * lightVector.scalar(normal)).sub(lightVector).normalize();
    }

    public static Vec3 getRefractionVector(Vec3 normal, Vec3 I, float n1, float n2) {

        float cosi = clamp(normal.scalar(I), -1, 1);
        float etai = n1, etat = n2;
        Vec3 N = normal;


        if (cosi < 0) {
            cosi = -cosi;
        } else {
            etai = n2;
            etat = n1;
            N = N.multScalar(-1);
        }

        float eta = etai / etat;
        float k = 1 - eta * eta * (1 - cosi * cosi);
        return k < 0 ? Vec3.ZERO : I.multScalar(eta).add(N.multScalar(eta * cosi - (float) Math.sqrt(k)));
    }

//    public static Vec3 getRefractionVector(Vec3 normal, Vec3 I, float n1, float n2) {
//
//        float c1 = normal.scalar(I);
//        float c2 = 1; //not implemented
//        float n = 0;
//
//        if (c1 == 0) return I;
//
//        if (c1 < 0) {
//            //entering medium
//            n = n1 / n2;
//        }
//
//        if (c1 > 0) {
//            //leaving medium
//            n = n2 / n1;
//        }
//
//        return I.multScalar(n).add(normal.multScalar(n * c1 - c2));
//    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
package material;

import Main.Main;
import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Shape;
import utils.MathEx;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Material {

    //material attributes
    public RgbColor ambient;
    public RgbColor diffuse;
    public RgbColor emission;
    public float reflection;
    public float opacity;
    public float refractiveIndex; //materialToAirSnellius as well
    public float smoothness;

    //calculated
//    private float distributionConeAngle;
    private float roughness;
    private float airToMaterialSnellius;
    private float airToMaterialSnelliusPWD; //to the power of two
    private float materialToAirSnelliusPWD; //to the power of two


    public Material(RgbColor ambient, RgbColor diffuse, RgbColor emission, float reflection, float smoothness, float opacity, float refractiveIndex) {

        this.ambient = ambient;
        this.diffuse = diffuse;
        this.reflection = reflection;
        this.smoothness = smoothness;
        this.opacity = opacity;
        this.refractiveIndex = refractiveIndex;
        this.emission = emission;

//        distributionConeAngle = (float) Math.acos(smoothness);
        roughness = 1 - smoothness;
        airToMaterialSnellius = 1 / refractiveIndex;
        airToMaterialSnelliusPWD = (float) Math.pow(airToMaterialSnellius, 2);
        materialToAirSnelliusPWD = (float) Math.pow(refractiveIndex, 2);
    }

    //    public abstract RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene);
    public RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene) {

        RgbColor color = calcAmbient(scene);

        for (Light light : scene.getLights()) {

            Vec3 lightVector = getLightVector(pos, light);      //getting light vector

            if (isInShadow(pos, lightVector, light, scene.shapeList)) continue;

            color = color.add(calcDiffuse(light, normal, lightVector));
            color = color.add(calcSpecular(light, normal, view, lightVector));
        }

        return color;
    }

    protected RgbColor calcAmbient(Scene scene) {
        return emission.add(ambient.multScalar(scene.AmbientIntensity));
    }

    protected RgbColor calcDiffuse(Light light, Vec3 normal, Vec3 lightVector) {

        return diffuse.multRGB(                                   //color of light multiplicated with
                light.getColor()).multScalar(                               //light color
                light.getIntensity() *                                      //intensity of light
                        Math.max(0, normal.scalar(lightVector)));           //dot product of normal and light vector
    }

    protected abstract RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector);

    protected boolean isInShadow(Vec3 pos, Vec3 lightVector, Light light, ArrayList<Shape> shapeList) {

        if (!Main.USE_SHADOWS) return false;    //shadows not enabled

        //create ray from intersection to light source
        Ray ray = new Ray(pos, lightVector);

        //check if there is anything in the way to the light source
        return ray.getIntersection(shapeList, pos.distance(light.getPosition())) != null;
    }

    protected static Vec3 getLightVector(Vec3 pos, Light light) {

        return light.getPosition().sub(pos).normalize();
    }

    public static Vec3 getReflectionVector(Vec3 normal, Vec3 lightVector) {

        return normal.multScalar(2 * lightVector.scalar(normal)).sub(lightVector).normalize();
    }

    public Vec3 getRefractionVector(Vec3 normal, Vec3 I) {

        float dot = normal.scalar(I);
        if (dot == 0) return I;

        float snelliusRatio;
        float snelliusRatioPWD;
        float cosA;                 //cosine alpha

        if (dot < 0) {
            //from outside to mateiral
            snelliusRatio = airToMaterialSnellius;
            snelliusRatioPWD = airToMaterialSnelliusPWD;
            cosA = -dot;
        } else {
            //from inside of material to air
            snelliusRatio = refractiveIndex;
            snelliusRatioPWD = materialToAirSnelliusPWD;
            cosA = dot;
        }

        float cosB = (float) Math.sqrt(1 - snelliusRatioPWD * (1 - Math.pow(dot, 2)));      //cosine beta
        Vec3 NcosB = normal.multScalar(cosB);                                               //normal * cosine beta


//        Vec3 NcosAMinI = normal.multScalar((float) Math.cos(dot)).sub(I);
        Vec3 NcosAMinI = normal.multScalar(cosA).sub(I);                    //N * cos(a) - I

        return (NcosAMinI.sub(NcosB)).multScalar(snelliusRatio);
    }

    /**
     * Taken from https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel
     *
     * @param normal
     * @param I
     * @param n1
     * @param n2
     * @return
     */
    public static Vec3 getRefractionVector(Vec3 normal, Vec3 I, float n1, float n2) {
        float cosi = MathEx.clamp(normal.scalar(I), -1, 1);
        float etai = n1, etat = n2;
        Vec3 N = normal;


        if (cosi < 0) {
            cosi = -cosi;
        } else {
            //internal reflection
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


    public List<Ray> getDistributedRays(Ray idealRay, int amount) {

        ArrayList<Ray> rays = new ArrayList<>();

        if (smoothness == 1) {
            rays.add(idealRay);
            return rays;
        }

        Vec3 dir = idealRay.getDirection();

//        Vec3 orthogonalVec1 = new Vec3(0, dir.z, -dir.y).normalize();
        Vec3 orthogonalVec1 = dir.getOrthogonal();
        Vec3 orthogonalVec2 = orthogonalVec1.cross(dir).normalize();

        Random r = new Random();

        for (int i = 0; i < amount; i++) {
            Vec3 deviation = (orthogonalVec1.multScalar(normalDistributedRoughness(r)).add(
                    orthogonalVec2.multScalar(normalDistributedRoughness(r))));

            Vec3 direction = dir.multScalar(smoothness).add(deviation);

            Vec3 newDirection = direction.add(deviation).normalize();


            rays.add(new Ray(idealRay.getStartPoint(), newDirection));
        }


        return rays;
    }

    private float normalDistributedRoughness(Random r) {
        return (float) Math.pow(((r.nextFloat() * 2) - 1), 2) * roughness;
    }

}
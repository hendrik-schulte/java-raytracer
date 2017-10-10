package material;

import Main.Main;
import raytracer.Ray;
import scene.Scene;
import scene.light.Light;
import scene.shape.Shape;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Material {

    public static final float SNELLIUS_VACUUM = 1f;
    public static final float SNELLIUS_AIR = 1.0003f;
    public static final float SNELLIUS_WATER = 1.333f;
    public static final float SNELLIUS_GLAS = 1.54f;
    public static final float SNELLIUS_DIAMOND = 2.42f;

    //material attributes
    public final RgbColor ambient;
    public final RgbColor diffuse;
    public final RgbColor emission;
    public final float reflection;
    public final float opacity;
    public final float transparency;
    public final float smoothness;
    public final float refractionIndex;

    //calculated
    public final float roughness;
    private final float airToMaterialSnellius;
    private final float materialToAirSnellius;
    private final float airToMaterialSnelliusPWD; //to the power of two
    private final float materialToAirSnelliusPWD; //to the power of two


    public Material(RgbColor ambient, RgbColor diffuse, RgbColor emission, float reflection, float smoothness, float opacity, float refractionIndex) {

        this.ambient = ambient;
        this.diffuse = diffuse;
        this.reflection = reflection;
        this.smoothness = smoothness;
        this.opacity = opacity;
        this.emission = emission;
        this.refractionIndex = refractionIndex;

        roughness = 1 - smoothness;
        transparency = 1 - opacity;

        airToMaterialSnellius = SNELLIUS_AIR / refractionIndex;
        materialToAirSnellius = refractionIndex / SNELLIUS_AIR;
        airToMaterialSnelliusPWD = (float) Math.pow(airToMaterialSnellius, 2);
        materialToAirSnelliusPWD = (float) Math.pow(materialToAirSnellius, 2);
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
        return emission.add(ambient.multScalar(scene.ambientIntensity * opacity));
    }

    protected RgbColor calcDiffuse(Light light, Vec3 normal, Vec3 lightVector) {

        return diffuse.multRGB(                                   //color of material multiplicated with
                light.getColor()).multScalar(                               //light color
                light.getIntensity() *                                      //intensity of light
                        opacity *
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

    /**
     * Returns the reflection vector based on the given normal and incoming vectors.
     * @param normal
     * @param incoming
     * @return
     */
    public static Vec3 getReflectionVector(Vec3 normal, Vec3 incoming) {

        return normal.multScalar(2 * incoming.scalar(normal)).sub(incoming).normalize();
    }

//    public Vec3 getRefractionVector(Vec3 normal, Vec3 I) {
//
//        float dot = normal.scalar(I);
//        if (dot == 0) {
//            return I;
//        }
//
//        float snelliusRatio;
//        float snelliusRatioPWD;
//        float cosA;                 //cosine alpha
//
//        if (dot < 0) {
//            //from outside to mateiral
//            snelliusRatio = airToMaterialSnellius;
//            snelliusRatioPWD = airToMaterialSnelliusPWD;
//            cosA = -dot;
////            Log.print(this, "from out to in");
//
//        } else {
//            //from inside of material to air
//            snelliusRatio = materialToAirSnellius;
//            snelliusRatioPWD = materialToAirSnelliusPWD;
//            cosA = dot;
////            Log.print(this, "from in to out. dot: " + dot);
//        }
//
//
//        double cosA_PWD = Math.pow(dot, 2);                                                 //dot^2 bzw. cosA^2
//
//        float inverseSneliusRatio = 1 - snelliusRatioPWD;
//        float inverseCosA_PWD = (float) (1 - cosA_PWD);
//        double cosB_PWD = 1 - snelliusRatioPWD * (1 - cosA_PWD);
//
//        if (cosB_PWD < 0) {
//            //total internal reflection
//
//            return getReflectionVector(I, normal.negate());
////            return Vec3.ZERO;
//        }
//
//
////        double cosB_PWD = 1 - (snelliusRatioPWD * (1 - cosA_PWD) );
//        float cosB = (float) Math.sqrt(cosB_PWD);          //cosine beta
//        Vec3 NcosB = normal.multScalar(cosB);                                               //normal * cosine beta
//
//
////        Vec3 NcosAMinI = normal.multScalar((float) Math.cos(dot)).sub(I);
//        Vec3 NcosAMinI = normal.multScalar(cosA).sub(I);                    //N * cos(a) - I
//
////        Log.print(this, "inverseSneliusRatio: " + inverseSneliusRatio);
////        Log.print(this, "inverseCosA_PWD: " + inverseCosA_PWD);
////        Log.print(this, "cosA: " + cosA);
////        Log.print(this, "cosA^2: " + cosA_PWD);
////        Log.print(this, "cosB^2: " + cosB_PWD);
////        Log.print(this, "cosB: " + cosB);
////        Log.print(this, "NcosB: " + NcosB);
//
//        return (NcosAMinI.sub(NcosB)).multScalar(snelliusRatio);
//    }
    /**
     * Returns the refraction vector based on the given normal and incoming vectors.
     * @param normal
     * @param I
     * @return
     */
    public Vec3 getRefractionVector(Vec3 normal, Vec3 I) {

        float dot = normal.scalar(I);

        //default: out to in
        float n = airToMaterialSnellius;
        float nPWD = airToMaterialSnelliusPWD;

        if (dot < 0) {
            //in to out
            n = materialToAirSnellius;
            nPWD = materialToAirSnelliusPWD;
        }

        if (1f - nPWD <= 0) {
            //total internal reflection
            return getReflectionVector(I, normal);
        }

        float cosB = I.scalar(normal);
        float sinB_PWD = nPWD * (1 - cosB * cosB);

        float a = n * cosB;
        float b = (float) Math.sqrt(1 - sinB_PWD);

        return I.negate().multScalar(n).add(normal.multScalar(a - b));
    }

//    public Vec3 getRefractionVector(Vec3 normal, Vec3 I) {
//
//        float dot = normal.scalar(I);
//
//        Log.print(this, "starting refract calculation");
//        Log.print(this, "normal: " + normal + " I: " + I);
//        Log.print(this, "dot: " + dot);
//
//        float n = airToMaterialSnellius;
//        float nPWD = airToMaterialSnelliusPWD;
//
//        if (dot < 0) {
//            //in to out
//            n = materialToAirSnellius;
//            nPWD = materialToAirSnelliusPWD;
////            dot = -dot;
//
//            Log.print(this, "in to out ");
//        } else{
//            Log.print(this, "out to in");
//        }
//
//        float cosBRadiant = 1 - nPWD * (1 - dot * dot);
//
//        if (cosBRadiant < 0) {
//            //total internal reflection
//            Log.print(this, "TIR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//
////            return getReflectionVector(I, normal);
//                return getReflectionVector(I, normal.negate().normalize());
//        }
//
//        float cosB = (float) Math.sqrt(cosBRadiant);
//
//        return (normal.multScalar(Math.abs(dot)).sub(I).sub(normal.multScalar(cosB))).multScalar(n);
//    }


//    /**
//     * Taken from https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel
//     *
//     * @param normal
//     * @param I
//     * @param n1
//     * @param n2
//     * @return
//     */
//    public static Vec3 getRefractionVector(Vec3 normal, Vec3 I, float n1, float n2) {
//        float cosi = MathEx.clamp(normal.scalar(I), -1, 1);
//        float etai = n1, etat = n2;
//        Vec3 N = normal.negate();
//
//
//        if (cosi < 0) {
//            cosi = -cosi;
//        } else {
//            //internal reflection
//            etai = n2;
//            etat = n1;
//            N = N.negate();
//        }
//
//        float eta = etai / etat;
//        float k = 1 - eta * eta * (1 - cosi * cosi);
//        return k < 0 ? Vec3.ZERO : I.multScalar(eta).add(N.multScalar(eta * cosi - (float) Math.sqrt(k)));
//    }

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

    public Vec3 getRefractionVectorDeGreve(Vec3 normal, Vec3 I){

        return getRefractionVector(normal, I, SNELLIUS_AIR, refractionIndex);
    }

    /**
     * Taken from https://graphics.stanford.edu/courses/cs148-10-summer/docs/2006--degreve--reflection_refraction.pdf
     *
     * @param normal
     * @param I
     * @param n1
     * @param n2
     * @return
     */
    public static Vec3 getRefractionVector(Vec3 normal, Vec3 I, float n1, float n2) {
        double n;
        double cosI = normal.scalar(I);
        if (cosI < 0) n = n1 / n2;   //out to in
        else n = n2 / n1;           //in to out
        double sinT2 = n * n * (1.0f - cosI * cosI);
        if (sinT2 > 1) return getReflectionVector(I, normal);
        double cosT = Math.sqrt(1.0 - sinT2);
        return I.multScalar(n).add(normal.multScalar((n * cosI - cosT)));
    }


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

//        Log.print(this, "");
//        Log.print(this, "smoothness: " + smoothness);
//        Log.print(this, "roughness: " + roughness);
//        Log.print(this, "norm roughness: " + normalDistributedRoughness(r));

        for (int i = 0; i < amount; i++) {
            Vec3 deviation = (orthogonalVec1.multScalar(normalDistributedRoughness(r)).add(
                    orthogonalVec2.multScalar(normalDistributedRoughness(r))));

            Vec3 direction = dir.multScalar(smoothness).add(deviation);

            Vec3 newDirection = direction.add(deviation).normalize();

//            Log.print(this, "scalar: " + (newDirection.scalar(dir)));

            rays.add(new Ray(idealRay.getStartPoint(), newDirection));
        }

        return rays;
    }

    private float normalDistributedRoughness(Random r) {
        return (float) (Math.pow(r.nextFloat(), 2) * 2 - 1) * roughness;
    }

//    public static Material parseMaterial(javafx.scene.paint.Material mat){
//        return new Lambert(RgbColor.RED,
//                RgbColor.RED,
//                RgbColor.BLACK,
//                0.0f,
//                1.0f,
//                1,
//                1);
//    }
}
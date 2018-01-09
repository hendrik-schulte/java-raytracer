package material;

import raytracer.Ray;
import scene.Scene;
import scene.SceneObject;
import scene.light.Light;
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

    /**
     * Calculates a local illumination model for this material.
     * @param pos position in world space.
     * @param normal
     * @param view vector from material to camera.
     * @param scene
     * @return
     */
    public RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene) {

        RgbColor color = calcAmbient(scene);

        for (Light light : scene.getLights()) {

            Vec3 lightVector = getLightVector(pos, light);      //getting light vector

            if (isInShadow(pos, lightVector, light, scene.root)) continue;

            color = color.add(calcDiffuse(light, normal, lightVector));
            color = color.add(calcSpecular(light, normal, view, lightVector));
        }

        return color;
    }

    /**
     * Calculates the ambient color.
     * @param scene
     * @return
     */
    protected RgbColor calcAmbient(Scene scene) {
        return emission.add(ambient.multScalar(scene.ambientIntensity * opacity));
    }

    /**
     * Colculates the diffuse part of the local illumination model.
     * @param light
     * @param normal
     * @param lightVector
     * @return
     */
    protected RgbColor calcDiffuse(Light light, Vec3 normal, Vec3 lightVector) {

        return diffuse.multRGB(                                   //color of material multiplicated with
                light.getColor()).multScalar(                               //light color
                light.getIntensity() *                                      //intensity of light
                        opacity *
                        Math.max(0, normal.scalar(lightVector)));           //dot product of normal and light vector
    }

    /**
     * Calculates the specular part of the local illumination model.
     * @param light
     * @param normal
     * @param view
     * @param lightVector
     * @return
     */
    protected abstract RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector);

    /**
     * Returns true if the given point is in shadow in respect to the given light source.
     * @param pos
     * @param lightVector
     * @param light
     * @param root
     * @return
     */
    protected boolean isInShadow(Vec3 pos, Vec3 lightVector, Light light, SceneObject root) {

//        if(true) return false;

        //create ray from intersection to light source
        Ray ray = new Ray(pos, lightVector);

//        Log.print(this, "checking shadow at " + pos + " sqrd: " + pos.distanceSquared(light.getWorldPosition()) + " dist: " + pos.distance(light.getWorldPosition()));

        //check if there is anything in the way to the light source
        return ray.shadowCheck(root, pos.distanceSquared(light.getWorldPosition()));
    }

    /**
     * Given the position and light source this returns the light vector.
     * @param pos
     * @param light
     * @return
     */
    protected static Vec3 getLightVector(Vec3 pos, Light light) {

        return light.getWorldPosition().sub(pos).normalize();
    }

    /**
     * Returns the reflection vector based on the given normal and incoming vectors.
     * @param normal
     * @param incoming
     * @return
     */
    public static Vec3 getReflectionVector(Vec3 normal, Vec3 incoming) {

        return normal.scale(2 * incoming.scalar(normal)).sub(incoming).normalize();
    }

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

        return I.negate().scale(n).add(normal.scale(a - b));
    }

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
        return I.scale(n).add(normal.scale((n * cosI - cosT)));
    }

    /**
     * Returns a bundle of rays of the given amount where each ray is randomly altered based on the smoothness.
     * @param idealRay
     * @param amount
     * @return
     */
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
            Vec3 deviation = (orthogonalVec1.scale(normalDistributedRoughness(r)).add(
                    orthogonalVec2.scale(normalDistributedRoughness(r))));

            Vec3 direction = dir.scale(smoothness).add(deviation);

            Vec3 newDirection = direction.add(deviation).normalize();

//            Log.print(this, "scalar: " + (newDirection.scalar(dir)));

            rays.add(new Ray(idealRay.getStartPoint(), newDirection));
        }

        return rays;
    }

    private float normalDistributedRoughness(Random r) {
        return (float) (Math.pow(r.nextFloat(), 2) * 2 - 1) * roughness;
    }
}
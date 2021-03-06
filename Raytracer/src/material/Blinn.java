package material;

import org.w3c.dom.css.RGBColor;
import raytracer.Settings;
import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Blinn extends Material {

    private RgbColor specular;
    private float specularExp;
    private float specularNormalFactor;

    public Blinn(RgbColor ambient, RgbColor diffuse, RgbColor emission, RgbColor specular, float specularExp, float reflection, float smoothness, float opacity, float refractiveIndex) {
        super(ambient, diffuse, emission, reflection, smoothness, opacity, refractiveIndex);

        this.specular = specular;
        this.specularExp = specularExp;
        specularNormalFactor = (float) ((specularExp + 2) / 2 * Math.PI);
    }

    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {

        Vec3 halfwayVector = view.add(lightVector).normalize();    //calculate halfway vector

        float tempSpec = Math.max(normal.scalar(halfwayVector), 0f);

        if (tempSpec == 0) return RgbColor.BLACK;

        float specScalar = light.getIntensity() *                           //get light intensity multiplied with
                specularNormalFactor *                                      //normalised specular factor
                ((float) Math.pow(tempSpec, specularExp));      //dot of view and reflection vector to the power of specular exponent

        return specular.multRGB(
                light.getColor()).multScalar(
                specScalar);  //multiply intensity with light color and specular color
    }

    /**
     * Creates the default wall material based on the raytracer settings.
     * @param settings
     * @param baseColor
     * @param floor
     * @return
     */
    public static Blinn CreateWall(Settings settings, RgbColor baseColor, boolean floor){
        float smoothness = 1;

        if(floor) smoothness = settings.FLOOR_SMOOTHNESS;

        return new Blinn(baseColor, baseColor, RgbColor.BLACK, settings.ROOM_SPECULAR, settings.ROOM_SPECULAREXP, settings.ROOM_REFLECTIVITY, smoothness, 1, 1);
    }
}

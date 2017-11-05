package material;

import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Lambert extends Material {

    public Lambert(RgbColor ambient, RgbColor diffuse, RgbColor emission, float reflection, float smoothness, float opacity, float refractiveIndex) {
        super(ambient, diffuse, emission, reflection, smoothness, opacity, refractiveIndex);
    }

    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {
        return RgbColor.BLACK;
    }

    //region Default Materials

    //Simple
    public static final Lambert WHITE = new Lambert(RgbColor.WHITE, RgbColor.WHITE, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert BLUE = new Lambert(RgbColor.BLUE, RgbColor.BLUE, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert RED = new Lambert(RgbColor.RED, RgbColor.RED, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert GREEN = new Lambert(RgbColor.GREEN, RgbColor.GREEN, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert YELLOW = new Lambert(RgbColor.YELLOW, RgbColor.YELLOW, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert MAGENTA = new Lambert(RgbColor.MAGENTA, RgbColor.MAGENTA, RgbColor.BLACK, 0, 1, 1, 1);

    //Reflective
    public static final Lambert FULL_SMOOTH_REFLECTIVE = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 1, 1, 1, 1);

    //Refractive
    public static final Lambert DIAMOND_SMOOTH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, 1, 0, SNELLIUS_DIAMOND);
    public static final Lambert GLAS_SMOOTH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, 1, 0, SNELLIUS_GLAS);
    public static final Lambert WATER_SMOOTH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, 1, 0, SNELLIUS_WATER);

    public static final Lambert DIAMOND_ROUGH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, .96f, 0, SNELLIUS_DIAMOND);
    public static final Lambert GLAS_ROUGH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, .96f, 0, SNELLIUS_GLAS);
    public static final Lambert WATER_ROUGH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, .96f, 0, SNELLIUS_WATER);

    public static final Lambert DIAMOND_VERY_ROUGH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, .9f, 0, SNELLIUS_DIAMOND);
    public static final Lambert GLAS_VERY_ROUGH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, .9f, 0, SNELLIUS_GLAS);
    public static final Lambert WATER_VERY_ROUGH = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, .9f, 0, SNELLIUS_WATER);


    //Blurry Reflection
    public static final Lambert GREY_BLURRY_REFLECTIVE = new Lambert(RgbColor.SOFT_GRAY, RgbColor.SOFT_GRAY, RgbColor.BLACK, 0.5f,0.92f,1,1);
    public static final Lambert BLUE_BLURRY_REFLECTIVE = new Lambert(RgbColor.BLUE, RgbColor.BLUE, RgbColor.BLACK, 0.5f,0.92f,1,1);
    public static final Lambert FULL_BLURRY_REFLECTIVE = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 1.0f,0.92f,1,1);

    public static final Lambert GREY_ROUGH_BLURRY_REFLECTIVE = new Lambert(RgbColor.SOFT_GRAY, RgbColor.SOFT_GRAY, RgbColor.BLACK, 0.3f,0.5f,1,1);

    //endregion
}

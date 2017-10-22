package material;

import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Lambert extends Material {

    public static final Lambert WHITE = new Lambert(RgbColor.WHITE, RgbColor.WHITE, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert BLUE = new Lambert(RgbColor.BLUE, RgbColor.BLUE, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert RED = new Lambert(RgbColor.RED, RgbColor.RED, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert GREEN = new Lambert(RgbColor.GREEN, RgbColor.GREEN, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert YELLOW = new Lambert(RgbColor.YELLOW, RgbColor.YELLOW, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert MAGENTA = new Lambert(RgbColor.MAGENTA, RgbColor.MAGENTA, RgbColor.BLACK, 0, 1, 1, 1);
    public static final Lambert FULL_SMOOTH_REFLECTIVE = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 1, 1, 1, 1);
    public static final Lambert WHITE_EMMISIVE = new Lambert(RgbColor.WHITE, RgbColor.WHITE, RgbColor.WHITE, 0, 1, 1, 1);
    public static final Lambert DIAMOND = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.BLACK, 0, 1, 0, SNELLIUS_DIAMOND);
    public static final Lambert DEBUG_GREEN = new Lambert(RgbColor.BLACK, RgbColor.BLACK, RgbColor.GREEN, 0.0f, 1.0f, 1, 1f);

    public Lambert(RgbColor ambient, RgbColor diffuse, RgbColor emission, float reflection, float smoothness, float opacity, float refractiveIndex) {
        super(ambient, diffuse, emission, reflection, smoothness, opacity, refractiveIndex);
    }

    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {
        return RgbColor.BLACK;
    }
}

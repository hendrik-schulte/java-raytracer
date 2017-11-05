package material;

import scene.Scene;
import scene.light.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Unlit extends Material {

    public Unlit(RgbColor emission) {
        super(RgbColor.BLACK, RgbColor.BLACK, emission, 0, 1, 1, 1);
    }

    @Override
    public RgbColor getColor(Vec3 pos, Vec3 normal, Vec3 view, Scene scene){
        return emission;
    }

    @Override
    protected RgbColor calcSpecular(Light light, Vec3 normal, Vec3 view, Vec3 lightVector) {
        return RgbColor.BLACK;
    }

    //region Default Materials

    //Emmissive
    public static final Unlit EMMISIVE_WHITE = new Unlit(RgbColor.WHITE);
    public static final Unlit EMMISIVE_GREEN = new Unlit(RgbColor.GREEN);
    public static final Unlit EMMISIVE_BLUE = new Unlit(RgbColor.BLUE);
    public static final Unlit EMMISIVE_RED = new Unlit(RgbColor.RED);
    public static final Unlit EMMISIVE_BLACK = new Unlit(RgbColor.BLACK);
    public static final Unlit EMMISIVE_YELLOW = new Unlit(RgbColor.YELLOW);
    public static final Unlit EMMISIVE_MAGENTA = new Unlit(RgbColor.MAGENTA);

    //endregion
}

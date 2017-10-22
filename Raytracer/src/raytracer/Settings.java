package raytracer;

import utils.AntiAliasing;
import utils.RgbColor;

/**
 * Container for most important raytracer settings.
 */
public class Settings {

    //OUTPUT SETTINGS

    public int IMAGE_WIDTH = 800;
    public int IMAGE_HEIGHT = 600;
    public boolean DRAW_STATS = true;
    public String OUTPUT_FILE_NAME = "pretty";

    //RAYTRACER OPTIONS

    public int RECURSIONS = 4;
    public int RAY_DISTRIBUTION_SAMPLES = 1;
    public int MULTI_THREADING = 4;
    public float AMBIENT = 0.04f;
    public AntiAliasing ANTIALIASING = new AntiAliasing(
            AntiAliasing.Level.x16,
            true,
            IMAGE_WIDTH, IMAGE_HEIGHT);
//    public boolean USE_SHADOWS = true;
    public float LIGHT_SAMPLING = .5f;
    public int NUM_LIGHTS = 8;

    public float FLOOR_SMOOTHNESS = 0.97f;
    public float CEILING_SMOOTHNESS = 1f;
    public float WALL_SMOOTHNESS = 1f;
    public float ROOM_REFLECTIVITY = 0.2f;
    public RgbColor ROOM_SPECULAR = new RgbColor(0f, 0f, 0f);
    public float ROOM_SPECULAREXP = 12f;

    public long timeStart;
}

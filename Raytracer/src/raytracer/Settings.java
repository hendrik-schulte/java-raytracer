package raytracer;

import utils.AntiAliasing;
import utils.RgbColor;

/**
 * Container for most important raytracer settings.
 */
public class Settings {

    //region Default Settings

    //OUTPUT SETTINGS

    public int IMAGE_WIDTH = 800;
    public int IMAGE_HEIGHT = 600;

    /**
     * When true, render stats are drawn on top of the rendering.
     */
    public boolean DRAW_STATS = true;

    /**
     * Defines the name of the output file (.png is added automatically).
     */
    public String OUTPUT_FILE_NAME = "pretty";

    //RAYTRACER OPTIONS

    public int RECURSIONS = 4;

    /**
     * How many samples are taken for blurry reflections.
     */
    public int RAY_DISTRIBUTION_SAMPLES = 1;

    /**
     * How many Render threads render a single image simultaneously (4 seems best).
     */
    public int MULTI_THREADING = 4;



    /**
     * Anti Aliasing options.
     */
    public AntiAliasing ANTIALIASING = new AntiAliasing(
            AntiAliasing.Level.x16,
            true,
            IMAGE_WIDTH, IMAGE_HEIGHT);
//    public boolean USE_SHADOWS = true;#

    /**
     * Ambient lighting factor.
     */
    public float AMBIENT = 0.04f;
    public boolean USE_AREA_LIGHTS = true;
    public float LIGHT_SAMPLING = .5f;
    public int NUM_LIGHTS = 8;

    public float FLOOR_SMOOTHNESS = 0.97f;
    public float CEILING_SMOOTHNESS = 1f;
    public float WALL_SMOOTHNESS = 1f;
    public float ROOM_REFLECTIVITY = 0.2f;
    public RgbColor ROOM_SPECULAR = new RgbColor(0f, 0f, 0f);
    public float ROOM_SPECULAREXP = 12f;

    //endregion

    public static Settings CUSTOM(){
        Settings s = new Settings();

        s.OUTPUT_FILE_NAME = "custom";

        s.RECURSIONS = 4;
        s.ANTIALIASING = new AntiAliasing(AntiAliasing.Level.x8, true, s.IMAGE_WIDTH, s.IMAGE_HEIGHT);

        s.LIGHT_SAMPLING = 0.6f;
        s.NUM_LIGHTS = 4;

        s.FLOOR_SMOOTHNESS = 1;
        s.WALL_SMOOTHNESS = 1;
        s.ROOM_REFLECTIVITY = 0;

        return s;
    }

    public static Settings FAST(){
        Settings s = new Settings();

        s.OUTPUT_FILE_NAME = "fast";

        s.RECURSIONS = 2;
        s.ANTIALIASING = new AntiAliasing(AntiAliasing.Level.x4, true, s.IMAGE_WIDTH, s.IMAGE_HEIGHT);

        s.USE_AREA_LIGHTS = false;
        s.LIGHT_SAMPLING = 0.5f;
        s.NUM_LIGHTS = 2;

        s.FLOOR_SMOOTHNESS = 1;
        s.WALL_SMOOTHNESS = 1;
        s.ROOM_REFLECTIVITY = 0;

        return s;
    }

    public static Settings AVERAGE(){
        Settings s = new Settings();

        s.OUTPUT_FILE_NAME = "average";

        s.RECURSIONS = 3;
        s.ANTIALIASING = new AntiAliasing(AntiAliasing.Level.x8, true, s.IMAGE_WIDTH, s.IMAGE_HEIGHT);

        s.USE_AREA_LIGHTS = true;
        s.LIGHT_SAMPLING = 0.4f;
        s.NUM_LIGHTS = 7;

        s.FLOOR_SMOOTHNESS = 1;
        s.WALL_SMOOTHNESS = 1;
        s.ROOM_REFLECTIVITY = 0;

        return s;
    }

    public static Settings PRETTY(){
        Settings s = new Settings();

        s.OUTPUT_FILE_NAME = "pretty";

//        s.IMAGE_WIDTH = 1024;
//        s.IMAGE_HEIGHT = 800;

        s.RAY_DISTRIBUTION_SAMPLES = 2;
        s.RECURSIONS = 4;
        s.ANTIALIASING = new AntiAliasing(AntiAliasing.Level.x16, true, s.IMAGE_WIDTH, s.IMAGE_HEIGHT);

        s.USE_AREA_LIGHTS = true;
        s.LIGHT_SAMPLING = 0.8f;
        s.NUM_LIGHTS = 9;

        s.FLOOR_SMOOTHNESS = 0.97f;
        s.WALL_SMOOTHNESS = 1;
        s.ROOM_REFLECTIVITY = 0.2f;

        return s;
    }
}


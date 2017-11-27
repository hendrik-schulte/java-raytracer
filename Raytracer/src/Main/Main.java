package Main;

import raytracer.Raytracer;
import raytracer.Settings;
import scene.Scene;
import scene.SceneFactory;
import ui.Window;
import utils.Timer;
import utils.algebra.Matrix4x4;
import utils.algebra.Quaternion;
import utils.algebra.Vec3;
import utils.io.Log;

public class Main {

    public static void main(String[] args) {

//        Matrix4x4 mat1 =  new Matrix4x4(
//                Quaternion.euler(0,0,0),
//                Vec3.ZERO,                      //position
//                new Vec3(5, 1, 1));
//
//        Matrix4x4 mat2 =  new Matrix4x4(
//                Quaternion.euler(90,0,0),
//                Vec3.ZERO,                      //position
//                new Vec3(1, 1, 1));
//
//        Log.print(new Object(), "mat1: " + mat1);
//        Log.print(new Object(), "mat2: " + mat2);
//        Log.print(new Object(), "mat1 * mat2: " + mat1.mult(mat2));

        Settings set = Settings.AVERAGE();


        Scene renderScene = SceneFactory.createCustom(set);
//        Scene renderScene = SceneFactory.createClassic(settings);

        renderScene.root.printRecursively();

        render(renderScene, set);

//        renderClassic(custom);
//        renderClassic(fast);
//        renderClassic(average);
//        renderClassic(pretty);
    }

    private static void renderClassic(Settings settings) {
        Scene renderScene = SceneFactory.createClassic(settings);
        render(renderScene, settings);
    }


    /**
     * Raytrace through the given scene with the given settings.
     **/
    private static void render(Scene scene, Settings settings) {
        Window renderWindow = new Window(settings.IMAGE_WIDTH, settings.IMAGE_HEIGHT);

        Timer timer = new Timer();
        Raytracer raytracer = new Raytracer(scene, renderWindow, settings, () -> renderingFinished(renderWindow, settings, timer));

        raytracer.renderScene();
    }

    /**
     * This is called after the last render Thread finished
     */
    private static void renderingFinished(Window renderWindow, Settings settings, Timer timer) {

        renderWindow.exportRendering(settings.OUTPUT_FILE_NAME, timer.stop(), settings);

        if (settings.ANTIALIASING.isAdaptive())
            Log.print(settings.ANTIALIASING, settings.ANTIALIASING.calcAdaptivePerformance() + "% of multi-sampled pixels have been saved by adaptive AA. Color Threshold: " + settings.ANTIALIASING.colorThreshold);
    }
}
// ************************************************************ //
//                      Hochschule Duesseldorf                  //
//                                                              //
//                     Vertiefung Computergrafik                //
// ************************************************************ //


/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    1. Documentation:    Did you comment your code shortly but clearly?
    2. Structure:        Did you clean up your code and put everything into the right bucket?
    3. Performance:      Are all loops and everything inside really necessary?
    4. Theory:           Are you going the right way?

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 <<< YOUR TEAM NAME >>>

     Master of Documentation:
     Master of Structure:
     Master of Performance:
     Master of Theory:

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

import material.Material;
import material.Phong;
import raytracer.Raytracer;
import scene.camera.PerspCam;
import scene.light.Light;
import scene.shape.Plane;
import scene.shape.Sphere;
import ui.Window;
import scene.Scene;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.awt.*;

// Main application class. This is the routine called by the JVM to run the program.
public class Main {

    /**
     * BOX_DIMENSION
     **/

    static int IMAGE_WIDTH = 800;
    static int IMAGE_HEIGHT = 600;

    /**
     * RAYTRACER
     **/

    static int RECURSIONS = 2;

    /**
     * Initial method. This is where the show begins.
     **/
    public static void main(String[] args) {
        long tStart = System.currentTimeMillis();

        Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

        draw(renderWindow);

        renderWindow.exportRendering(String.valueOf(stopTime(tStart)), RECURSIONS, 0);

//        while(true){
//            //Thread.sleep(100);
//            System.out.println("(" + MouseInfo.getPointerInfo().getLocation().x +
//                    ", " +
//                    MouseInfo.getPointerInfo().getLocation().y + ")");
//        }
    }

    /**
     * Draw the scene that was set up
     **/
    private static void draw(Window renderWindow) {
        Scene renderScene = setupScene();

        raytraceScene(renderWindow, renderScene);
    }

    /**
     * Raytrace through the scene
     **/
    private static void raytraceScene(Window renderWindow, Scene renderScene) {
        Raytracer raytracer = new Raytracer(renderScene, renderWindow, RECURSIONS);

        raytracer.renderScene();
    }

    /**
     * Stop the time for debug only
     **/
    private static double stopTime(long tStart) {
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        return tDelta / 1000.0;
    }

    private static Scene setupScene() {
        Scene scene = new Scene(0.1f);

        scene.createCamera(new PerspCam(
                new Vec3(0, 0, 17),    //pos
                new Vec3(0, 0, 0),    //center of view
                new Vec3(0, 1, 0),    //user-up
                70,
                3));

        scene.createShape(new Sphere(
                new Vec3(0, 0, 0),
                1f,
                new Phong(RgbColor.BLACK,
                        RgbColor.BLACK,
                        new RgbColor(0.07f, 0.07f, 0.07f),
                        32f,
                        1f,
                        0)));

        scene.createShape(new Sphere(
                new Vec3(-2, 1.5f, - 1.5f),
                1f,
                new Phong(RgbColor.RED,
                        RgbColor.RED,
                        new RgbColor(0.05f, 0.05f, 0.05f),
                        26,
                        .5f,
                        0)));

//        scene.createShape(new Sphere(
//                new Vec3(1, -1.6f, 11),
//                1f,
//                new Phong(RgbColor.GREEN,
//                        RgbColor.GREEN,
//                        new RgbColor(0.05f, 0.05f, 0.05f),
//                        26,
//                        .5f,
//                        0)));

        //back wall
        scene.createShape(new Plane(
                new Vec3(0, 0, -7),    //pos
                new Vec3(0, 0, 1),     //normal
                new Phong(RgbColor.CYAN,        //ambient
                        RgbColor.CYAN,          //diffuse
                        new RgbColor(0.02f, 0.02f,0.02f),       //specular
                        12,
                        .2f,
                        0)));

        //Left wall
        scene.createShape(new Plane(
                new Vec3(-4, 0, 0),    //pos
                new Vec3(1, 0, 0),     //normal
                new Phong(RgbColor.MAGENTA,        //ambient
                        RgbColor.MAGENTA,          //diffuse
                        new RgbColor(0.02f, 0.02f,0.02f),       //specular
                        12,
                        .2f,
                        0)));

        //Floor
        scene.createShape(new Plane(
                new Vec3(0, -2, 0),    //pos
                new Vec3(0, 1, 0),     //normal
                new Phong(RgbColor.BLUE,        //ambient
                        RgbColor.BLUE,          //diffuse
                        new RgbColor(0.02f, 0.02f,0.02f),       //specular
                        12,
                        .2f,
                        0)));

//        //Mirror
//        scene.createShape(new Plane(
//                new Vec3(0, -2, 0),    //pos
//                new Vec3(0, 1, 0),     //normal
//                new Phong(RgbColor.BLACK,        //ambient
//                        RgbColor.BLACK,          //diffuse
//                        new RgbColor(0.1f, 0.1f,0.1f),       //specular
//                        64,
//                        1f,
//                        0)));

//        scene.createShape(new Sphere(new Vec3(5, 1, -3.5f), 1.1f, new Phong(RgbColor.GREEN, 0.2f, 0.0f, 4)));

        scene.createLight(new Light(
                new Vec3(3, 0.5f, 1.5f),
                RgbColor.WHITE,
                0.4f));

//        scene.createLight(new Light(
//                new Vec3(0, 4.5f, 0f),
//                RgbColor.WHITE,
//                0.4f));
//
//        scene.createLight(new Light(
//                new Vec3(0, 4.5f, 16f),
//                RgbColor.WHITE,
//                0.5f));

        return scene;
    }
}
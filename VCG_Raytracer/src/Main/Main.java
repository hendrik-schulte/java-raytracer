package Main;// ************************************************************ //
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

import material.Blinn;
import material.Lambert;
import material.Material;
import material.Phong;
import raytracer.Raytracer;
import scene.camera.PerspCam;
import scene.light.AreaLight;
import scene.light.Light;
import scene.shape.*;
import scene.shape.Rectangle;
import ui.Window;
import scene.Scene;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

import java.awt.*;

// Main.Main application class. This is the routine called by the JVM to run the program.
public class Main {

    /**
     * BOX_DIMENSION
     **/

    static int IMAGE_WIDTH = 1000;
    static int IMAGE_HEIGHT = 800;

    /**
     * RAYTRACER
     **/

    private static int RECURSIONS = 2;
    private static int RAY_DISTRIBUTION_SAMPLES = 2;
    private static int MULTI_THREADING = 4;
    private static float AMBIENT = 0.04f;
    private static Raytracer.AntiAliasingLevel ANTIALIASING_LEVEL = Raytracer.AntiAliasingLevel.x2;
    public static boolean USE_SHADOWS = true;

    private static float ROOM_SMOOTHNESS = 1.00f;
    private static float ROOM_REFLECTIVITY = 0.0f;

    private static Window renderWindow;
    private static long tStart;


    /**
     * Initial method. This is where the show begins.
     **/
    public static void main(String[] args) {
        tStart = System.currentTimeMillis();

        renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

        draw(renderWindow);
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
        Raytracer raytracer = new Raytracer(renderScene, renderWindow, RECURSIONS, RAY_DISTRIBUTION_SAMPLES, ANTIALIASING_LEVEL, MULTI_THREADING, () -> renderingFinished());

        raytracer.renderScene();
    }

    /**
     * This is called after the last render Thread finished
     */
    private static void renderingFinished() {
//        renderWindow.exportRendering(String.valueOf(stopTime(tStart)), RECURSIONS, getAntiAliasingLevel(), MULTI_THREADING);
        renderWindow.exportRendering(stopTime(tStart), RECURSIONS, getAntiAliasingLevel(), MULTI_THREADING);
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
        Scene scene = new Scene(AMBIENT);

        scene.createCamera(new PerspCam(
                new Vec3(0, 0, 17),    //pos
                new Vec3(0, 0, 0),    //center of view
                new Vec3(0, 1, 0),    //user-up
                70,
                3));

        setupSpheres(scene);
        setupBox(scene);

//        scene.createShape(new Sphere(new Vec3(5, 1, -3.5f), 1.1f, new Phong(RgbColor.GREEN, 0.2f, 0.0f, 4)));

        setupLight(scene);

        return scene;
    }

    private static void setupSpheres(Scene scene) {

//        scene.createShape(new Sphere(
//                new Vec3(0, 0, 8),
//                1.0f,
//                new Lambert(RgbColor.RED,
//                        RgbColor.RED,
//                        RgbColor.BLACK,
//                        0.0f,
//                        1.0f,
//                        1,
//                        1f)));

//        scene.createShape(new Sphere(
//                new Vec3(0, 0, 8),
//                2.0f,
//                new Phong(RgbColor.BLACK,
//                        RgbColor.BLACK,
//                        RgbColor.BLACK,
//                        new RgbColor(0.00f, 0.00f, 0.00f),
//                        32f,
//                        0.5f,
//                        1.0f,
//                        1,
//                        1)));

//        scene.createShape(new Sphere(
//                new Vec3(-2, 1.5f, 7.5f),
//                1f,
//                new Phong(RgbColor.RED,
//                        RgbColor.RED,
//                        RgbColor.BLACK,
//                        new RgbColor(0.08f, 0.08f, 0.08f),
//                        26,
//                        .0f,
//                        1.0f,
//                        1,
//                        1)));

        scene.createShape(new Sphere(
                new Vec3(2, 1.5f, 7.5f),
                1f,
                new Blinn(RgbColor.BLUE,
                        RgbColor.BLUE,
                        RgbColor.BLACK,
                        new RgbColor(0.08f, 0.08f, 0.08f),
                        26,
                        .0f,
                        1.0f,
                        1,
                        1)));

        //mat material
//        scene.createShape(new Sphere(
//                new Vec3(0, 0.5f, 12.0f),
//                1f,
//                new Phong(RgbColor.GRAY,
//                        RgbColor.GRAY,
//                        RgbColor.BLACK,
//                        new RgbColor(0.05f, 0.05f, 0.05f),
//                        6,
//                        .4f ,
//                        0.97f,
//                        1,
//                        1)));

//        scene.createShape(new Sphere(
//                new Vec3(2, 1.5f, 10.0f),
//                1f,
//                new Phong(RgbColor.GRAY,
//                        RgbColor.GRAY,
//                        RgbColor.BLACK,
//                        new RgbColor(0.05f, 0.05f, 0.05f),
//                        6,
//                        .3f ,
//                        0.85f,
//                        1,
//                        1)));
//
//        scene.createShape(new Sphere(
//                new Vec3(-2, 1.5f, 10.0f),
//                1f,
//                new Phong(RgbColor.GRAY,
//                        RgbColor.GRAY,
//                        RgbColor.BLACK,
//                        new RgbColor(0.05f, 0.05f, 0.05f),
//                        6,
//                        .3f ,
//                        0.1f,
//                        1,
//                        1)));

//        refractive
//        scene.createShape(new Sphere(
//                new Vec3(-1, -.1f, 12.5f),
//                0.7f,
//                new Phong(RgbColor.WHITE,
//                        RgbColor.WHITE,
//                        RgbColor.BLACK,
//                        new RgbColor(0.08f, 0.08f, 0.08f),
//                        26,
//                        .0f,
//                        1.0f,
//                        0,
//                        1.3f)));
    }

    private static void setupBox(Scene scene) {
        //left wall
        scene.createShape(new Plane(
                new Vec3(-5, 0, 0),    //pos
                new Vec3(1, 0, 0),     //normal
                new Blinn(RgbColor.DARK_MAGENTA,        //ambient
                        RgbColor.DARK_MAGENTA,          //diffuse
                        RgbColor.BLACK,            //emision
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //right wall
        scene.createShape(new Plane(
                new Vec3(5, 0, 0),    //pos
                new Vec3(-1, 0, 0),     //normal
                new Blinn(RgbColor.DARK_GREEN,        //ambient
                        RgbColor.DARK_GREEN,          //diffuse
                        RgbColor.BLACK,        //emission
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //Floor
        scene.createShape(new Plane(
                new Vec3(0, -2, 0),    //pos
                new Vec3(0, 1, 0),     //normal
                new Blinn(RgbColor.DARK_GRAY,        //ambient
                        RgbColor.GRAY,          //diffuse
                        RgbColor.BLACK,         //emission
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        .0f,
                        1.0f,
                        1,
                        1)));

        //back wall
        scene.createShape(new Plane(
                new Vec3(0, 0, 4),    //pos
                new Vec3(0, 0, 1),     //normal
                new Blinn(RgbColor.SOFT_GRAY,        //ambient
                        RgbColor.SOFT_GRAY,          //diffuse
                        RgbColor.BLACK,        //emission
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //Ceiling
        scene.createShape(new Plane(
                new Vec3(0, 4.2f, 0),    //pos
                new Vec3(0, -1, 0),      //normal
                new Blinn(RgbColor.DARK_CYAN,      //ambient
                        RgbColor.DARK_CYAN,        //diffuse
                        RgbColor.BLACK,             //emission
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //front wall
        scene.createShape(new Plane(
                new Vec3(0, 0, 14),    //pos
                new Vec3(0, 0, -1),     //normal
                new Blinn(RgbColor.DARK_RED,        //ambient
                        RgbColor.DARK_RED,          //diffuse
                        RgbColor.BLACK,        //emission
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        0.0f,
                        1.0f,
                        1,
                        1)));


//        //circle
//        scene.createShape(new Circle(
//                new Vec3(1, 1, 6),    //pos
//                new Vec3(0, 0, 1),     //normal
//                1f,
//                new Phong(RgbColor.SOFT_GRAY,        //ambient
//                        RgbColor.SOFT_GRAY,          //diffuse
//                        RgbColor.GREEN,        //emission
//                        RgbColor.BLACK,       //specular
//                        0,
//                        .0f,
//                        1,
//                        1,
//                        1)));
    }

    private static void setupLight(Scene scene) {

//        scene.createLight(new Light(
//                new Vec3(0, 3.7f, 8f),
//                RgbColor.WHITE,
//                0.5f));

        scene.createLight(new AreaLight(
                        RgbColor.WHITE,
                        0.5f,
                        new Rectangle(
                                new Vec3(0, 4.10f, 8),    //pos
                                new Vec3(-1.3f, .0f, 0),     //a
                                new Vec3(0, .0f, -1.3f),     //b
                                new Lambert(RgbColor.WHITE,        //ambient
                                        RgbColor.WHITE,          //diffuse
                                        RgbColor.WHITE,          //emission
                                        0.f,
                                        1.0f,
                                        1,
                                        1)),
                        0.15f,
                        0.8f,
                        new Vec2(4, 4),
                        1.0f),
                true);


//        scene.createLight(new AreaLight(
//                        RgbColor.WHITE,
//                        0.5f,
//                        new Circle(
//                                new Vec3(0, 4.10f, 8),    //pos
//                                new Vec3(0, -1, 0),     //normal
//                                1.5f,
//                                new Lambert(RgbColor.WHITE,        //ambient
//                                        RgbColor.WHITE,          //diffuse
//                                        RgbColor.WHITE,          //emission
//                                        0.f,
//                                        1.0f,
//                                        1,
//                                        1)),
//                        0.11f,
//                        .9f,
//                        new Vec2(10, 3),
//                        1f),
//                true);
    }

    private static int getAntiAliasingLevel() {

        switch (ANTIALIASING_LEVEL) {
            case disabled:
                return 0;
            case x2:
                return 2;
            case x4:
                return 4;
            case x8:
                return 8;
        }
        return -1;
    }
}
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

import material.Material;
import material.Phong;
import raytracer.Raytracer;
import scene.camera.PerspCam;
import scene.light.Light;
import scene.shape.*;
import scene.shape.Rectangle;
import ui.Window;
import scene.Scene;
import utils.RgbColor;
import utils.algebra.Vec3;

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

    static int RECURSIONS = 10;
    static float AMBIENT = 0.05f;
    static Raytracer.AntiAliasingLevel ANTIALIASING_LEVEL = Raytracer.AntiAliasingLevel.x4;
    public static boolean USE_SHADOWS = true;

    /**
     * Initial method. This is where the show begins.
     **/
    public static void main(String[] args) {
        long tStart = System.currentTimeMillis();

        Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

        draw(renderWindow);

        renderWindow.exportRendering(String.valueOf(stopTime(tStart)), RECURSIONS, getAntiAliasingLevel());

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
        Raytracer raytracer = new Raytracer(renderScene, renderWindow, RECURSIONS, ANTIALIASING_LEVEL);

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

//        scene.createLight(new Light(
//                new Vec3(3, 0.5f, 6.5f),
//                RgbColor.WHITE,
//                0.4f));

        scene.createLight(new Light(
                new Vec3(0, 3.7f, 8f),
                RgbColor.WHITE,
                0.5f));

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

    private static void setupSpheres(Scene scene) {

        scene.createShape(new Sphere(
                new Vec3(0, 0, 8),
                1.0f,
                new Phong(RgbColor.BLACK,
                        RgbColor.BLACK,
                        RgbColor.BLACK,
                        new RgbColor(0.07f, 0.07f, 0.07f),
                        32f,
                        0.5f,
                        1,
                        1)));

        scene.createShape(new Sphere(
                new Vec3(-2, 1.5f, 7.5f),
                1f,
                new Phong(RgbColor.RED,
                        RgbColor.RED,
                        RgbColor.BLACK,
                        new RgbColor(0.05f, 0.05f, 0.05f),
                        26,
                        .2f,
                        1,
                        1)));

        //refractive
        scene.createShape(new Sphere(
                new Vec3(-1, -.1f, 12.5f),
                0.7f,
                new Phong(RgbColor.WHITE,
                        RgbColor.WHITE,
                        RgbColor.BLACK,
                        new RgbColor(0.08f, 0.08f, 0.08f),
                        26,
                        .0f,
                        0,
                        1.3f)));
    }

    private static void setupBox(Scene scene) {
        //left wall
        scene.createShape(new Plane(
                new Vec3(-5, 0, 0),    //pos
                new Vec3(1, 0, 0),     //normal
                new Phong(RgbColor.MAGENTA,        //ambient
                        RgbColor.MAGENTA,          //diffuse
                        RgbColor.BLACK,            //emision
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        .0f,
                        1,
                        1)));

        //right wall
        scene.createShape(new Plane(
                new Vec3(5, 0, 0),    //pos
                new Vec3(-1, 0, 0),     //normal
                new Phong(RgbColor.GREEN,        //ambient
                        RgbColor.GREEN,          //diffuse
                        RgbColor.BLACK,
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        .0f,
                        1,
                        1)));

        //back wall
        scene.createShape(new Plane(
                new Vec3(0, 0, 4),    //pos
                new Vec3(0, 0, 1),     //normal
                new Phong(RgbColor.SOFT_GRAY,        //ambient
                        RgbColor.SOFT_GRAY,          //diffuse
                        RgbColor.BLACK,
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        .0f,
                        1,
                        1)));

        //front wall
        scene.createShape(new Plane(
                new Vec3(0, 0, 14),    //pos
                new Vec3(0, 0, -1),     //normal
                new Phong(RgbColor.YELLOW,        //ambient
                        RgbColor.YELLOW,          //diffuse
                        RgbColor.BLACK,
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        0.0f,
                        1,
                        1)));

        //Floor
        scene.createShape(new Plane(
                new Vec3(0, -2, 0),    //pos
                new Vec3(0, 1, 0),     //normal
                new Phong(RgbColor.BLUE,        //ambient
                        RgbColor.BLUE,          //diffuse
                        RgbColor.BLACK,
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        .0f,
                        1,
                        1)));

        //Ceiling
        scene.createShape(new Plane(
                new Vec3(0, 4.2f, 0),    //pos
                new Vec3(0, -1, 0),     //normal
                new Phong(RgbColor.LIGHT_GRAY,        //ambient
                        RgbColor.LIGHT_GRAY,          //diffuse
                        RgbColor.BLACK,
                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
                        12,
                        .0f,
                        1,
                        1)));

//        Ceiling light rect
        scene.createShape(new Rectangle(
                new Vec3(0, 4.1f, 8),    //pos
                new Vec3(-2, .0f, 0),     //a
                new Vec3(0, .0f, -2),     //b
                new Phong(RgbColor.WHITE,        //ambient
                        RgbColor.WHITE,          //diffuse
                        RgbColor.WHITE,
                        RgbColor.BLACK,       //specular
                        12,
                        0.f,
                        1,
                        1)));
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
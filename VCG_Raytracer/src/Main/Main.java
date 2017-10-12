package Main;

import material.Blinn;
import material.Lambert;
import material.Material;
import material.Unlit;
import raytracer.Raytracer;
import scene.Scene;
import scene.camera.PerspCam;
import scene.light.AreaLight;
import scene.light.RectLight;
import scene.light.VolumeLight;
import scene.shape.Mesh;
import scene.shape.Plane;
import scene.shape.Rectangle;
import scene.shape.Sphere;
import ui.Window;
import utils.AntiAliasing;
import utils.RgbColor;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.DataImporter;
import utils.io.Log;

public class Main {


    //OUTPUT SETTINGS

    static int IMAGE_WIDTH = 800;
    static int IMAGE_HEIGHT = 600;
    static boolean DRAW_STATS = true;

    //RAYTRACER OPTIONS

    private static int RECURSIONS = 4;
    private static int RAY_DISTRIBUTION_SAMPLES = 1;
    private static int MULTI_THREADING = 4;
    private static float AMBIENT = 0.04f;
    private static AntiAliasing ANTIALIASING = new AntiAliasing(AntiAliasing.Level.x8, true, IMAGE_WIDTH, IMAGE_HEIGHT);
    public static boolean USE_SHADOWS = true;
    private static float LIGHT_SAMPLING = .5f;
    private static int NUM_LIGHTS = 6;

    private static float ROOM_SMOOTHNESS = 1.00f;
    private static float ROOM_REFLECTIVITY = 0.0f;
    private static RgbColor ROOM_SPECULAR = new RgbColor(0f, 0f, 0f);
    private static float ROOM_SPECULAREXP = 12f;

    private static Window renderWindow;
    private static long timeStart;


    public static void main(String[] args) {
        timeStart = System.currentTimeMillis();

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
        Raytracer raytracer = new Raytracer(renderScene, renderWindow, RECURSIONS, RAY_DISTRIBUTION_SAMPLES, ANTIALIASING, MULTI_THREADING, () -> renderingFinished());

        raytracer.renderScene();
    }

    /**
     * This is called after the last render Thread finished
     */
    private static void renderingFinished() {
        if (DRAW_STATS) renderWindow.exportRendering(stopTime(timeStart), RECURSIONS, ANTIALIASING, MULTI_THREADING);
        else renderWindow.exportRendering();

        Log.print(ANTIALIASING, ANTIALIASING.calcAdaptivePerformance() + "% of multisampled pixels have been saved by adaptive AA. Color Threshold: " + ANTIALIASING.colorThreshold);
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

        scene.setCamera(new PerspCam(
                new Vec3(0, 0, 17),    //pos
                new Vec3(0, 0, 0),    //center of view
                new Vec3(0, 1, 0),    //user-up
                35,
                1,
                IMAGE_WIDTH,
                IMAGE_HEIGHT));

//        scene.setCamera(new OrthoCam(
//                new Vec3(17, 17, 50),    //pos
//                new Vec3(0, 0, 7),    //center of view
//                new Vec3(0, 1, 0),    //user-up
//                12.6f,
//                IMAGE_WIDTH,
//                IMAGE_HEIGHT));

        setupSpheres(scene);
//        setupObjects(scene);
//        setupBox(scene);
        setupClassicBox(scene);
        setupLight(scene);

        return scene;
    }

    private static void setupSpheres(Scene scene) {

        //reflective
//        scene.createShape(new Sphere(
//                new Vec3(-1, -2.23f, 2.66f),
//                1.0f,
//                new Lambert(RgbColor.BLACK,
//                        RgbColor.BLACK,
//                        RgbColor.BLACK,
//                        1f,
//                        1.0f,
//                        1,
//                        1)));

//        scene.createShape(new Sphere(
//                new Vec3(-1, -2.233333333f, 2.66666666f),
//                1.0f,
//                Lambert.FULL_SMOOTH_REFLECTIVE));

        scene.createShape(new Sphere(
                new Matrix4x4(
                        new Vec3(-1, -2.033333333f, 2.66666666f),
                        new Vec3(1, 1.05f, 1)),
                Lambert.FULL_SMOOTH_REFLECTIVE));

//        //reflective blurry
//        scene.createShape(new Sphere(
//                new Vec3(2.f, -3.7f, 7),
//                1.0f,
//                new Lambert(RgbColor.SOFT_GRAY,   //ambient
//                        RgbColor.SOFT_GRAY,     //diffuse
//                        RgbColor.BLACK,         //emission
//                        0.5f,
//                        0.92f,
//                        1,
//                        1)));

        //red phong sphere
//        scene.createShape(new Sphere(
//                new Vec3(2, -1f, 10f),
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

        //mat material
//        scene.createShape(new Sphere(
//                new Vec3(-2, -1, 8.0f),
//                1f,
//                new Phong(RgbColor.GRAY,
//                        RgbColor.GRAY,
//                        RgbColor.BLACK,
//                        new RgbColor(0.05f, 0.05f, 0.05f),
//                        6,
//                        .4f ,
//                        0.98f,
//                        1,
//                        1)));

//        scene.createShape(new Sphere(
//                new Vec3(5, -2f, 3.0f),
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
//                        new Rg/bColor(0.05f, 0.05f, 0.05f),
//                        6,
//                        .3f ,
//                        0.1f,
//                        1,
//                        1)));

//        scene.createShape(new Sphere(
//                new Vec3(1.f, -2.233333f, 4.3333333f),
//                1.0f,
//                Lambert.DIAMOND));

        scene.createShape(new Sphere(
                new Matrix4x4(
                        new Vec3(1.f, -2.233333f, 4.3333333f),
                        new Vec3(1.2f, 1, 1)),
                Lambert.DIAMOND));


//        refractive
//        scene.createShape(new Sphere(
//                new Vec3(1.5f, -0.5f, 9.5f),
//                1.5f,
//                new Phong(RgbColor.WHITE,                               //ambient
//                        RgbColor.WHITE,                                 //diffuse
//                        RgbColor.BLACK,                                 //emission
//                        new RgbColor(0.04f, 0.04f, 0.04f),    //specular
//                        58,
//                        .0f,
//                        1f,
//                        0,
//                        1.8f)));

//        //refractive blurry
//        scene.createShape(new Sphere(
//                new Vec3(0.5f, -1.0f, 10.5f),
//                1.0f,
//                new Blinn(RgbColor.WHITE,                               //ambient
//                        RgbColor.WHITE,                                 //diffuse
//                        RgbColor.BLACK,                                 //emission
//                        new RgbColor(0.05f, 0.05f, 0.05f),    //specular
//                        36,
//                        .0f,
//                        0.97f,
//                        0,
//                        1.5f)));
    }

    private static void setupObjects(Scene scene) {
        //plane
//        scene.createShape(new Plane(
//                new Vec3(0,0,5),
//                new Vec3(0,0,1),
//                true,
//                new Lambert(RgbColor.SOFT_GRAY,        //ambient
//                        RgbColor.SOFT_GRAY,          //diffuse
//                        RgbColor.RED,        //emission
//                        0,
//                        1,
//                        1,
//                        1)));

        //circle
//        scene.createShape(new Circle(
//                new Vec3(1, 1, 9),    //pos
//                new Vec3(0, 0, -1),     //normal
//                0.5f,
//                true,
//                new Phong(RgbColor.SOFT_GRAY,        //ambient
//                        RgbColor.SOFT_GRAY,          //diffuse
//                        RgbColor.GREEN,        //emission
//                        RgbColor.BLACK,       //specular
//                        0,
//                        .0f,
//                        1,
//                        1,
//                        1)));

        //triangle
//        scene.createShape(new Triangle(
//                new Vec3(2, -1, 9),      //A
//                new Vec3(3, 3, 6),     //B
//                new Vec3(0, 0, 7),     //C
//                true,
//                new Phong(RgbColor.DARK_MAGENTA,        //ambient
//                        RgbColor.DARK_MAGENTA,          //diffuse
//                        RgbColor.BLACK,                 //emission
//                        new RgbColor(0.02f, 0.02f, 0.02f),       //specular
//                        32,
//                        .4f,
//                        1,
//                        1,
//                        1)));

        //box
//        scene.createShape(new Cube(
//                new Vec3(0, -1, 5),
//                new Vec3(1,1,1),
//                new Vec3(1,1,1),
//                new Vec3(1,1,1),
//                new Phong(RgbColor.DARK_CYAN,
//                        RgbColor.DARK_CYAN,
//                        RgbColor.BLACK,
//                        new RgbColor(0.00f, 0.00f, 0.00f),
//                        32f,
//                        0.5f,
//                        1.0f,
//                        1,
//                        1)
//        ));

        //mesh
        scene.createShape(new Mesh(
                new Vec3(0, 0f, 8),
                Vec3.ONE.multScalar(5),
//                DataImporter.loadOBJ("VCG_Raytracer\\models\\IronMan.obj"),
                DataImporter.loadOBJ("VCG_Raytracer\\models\\Scooter-smgrps.obj")
                //                new Phong(RgbColor.BLACK,
//                        RgbColor.BLACK,
//                        RgbColor.BLACK,
//                        new RgbColor(0.00f, 0.00f, 0.00f),
//                        32f,
//                        0.5f,
//                        1.0f,
//                        1,
//                        1),
        ));
    }

    private static void setupClassicBox(Scene scene) {
        //Floor
        scene.createShape(new Plane(
                new Vec3(0, -3.3333333f, 0),    //pos
                new Vec3(0, 1, 0),     //normal
                false,
                Lambert.WHITE));

        //left wall
        scene.createShape(new Plane(
                new Vec3(-4, 0, 0),    //pos
                new Vec3(1, 0, 0),     //normal
                false,
                Lambert.RED));

        //right wall
        scene.createShape(new Plane(
                new Vec3(4, 0, 0),    //pos
                new Vec3(-1, 0, 0),     //normal
                false,
                Lambert.BLUE));

        //back wall
        scene.createShape(new Plane(
                new Vec3(0, 0, -2),    //pos
                new Vec3(0, 0, 1),     //normal
                false,
                Lambert.WHITE));

        //Ceiling
        scene.createShape(new Plane(
                new Vec3(0, 3.3333333f, 0),    //pos
                new Vec3(0, -1, 0),      //normal
                false,
                Lambert.WHITE));

        //front wall
        scene.createShape(new Plane(
                new Vec3(0, 0, 8),    //pos
                new Vec3(0, 0, -1),     //normal
                false,
                Lambert.WHITE));
    }

    private static void setupBox(Scene scene) {

        //Floor
        scene.createShape(new Plane(
                new Vec3(0, -2, 0),    //pos
                new Vec3(0, 1, 0),     //normal
                false,
                new Blinn(RgbColor.DARK_GRAY,        //ambient
                        RgbColor.GRAY,          //diffuse
                        RgbColor.BLACK,         //emission
                        ROOM_SPECULAR,       //specular
                        ROOM_SPECULAREXP,
                        .0f,
                        1f,
                        1,
                        1)));

        //left wall
        scene.createShape(new Plane(
                new Vec3(-5, 0, 0),    //pos
                new Vec3(1, 0, 0),     //normal
                false,
                new Blinn(RgbColor.DARK_MAGENTA,        //ambient
                        RgbColor.DARK_MAGENTA,          //diffuse
                        RgbColor.BLACK,             //emision
                        ROOM_SPECULAR,              //specular
                        ROOM_SPECULAREXP,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //right wall
        scene.createShape(new Plane(
                new Vec3(5, 0, 0),    //pos
                new Vec3(-1, 0, 0),     //normal
                false,
                new Blinn(RgbColor.DARK_GREEN,        //ambient
                        RgbColor.DARK_GREEN,          //diffuse
                        RgbColor.BLACK,        //emission
                        ROOM_SPECULAR,       //specular
                        ROOM_SPECULAREXP,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //back wall
        scene.createShape(new Plane(
                new Vec3(0, 0, 3),    //pos
                new Vec3(0, 0, 1),     //normal
                false,
                new Blinn(RgbColor.SOFT_GRAY,        //ambient
                        RgbColor.SOFT_GRAY,          //diffuse
                        RgbColor.BLACK,        //emission
                        ROOM_SPECULAR,       //specular
                        ROOM_SPECULAREXP,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //Ceiling
        scene.createShape(new Plane(
                new Vec3(0, 4.f, 0),    //pos
                new Vec3(0, -1, 0),      //normal
                false,
                new Blinn(RgbColor.DARK_CYAN,      //ambient
                        RgbColor.DARK_CYAN,        //diffuse
                        RgbColor.BLACK,             //emission
                        ROOM_SPECULAR,       //specular
                        ROOM_SPECULAREXP,
                        ROOM_REFLECTIVITY,
                        ROOM_SMOOTHNESS,
                        1,
                        1)));

        //front wall
        scene.createShape(new Plane(
                new Vec3(0, 0, 14),    //pos
                new Vec3(0, 0, -1),     //normal
                false,
                new Blinn(RgbColor.BLACK,        //ambient
                        RgbColor.BLACK,          //diffuse
                        RgbColor.BLACK,        //emission
                        ROOM_SPECULAR,       //specular
                        ROOM_SPECULAREXP,
                        0.0f,
                        1.0f,
                        1,
                        1)));
    }

    private static void setupLight(Scene scene) {

//        scene.createLight(new Light(
//                new Vec3(0, 3.7f, 8f),
//                RgbColor.WHITE,
//                0.5f));

        //classic area light
//        scene.createLight(new RectLight(
//                        RgbColor.WHITE,
//                        0.5f,
//                        new Rectangle(
//                                new Vec3(0, 3.20f, 3),    //pos
//                                new Vec3(-0.6f, .0f, 0.0f),     //a
//                                new Vec3(0.0f, .0f, -0.6f),     //b
//                                true,
//                                new Unlit(RgbColor.WHITE)),
//                        0.2f,
//                        0.95f,
//                        new Vec2(NUM_LIGHTS, NUM_LIGHTS),
//                        LIGHT_SAMPLING),
//                true);

        //volume light
        scene.createLight(new VolumeLight(
                        RgbColor.WHITE,
                        0.5f,
                        new Sphere(
                                new Vec3(0, 2.60f, 3),    //pos
                                0.4f,
                                new Unlit(RgbColor.WHITE)),
                        1.4f,
                        NUM_LIGHTS * NUM_LIGHTS,
                        LIGHT_SAMPLING),
                true);

//
//        scene.createLight(new AreaLight(
//                        RgbColor.WHITE,
//                        0.5f,
//                        new Rectangle(
//                                new Vec3(0, 3.90f, 8),    //pos
//                                new Vec3(-1.0f, .0f, 0.5f),     //a
//                                new Vec3(0.5f, .0f, -1.0f),     //b
//                                false,
//                                new Unlit(RgbColor.WHITE)),
//                        0.15f,
//                        0.8f,
//                        new Vec2(NUM_LIGHTS, NUM_LIGHTS),
//                        LIGHT_SAMPLING),
//                true);


//        scene.createLight(new AreaLight(
//                        RgbColor.WHITE,
//                        0.5f,
//                        new Circle(
//                                new Vec3(0, 4.10f, 8),    //pos
//                                new Vec3(0, -1, 0),     //normal
//                                1.5f,
//        false,
//                                new Unlit(RgbColor.WHITE)),
//                        0.11f,
//                        .9f,
//                        new Vec2(NUM_LIGHTS * 2, NUM_LIGHTS / 2),
//                        LIGHT_SAMPLING),
//                true);
    }


}
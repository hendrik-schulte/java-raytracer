package Main;

import material.Blinn;
import material.Lambert;
import material.Unlit;
import raytracer.Raytracer;
import raytracer.Settings;
import scene.Scene;
import scene.SceneObject;
import scene.camera.PerspCam;
import scene.light.RectLight;
import scene.light.VolumeLight;
import scene.shape.Mesh;
import scene.shape.Plane;
import scene.shape.Rectangle;
import scene.shape.Sphere;
import ui.Window;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.DataImporter;
import utils.io.Log;

public class Main {

    public static Settings Set;

    private static Window renderWindow;

    public static void main(String[] args) {

        Set = new Settings();

        Set.timeStart = System.currentTimeMillis();

        renderWindow = new Window(Set.IMAGE_WIDTH, Set.IMAGE_HEIGHT);

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
        Raytracer raytracer = new Raytracer(renderScene, renderWindow, Set, () -> renderingFinished());

        raytracer.renderScene();
    }

    /**
     * This is called after the last render Thread finished
     */
    private static void renderingFinished() {

        renderWindow.exportRendering(Set.OUTPUT_FILE_NAME, stopTime(Set.timeStart), Set);

        Log.print(Set.ANTIALIASING, Set.ANTIALIASING.calcAdaptivePerformance() + "% of multi-sampled pixels have been saved by adaptive AA. Color Threshold: " + Set.ANTIALIASING.colorThreshold);
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
        Scene scene = new Scene(Set.AMBIENT);


//        scene.setCamera(new OrthoCam(
//                new Vec3(17, 17, 50),    //pos
//                new Vec3(0, 0, 7),    //center of view
//                new Vec3(0, 1, 0),    //user-up
//                12.6f,
//                IMAGE_WIDTH,
//                IMAGE_HEIGHT));


//        setupCustom(scene);
        setupClassic(scene);

        return scene;
    }


// - - - - - - - - - - - - - - Custom Raytracing Setup - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


    private static void setupCustom(Scene scene) {

        scene.setCamera(new PerspCam(
                new Vec3(0, 0, 17),    //pos
                new Vec3(0, 0, 0),    //center of view
                new Vec3(0, 1, 0),    //user-up
                35,
                1,
                Set.IMAGE_WIDTH,
                Set.IMAGE_HEIGHT));

//        setupObjects(scene);
        setupSpheres(scene);
        setupBox(scene);
        setupLight(scene);
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
//                new Matrix4x4(
//                        new Vec3(-1, -2.033333333f, 2.66666666f),
//                        new Vec3(1, 1.05f, 1)),
//                Lambert.FULL_SMOOTH_REFLECTIVE));


        //reflective blurry
        scene.createShape(new Sphere(
                new Vec3(-1, -1.0f, 2.66666666f),
                1.0f,
                new Lambert(RgbColor.SOFT_GRAY,   //ambient
                        RgbColor.SOFT_GRAY,     //diffuse
                        RgbColor.BLACK,         //emission
                        0.5f,
                        0.92f,
                        1,
                        1)));

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
//                new Matrix4x4(
//                        new Vec3(1.f, -2.233333f, 4.3333333f),
//                        new Vec3(1.2f, 1, 1)),
//                Lambert.DIAMOND));


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

        //refractive blurry
        scene.createShape(new Sphere(
                new Vec3(1.f, -1.f, 4.3333333f),
                1.0f,
                new Blinn(RgbColor.WHITE,                               //ambient
                        RgbColor.WHITE,                                 //diffuse
                        RgbColor.BLACK,                                 //emission
                        new RgbColor(0.05f, 0.05f, 0.05f),    //specular
                        36,
                        .0f,
                        0.97f,
                        0,
                        1.5f)));
    }

    private static void setupBox(Scene scene) {

        SceneObject boxRoot = new SceneObject(new Vec3(0, 0, 0));
        scene.createShape(boxRoot);

        float top = 2;
        float bottom = -2;
        float back = -1;
        float front = 8;
        float left = -5;
        float right = 5;

        //Floor
        new Plane(
                new Vec3(0, bottom, 0),    //pos
                new Vec3(0, 1, 0),     //normal
                false,
                new Blinn(RgbColor.DARK_GRAY,        //ambient
                        RgbColor.GRAY,          //diffuse
                        RgbColor.BLACK,         //emission
                        Set.ROOM_SPECULAR,       //specular
                        Set.ROOM_SPECULAREXP,
                        Set.ROOM_REFLECTIVITY,
                        Set.FLOOR_SMOOTHNESS,
                        1,
                        1)).setParent(boxRoot);

        //left wall
        new Plane(
                new Vec3(left, 0, 0),    //pos
                new Vec3(1, 0, 0),     //normal
                false,
                new Blinn(RgbColor.DARK_MAGENTA,        //ambient
                        RgbColor.DARK_MAGENTA,          //diffuse
                        RgbColor.BLACK,             //emision
                        Set.ROOM_SPECULAR,              //specular
                        Set.ROOM_SPECULAREXP,
                        Set.ROOM_REFLECTIVITY,
                        Set.WALL_SMOOTHNESS,
                        1,
                        1)).setParent(boxRoot);

        //right wall
        new Plane(
                new Vec3(right, 0, 0),    //pos
                new Vec3(-1, 0, 0),     //normal
                false,
                new Blinn(RgbColor.DARK_GREEN,        //ambient
                        RgbColor.DARK_GREEN,          //diffuse
                        RgbColor.BLACK,        //emission
                        Set.ROOM_SPECULAR,       //specular
                        Set.ROOM_SPECULAREXP,
                        Set.ROOM_REFLECTIVITY,
                        Set.WALL_SMOOTHNESS,
                        1,
                        1)).setParent(boxRoot);

        //back wall
        new Plane(
                new Vec3(0, 0, back),    //pos
                new Vec3(0, 0, 1),     //normal
                false,
                new Blinn(RgbColor.SOFT_GRAY,        //ambient
                        RgbColor.SOFT_GRAY,          //diffuse
                        RgbColor.BLACK,        //emission
                        Set.ROOM_SPECULAR,       //specular
                        Set.ROOM_SPECULAREXP,
                        Set.ROOM_REFLECTIVITY,
                        Set.WALL_SMOOTHNESS,
                        1,
                        1)).setParent(boxRoot);

        //Ceiling
        new Plane(
                new Vec3(0, top, 0),    //pos
                new Vec3(0, -1, 0),      //normal
                false,
                new Blinn(RgbColor.DARK_CYAN,      //ambient
                        RgbColor.DARK_CYAN,        //diffuse
                        RgbColor.BLACK,             //emission
                        Set.ROOM_SPECULAR,       //specular
                        Set.ROOM_SPECULAREXP,
                        Set.ROOM_REFLECTIVITY,
                        Set.CEILING_SMOOTHNESS,
                        1,
                        1)).setParent(boxRoot);

        //front wall
        new Plane(
                new Vec3(0, 0, front),    //pos
                new Vec3(0, 0, -1),     //normal
                false,
                new Blinn(RgbColor.BLACK,        //ambient
                        RgbColor.BLACK,          //diffuse
                        RgbColor.BLACK,        //emission
                        Set.ROOM_SPECULAR,       //specular
                        Set.ROOM_SPECULAREXP,
                        0.0f,
                        1,
                        1,
                        1)).setParent(boxRoot);
    }

    private static void setupLight(Scene scene) {

//        scene.createLight(new Light(
//                new Vec3(0, 3.7f, 8f),
//                RgbColor.WHITE,
//                0.5f));

        //classic area light


        //volume light
        scene.createLight(new VolumeLight(
                        RgbColor.WHITE,
                        0.9f,
                        new Sphere(
                                new Vec3(0, 2.60f, 3),    //pos
                                0.4f,
                                new Unlit(RgbColor.WHITE)),
                        1.7f,
                        Set.NUM_LIGHTS * Set.NUM_LIGHTS,
                        Set.LIGHT_SAMPLING),
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

// - - - - - - - - - - - - - - Classic Cornell Box - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static void setupClassic(Scene scene) {

        scene.setCamera(new PerspCam(
                new Vec3(0, 0, 17),    //pos
                new Vec3(0, 0, 0),    //center of view
                new Vec3(0, 1, 0),    //user-up
                35,
                1,
                Set.IMAGE_WIDTH,
                Set.IMAGE_HEIGHT));

        setupClassicBox(scene);
        setupClassicSpheres(scene);
        setupClassicLight(scene);
    }

    private static void setupClassicSpheres(Scene scene) {

        scene.createShape(new Sphere(
                new Vec3(-1, -2.233333333f, 2.66666666f),
                1.0f,
                Lambert.FULL_SMOOTH_REFLECTIVE));


        scene.createShape(new Sphere(
                new Vec3(1.f, -2.233333f, 4.3333333f),
                1.0f,
                Lambert.DIAMOND));

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

    private static void setupClassicLight(Scene scene) {
        scene.createLight(new RectLight(
                        RgbColor.WHITE,
                        0.5f,
                        new Rectangle(
                                new Vec3(0, 3.20f, 3),    //pos
                                new Vec3(-0.6f, .0f, 0.0f),     //a
                                new Vec3(0.0f, .0f, -0.6f),     //b
                                true,
                                new Unlit(RgbColor.WHITE)),
                        0.2f,
                        0.95f,
                        new Vec2(Set.NUM_LIGHTS, Set.NUM_LIGHTS),
                        Set.LIGHT_SAMPLING),
                true);
    }
}
package scene;

import material.*;
import raytracer.Settings;
import scene.camera.PerspCam;
import scene.light.Light;
import scene.light.RectLight;
import scene.light.VolumeLight;
import scene.shape.*;
import utils.RgbColor;
import utils.algebra.Matrix4x4;
import utils.algebra.Quaternion;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.DataImporter;
import utils.io.Log;

public class SceneFactory {

    public static Scene createCustom(Settings settings) {

        Scene scene = new Scene(settings.AMBIENT);

        createPerpCam(scene, settings);

//        setupObjects(scene);
        setupSpheres(scene);
        setupBox(scene, settings);
        setupLight(scene, settings);
//        setupTest(scene, settings);

        return scene;
    }

    public static Scene createClassic(Settings settings) {
        Scene scene = new Scene(settings.AMBIENT);

        createPerpCam(scene, settings);

        setupClassicBox(scene);
        setupClassicSpheres(scene);
        setupClassicLight(scene, settings);

        return scene;
    }

    private static void createPerpCam(Scene scene, Settings settings) {
        scene.setCamera(new PerspCam(
                new Vec3(0, 0, 17),    //pos
                new Vec3(0, 0, 0),    //center of view
                new Vec3(0, 1, 0),    //user-up
                35,
                1,
                settings.IMAGE_WIDTH,
                settings.IMAGE_HEIGHT));
    }

    //region Custom Raytracing Setup

    private static void setupTest(Scene scene, Settings settings) {
        new Plane(
                new Vec3(0, 0, 8),    //pos
                new Vec3(0, 0, -1),     //normal
                false,
                new Blinn(RgbColor.BLACK,        //ambient
                        RgbColor.BLACK,          //diffuse
                        RgbColor.BLACK,        //emission
                        settings.ROOM_SPECULAR,       //specular
                        settings.ROOM_SPECULAREXP,
                        0.0f,
                        1,
                        1,
//                        1)).setParent(boxRoot);
                        1)).setParent(scene.root);


        scene.createShape(new Sphere(
                new Vec3(0, -2f, 3.0f),
                1f,
                Phong.SMOOTH_BLUE));
    }

    private static void setupSpheres(Scene scene) {

        SceneObject sphereRoot = new SceneObject("Sphere Root", new Vec3(0, 1, 0));
        scene.createShape(sphereRoot);

        sphereRoot.setChild(new Sphere(
                new Vec3(-2, -1, 2),
                1.5f,
                Lambert.BLUE));

        sphereRoot.setChild(new Sphere(
                new Vec3(2, -1, 2),
                1.0f,
                Lambert.RED));

        //reflective
//        scene.createShape(new Sphere(
//                new Vec3(-1, -2.23f, 2.66f),
//                1.0f,
//                Lambert.FULL_SMOOTH_REFLECTIVE));


//        scene.createShape(new Sphere(
//                new Matrix4x4(
//                        new Vec3(-1, -2.033333333f, 2.66666666f),
//                        new Vec3(1, 1.05f, 1)),
//                Lambert.FULL_SMOOTH_REFLECTIVE));


        //reflective blurry
//        scene.createShape(new Sphere(
//                new Vec3(-1, -2.0f, 2.66666666f),
//                1.0f,
//                Lambert.GREY_BLURRY_REFLECTIVE));
//
//        scene.createShape(new Sphere(
//                new Vec3(2, -2f, 4f),
//                1f,
//                Lambert.DIAMOND_VERY_ROUGH));

//        scene.createShape(new Sphere(
//                new Vec3(0, -2f, 3.0f),
//                1f,
//                Phong.SMOOTH_GREY));
//
//        scene.createShape(new Sphere(
//                new Vec3(-2, 1.5f, 10.0f),
//                1f,
//                Lambert.GREY_ROUGH_BLURRY_REFLECTIVE));

//        scene.createShape(new Sphere(
//                new Matrix4x4(
//                        new Vec3(1.f, -2.233333f, 4.3333333f),
//                        new Vec3(1.2f, 1, 1)),
//                Lambert.DIAMOND));

        //refractive blurry
//        scene.createShape(new Sphere(
//                new Vec3(1.f, -1.f, 4.3333333f),
//                1.0f,
//                Lambert.DIAMOND_VERY_ROUGH));
    }

    private static void setupBox(Scene scene, Settings settings) {

        SceneObject boxRoot = new SceneObject("Walls", new Matrix4x4(
                Quaternion.euler(0,0,0),
                new Vec3(0,0,0),                      //position
                new Vec3(3,3,3)));   //setScale
        scene.createShape(boxRoot);

        float top = 1;
        float bottom = -1;
        float back = -1;
        float front = 1;
        float left = -1;
        float right = 1;

        //Floor
        boxRoot.setChild(new Plane(
                new Vec3(0, bottom, 0),    //pos
                new Vec3(0, 1, 0),     //normal
                false,
                Blinn.CreateWall(settings, RgbColor.LIGHT_GRAY, true)));

        //left wall
        boxRoot.setChild(new Plane(
                new Vec3(left, 0, 0),    //pos
                new Vec3(1, 0, 0),     //normal
                false,
                Blinn.CreateWall(settings, RgbColor.DARK_MAGENTA, false)));

        //right wall
        boxRoot.setChild(new Plane(
                new Vec3(right, 0, 0),    //pos
                new Vec3(-1, 0, 0),     //normal
                false,
                Blinn.CreateWall(settings, RgbColor.DARK_GREEN, false)));

        //back wall
//        boxRoot.setChild(new Plane(
//                new Vec3(0, 0, back),    //pos
//                new Vec3(0, 0, 1),     //normal
//                false,
//                Blinn.CreateWall(settings, RgbColor.SOFT_GRAY, false)));

        //Ceiling
//        boxRoot.setChild(new Plane(
//                new Vec3(0, top, 0),    //pos
//                new Vec3(0, -1, 0),      //normal
//                false,
//                Blinn.CreateWall(settings, RgbColor.DARK_CYAN, false)));

        //front wall
//        boxRoot.setChild(new Plane(
//                new Vec3(0, 0, front),    //pos
//                new Vec3(0, 0, -1),     //normal
//                false,
//                Blinn.CreateWall(settings, RgbColor.BLACK, false)));
    }

    private static void setupLight(Scene scene, Settings settings) {

        Vec3 center = new Vec3(0, 3.7f, 3f);
        float intensity = 0.80f;

        if (!settings.USE_AREA_LIGHTS) {

            scene.createLight(new Light(
                    center,
                    RgbColor.WHITE,
                    intensity));

            return;
        }

        //volume light
//        scene.createLight(new VolumeLight(
//                        RgbColor.WHITE,
//                        intensity,
//                        new Sphere(
//                                center,    //pos
//                                0.4f,
//                                Unlit.EMMISIVE_WHITE),
//                        1.7f,
//                        settings.NUM_LIGHTS * settings.NUM_LIGHTS,
//                        settings.LIGHT_SAMPLING),
//                true);

        scene.createLight(new RectLight(
                        RgbColor.WHITE,
                        intensity,
                        new Rectangle(
                                center,    //pos
                                new Vec3(-1.5f, .0f, 0.0f),     //a
                                new Vec3(0.05f, .0f, -1.5f),     //b
                                false,
                                Unlit.EMMISIVE_WHITE),
                        0.15f,
                        0.8f,
                        new Vec2(settings.NUM_LIGHTS, settings.NUM_LIGHTS),
                        settings.LIGHT_SAMPLING),
                true);
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

    //endregion

    //region Classic Cornell Box

    private static void setupClassicSpheres(Scene scene) {

        scene.createShape(new Sphere(
                new Vec3(-1, -2.233333333f, 2.66666666f),
                1.0f,
                Lambert.FULL_SMOOTH_REFLECTIVE));


        scene.createShape(new Sphere(
                new Vec3(1.f, -2.233333f, 4.3333333f),
                1.0f,
                Lambert.DIAMOND_SMOOTH));

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

    private static void setupClassicLight(Scene scene, Settings settings) {

        Vec3 center = new Vec3(0, 3.20f, 3);
        float intensity = 0.5f;

        if (!settings.USE_AREA_LIGHTS) {

            scene.createLight(new Light(
                    center,
                    RgbColor.WHITE,
                    intensity));

            return;
        }

        scene.createLight(new RectLight(
                        RgbColor.WHITE,
                        intensity,
                        new Rectangle(
                                new Vec3(0, 3.20f, 3),    //pos
                                new Vec3(-0.6f, .0f, 0.0f),     //a
                                new Vec3(0.0f, .0f, -0.6f),     //b
                                true,
                                Unlit.EMMISIVE_WHITE),
                        0.2f,
                        0.95f,
                        new Vec2(settings.NUM_LIGHTS, settings.NUM_LIGHTS),
                        settings.LIGHT_SAMPLING),
                true);
    }


    //endregion
}

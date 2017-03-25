/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    1. Send primary ray
    2. intersection test with all shapes
    3. if hit:
    3a: send secondary ray to the light source
    3b: 2
        3b.i: if hit:
            - Shape is in the shade
            - Pixel color = ambient value
        3b.ii: in NO hit:
            - calculate local illumination
    4. if NO hit:
        - set background color

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package raytracer;

import material.Material;
import scene.Scene;

import scene.camera.Camera;
import ui.Window;
import utils.*;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Raytracer {

    private RgbColor BackgroundColor = RgbColor.YELLOW;
    private BufferedImage mBufferedImage;
    private Scene scene;
    private Window mRenderWindow;

    private Camera cam;
    private Vec3 windowCenter;
    private float windowHeight;
    private float windowWidth;

    private int mMaxRecursions;
    private int rayDistributionSamples;
    private int multiThreading;
    private int threadsFinished;
    private Callback renderCallback;

    private AntiAliasingLevel antiAliasingLevel;
    private ArrayList<Vec2> antiAliasingPositions = new ArrayList<>();

    public enum AntiAliasingLevel {
        disabled,
        x2,
        x4,
        x8
    }

    public Raytracer(Scene scene, Window renderWindow, int recursions, int rayDistributionSamples, AntiAliasingLevel antiAliasingLevel, int multiThreading, Callback callback) {
        Log.print(this, "Init");
        mMaxRecursions = recursions;
        this.rayDistributionSamples = rayDistributionSamples;
        this.multiThreading = multiThreading;
        renderCallback = callback;
        this.antiAliasingLevel = antiAliasingLevel;
        mBufferedImage = renderWindow.getBufferedImage();

        this.scene = scene;
        cam = scene.camera;

        mRenderWindow = renderWindow;
    }

    public void renderScene() {
        Log.print(this, "Start rendering");

        cameraCalculation();

        startRenderThreads();
    }

    private void cameraCalculation() {
        float ratio = (float) mBufferedImage.getWidth() / (float) mBufferedImage.getHeight();
        windowHeight = (float) (2 * cam.focalLength * Math.tan(cam.viewAngle * Math.PI / 360));
        windowWidth = ratio * windowHeight;
        windowCenter = cam.getPosition().add(cam.view.multScalar(cam.focalLength));

        generateAADistribution();

//        Log.print(this, "resolution: " + mBufferedImage.getWidth() + " x " + mBufferedImage.getHeight());
//        Log.print(this, "ratio: " + ratio);
//        Log.print(this, "window height: " + windowHeight);
//        Log.print(this, "window width: " + windowWidth);
//        Log.print(this, "window center: " + windowCenter);
    }

    private void startRenderThreads() {
        threadsFinished = 0;
        int parts = mBufferedImage.getHeight() / multiThreading;

        for (int i = 0; i * 1 < multiThreading; i++) {

            int startRow = i * parts;
            int endRow = startRow + parts;

            if (i + 1 == multiThreading) endRow = mBufferedImage.getHeight();

            Thread renderThread = new RenderThread(this, 0, mBufferedImage.getWidth(), startRow, endRow, this::threadFinished);

            renderThread.start();
        }
    }

    private void threadFinished() {
        threadsFinished++;

        if (threadsFinished < multiThreading) return;

        Log.print(this, "All render threads finished!");
        renderCallback.callback();
    }

    public void renderBlock(int startX, int endX, int startY, int endY) {
        // Rows
        for (int y = startY; y < endY; y++) {
            // Columns
            for (int x = startX; x < endX; x++) {

                Vec2 normPos = pixelPosNormalized(x, y);

                if (antiAliasingLevel == AntiAliasingLevel.disabled) {
                    Vec3 worldPos = screen2World(windowCenter, normPos, windowWidth, windowHeight);

                    Ray ray = new Ray(cam.getPosition(), worldPos.sub(cam.getPosition()));

                    mRenderWindow.setPixel(mBufferedImage, traceRay(ray, 0), new Vec2(x, y));

                } else {
                    ArrayList<Ray> rays = new ArrayList<>();

                    for (Vec2 pos : antiAliasingPositions) {

                        Vec3 worldPos = screen2World(windowCenter, normPos.add(pos), windowWidth, windowHeight);

                        Ray ray = new Ray(cam.getPosition(), worldPos.sub(cam.getPosition()));

                        rays.add(ray);
                    }

                    mRenderWindow.setPixel(mBufferedImage, averageColor(rays, 0), new Vec2(x, y));
                }
            }
//                if(y % 10 == 0 && x % 10 == 0) Log.print(this, "pixel: " + x + "," + y + "\nnormPos:  " + normPos + "\nworldPos: " + worldPos);
        }
    }

    private RgbColor averageColor(List<Ray> rays, int currentRecursion) {

        Vec3 colorVec = new Vec3(0, 0, 0);

        for (Ray ray : rays) {
            RgbColor color = traceRay(ray, currentRecursion);

            colorVec = colorVec.add(new Vec3(color.red(), color.green(), color.blue()));
        }

        if (!rays.isEmpty()) colorVec = colorVec.divideScalar(rays.size());

        return new RgbColor(colorVec.x, colorVec.y, colorVec.z);
    }

    private RgbColor traceRay(Ray ray, int currentRecursion) {

        Intersection inter = ray.getIntersection(scene.shapeList);

        if (inter == null) return BackgroundColor;

//        if(inter.shape instanceof Plane) Log.print(this, "plane intersec");

//        if (debug) {
//            Log.print(this, "debug ray intersec: ");
//            Log.print(this, inter.toString());
//        }

        RgbColor color = RgbColor.BLACK;
        Material material = inter.shape.material;


        if (material.opacity > 0) {
            RgbColor materialColor = material.getColor(inter.interSectionPoint, inter.normal, ray.getDirection().negate(), scene);
            color = color.add(materialColor.multScalar(material.opacity));
        }

        if (currentRecursion >= mMaxRecursions) return color;

        if (material.opacity < 1) {
            //calc refraction vector
//            Vec3 refractionVector = Material.getRefractionVector(inter.normal, ray.getDirection(), 1, material.refractiveIndex);
            Vec3 refractionVector = material.getRefractionVector(inter.normal, ray.getDirection());

//            if (!refractionVector.equals(Vec3.ZERO)) {

            //calc refraction ray
            Ray refrRay = new Ray(inter.interSectionPoint, refractionVector);
            //recursively trace refraction ray
            RgbColor refractionColor = averageColor(material.getDistributedRays(refrRay, rayDistributionSamples), currentRecursion + 1);

            color = color.add(refractionColor.multScalar(1 - material.opacity));
//            }
        }

        if (material.reflection > 0) {
            //calc reflection ray
            Ray reflRay = new Ray(inter.interSectionPoint, Material.getReflectionVector(inter.normal, ray.getDirection().multScalar(-1)));
            //recursively trace reflection ray
            RgbColor reflectionColor = averageColor(material.getDistributedRays(reflRay, rayDistributionSamples), currentRecursion + 1);


            color = color.add(reflectionColor.multScalar(material.reflection));
        }

        return color;
    }

    private Vec3 screen2World(Vec3 windowCenter, Vec2 normPos, float windowWidth, float windowHeight) {

        return windowCenter
                .add(scene.camera.camUp.multScalar(normPos.y * windowHeight / -2f))
                .add(scene.camera.side.multScalar(normPos.x * windowWidth / 2f));
    }

    private Vec2 pixelPosNormalized(float x, float y) {
        return new Vec2(2 * ((x + 0.5f) / mBufferedImage.getWidth()) - 1,
                2 * ((y + 0.5f) / mBufferedImage.getHeight()) - 1);
    }

    private void generateAADistribution() {
        if (antiAliasingLevel != AntiAliasingLevel.disabled) {
            float pixelSizeNormPos = 2 / (float) mBufferedImage.getWidth(); //relative pixel size relative to resolution

            switch (antiAliasingLevel) {
                case x2:
                    float q4 = pixelSizeNormPos / 4;

                    antiAliasingPositions.add(new Vec2(-q4, -q4));
                    antiAliasingPositions.add(new Vec2(-q4, q4));
                    antiAliasingPositions.add(new Vec2(q4, -q4));
                    antiAliasingPositions.add(new Vec2(q4, q4));
                    break;
                case x4:
                    float q8 = pixelSizeNormPos / 8;

                    //upper left corner
                    antiAliasingPositions.add(new Vec2(-3 * q8, q8));
                    antiAliasingPositions.add(new Vec2(-q8, 3 * q8));
                    //bottom left corner
                    antiAliasingPositions.add(new Vec2(-3 * q8, -q8));
                    antiAliasingPositions.add(new Vec2(-q8, -3 * q8));
                    //upper right corner
                    antiAliasingPositions.add(new Vec2(3 * q8, q8));
                    antiAliasingPositions.add(new Vec2(q8, 3 * q8));
                    //bottom right
                    antiAliasingPositions.add(new Vec2(3 * q8, -q8));
                    antiAliasingPositions.add(new Vec2(q8, -3 * q8));
                    break;

                case x8:
                    q8 = pixelSizeNormPos / 8;

                    //upper left corner
                    antiAliasingPositions.add(new Vec2(-q8, q8));
                    antiAliasingPositions.add(new Vec2(-3 * q8, 3 * q8));
                    antiAliasingPositions.add(new Vec2(-3 * q8, q8));
                    antiAliasingPositions.add(new Vec2(-q8, 3 * q8));
                    //bottom left corner
                    antiAliasingPositions.add(new Vec2(-q8, -q8));
                    antiAliasingPositions.add(new Vec2(-3 * q8, -3 * q8));
                    antiAliasingPositions.add(new Vec2(-3 * q8, -q8));
                    antiAliasingPositions.add(new Vec2(-q8, -3 * q8));
                    //upper right corner
                    antiAliasingPositions.add(new Vec2(q8, q8));
                    antiAliasingPositions.add(new Vec2(3 * q8, 3 * q8));
                    antiAliasingPositions.add(new Vec2(3 * q8, q8));
                    antiAliasingPositions.add(new Vec2(q8, 3 * q8));
                    //bottom right
                    antiAliasingPositions.add(new Vec2(q8, -q8));
                    antiAliasingPositions.add(new Vec2(3 * q8, -3 * q8));
                    antiAliasingPositions.add(new Vec2(3 * q8, -q8));
                    antiAliasingPositions.add(new Vec2(q8, -3 * q8));
                    break;
            }
        }
    }
}

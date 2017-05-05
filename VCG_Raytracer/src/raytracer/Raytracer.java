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
import java.awt.image.RGBImageFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Raytracer {

    private RgbColor BackgroundColor = RgbColor.YELLOW;
    private BufferedImage mBufferedImage;
    private Scene scene;
    private Window mRenderWindow;

    private Camera cam;
//    private Vec3 windowCenter;
//    private float windowHeight;
//    private float windowWidth;

    private int mMaxRecursions;
    private int rayDistributionSamples;
    private int multiThreading;
    private int threadsFinished;
    private Callback renderCallback;

    private AntiAliasingLevel antiAliasingLevel;
    private ArrayList<Vec2> antiAliasingPositions = new ArrayList<>();

    private List<RenderBlock> renderBlocks = Collections.synchronizedList(new ArrayList<RenderBlock>());
    private final int renderBlockSize = 10;

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

        Log.print(this, "Preliminary calculation");

        cameraCalculation();
        generateAADistribution(antiAliasingLevel);

        generateRenderBlocks();

//        renderPixel(455, 300);
//        renderPixel(455, 301);
//        renderPixel(586, 312);
//        renderPixel(586, 313);
//        renderBlock(586, 589, 312, 314);

        Log.print(this, "Start rendering");

        startRenderThreads();
    }

    private void renderPixel(int x, int y) {
        Log.print(this, "calc Pixel (" + x + "," + y + ")");
        renderBlock(x, x + 1, y, y + 1);
    }

    private void cameraCalculation() { //todo move to camera class
//        float ratio = (float) mBufferedImage.getWidth() / (float) mBufferedImage.getHeight();
//        windowHeight = (float) (2 * cam.focalLength * Math.tan(cam.viewAngle * Math.PI / 360));
//        windowWidth = ratio * windowHeight;
//        windowCenter = cam.getPosition().add(cam.view.multScalar(cam.focalLength));



//        Log.print(this, "resolution: " + mBufferedImage.getWidth() + " x " + mBufferedImage.getHeight());
//        Log.print(this, "ratio: " + ratio);
//        Log.print(this, "window height: " + windowHeight);
//        Log.print(this, "window width: " + windowWidth);
//        Log.print(this, "window center: " + windowCenter);
    }

    private void generateRenderBlocks() {

        int rows = mBufferedImage.getHeight() / renderBlockSize;
        int columns = mBufferedImage.getWidth() / renderBlockSize;

        //columns
        for (int x = 0; x < columns; x++) {

            int startX = x * renderBlockSize;
            int endX = startX + renderBlockSize;
            if (x + 1 == columns) endX = mBufferedImage.getWidth();

            //rows
            for (int y = 0; y < rows; y++) {

                int startY = y * renderBlockSize;
                int endY = startY + renderBlockSize;
                if (y + 1 == rows) endY = mBufferedImage.getHeight();

                renderBlocks.add(new RenderBlock(startX, endX, startY, endY));
            }
        }

        Collections.shuffle(renderBlocks);
    }

    private void startRenderThreads() {
        threadsFinished = 0;
//        int parts = mBufferedImage.getHeight() / multiThreading;
//
//        for (int i = 0; i < multiThreading; i++) {
//
//            int startRow = i * parts;
//            int endRow = startRow + parts;
//
//            if (i + 1 == multiThreading) endRow = mBufferedImage.getHeight();
//
//            RenderBlock block = new RenderBlock(0, mBufferedImage.getWidth(), startRow, endRow);
//
//            Thread renderThread = new RenderThread(this, block, this::threadFinished);
//
//            renderThread.start();
//        }

        for (int i = 0; i < multiThreading; i++) {

            Thread renderThread = new RenderThread(this, renderBlocks, this::threadFinished);

            renderThread.start();
        }
    }

    private synchronized void threadFinished() {
        threadsFinished++;

        if (threadsFinished < multiThreading) return;

        Log.print(this, "All render threads finished!");
        renderCallback.callback();
    }

    public void renderBlock(RenderBlock rect) {
        renderBlock(rect.startX, rect.endX, rect.startY, rect.endY);
    }

    public void renderBlock(int startX, int endX, int startY, int endY) {
        // Rows
        for (int y = startY; y < endY; y++) {
            // Columns
            for (int x = startX; x < endX; x++) {

                Vec2 normPos = pixelPosNormalized(x, y);

                if (antiAliasingLevel == AntiAliasingLevel.disabled) {
//                    Vec3 worldPos = cam.norm2World(normPos);
//
//                    Ray ray = new Ray(cam.getPosition(), worldPos.sub(cam.getPosition()));

                    Ray ray = cam.calcPixelRay(normPos);

                    mRenderWindow.setPixel(mBufferedImage, traceRay(ray, 0), new Vec2(x, y));

                } else {
                    ArrayList<Ray> rays = new ArrayList<>();

                    for (Vec2 pos : antiAliasingPositions) {

//                        Vec3 worldPos = cam.norm2World(normPos.add(pos));
//                        Ray ray = new Ray(cam.getPosition(), worldPos.sub(cam.getPosition()));

                        rays.add(cam.calcPixelRay(normPos.add(pos)));
                    }

                    mRenderWindow.setPixel(mBufferedImage, traceRays(rays, 0), new Vec2(x, y));
                }
            }
//                if(y % 10 == 0 && x % 10 == 0) Log.print(this, "pixel: " + x + "," + y + "\nnormPos:  " + normPos + "\nworldPos: " + worldPos);
        }
    }

    private RgbColor traceRays(List<Ray> rays, int currentRecursion) {

        Vec3 colorVec = new Vec3(0, 0, 0);

        for (Ray ray : rays) {
            RgbColor color = traceRay(ray, currentRecursion);

            colorVec = colorVec.add(new Vec3(color.red(), color.green(), color.blue()));
        }

        if (!rays.isEmpty()) colorVec = colorVec.divideScalar(rays.size());

        return new RgbColor(colorVec.x, colorVec.y, colorVec.z);
    }

    private RgbColor traceRay(Ray ray, int currentRecursion) {

//        Log.print(this, "Ray " + ray + " rec: " + currentRecursion);

        Intersection intersection = ray.getIntersection(scene.shapeList);

        if (intersection == null) return BackgroundColor;

        RgbColor color = RgbColor.BLACK;
        Material material = intersection.shape.material;
        Vec3 viewVector = ray.getDirection().negate().normalize();

//        Log.print(this, intersection.toString());

        color = color.add(traceIllumination(intersection, viewVector, material));

        if (currentRecursion >= mMaxRecursions) {
//            Log.print(this, "max recursion reached");
            return color;
        }

        if (material.opacity < 1)
            color = color.add(traceRefraction(intersection, viewVector, material, currentRecursion));

        if (material.reflection > 0)
            color = color.add(traceReflection(intersection, viewVector, material, currentRecursion));

        return color;
    }

    private RgbColor traceIllumination(Intersection inter, Vec3 viewVector, Material material) {

        return material.getColor(inter.interSectionPoint, inter.normal, viewVector, scene);
    }

    private RgbColor traceRefraction(Intersection inter, Vec3 viewVector, Material material, int currentRecursion) {
        //calc refraction vector
        Vec3 refractionVector = material.getRefractionVector(inter.normal, viewVector);

        //calc ideal refraction ray
        Ray refractionRay = new Ray(inter.interSectionPoint, refractionVector);

        //get distributed rays (in case of diffuse refraction)
        List<Ray> rays = material.getDistributedRays(refractionRay, rayDistributionSamples);

        //recursively trace refraction rays
        RgbColor refractionColor = traceRays(rays, currentRecursion + 1);

        return refractionColor.multScalar(material.transparency);
    }

    private RgbColor traceReflection(Intersection inter, Vec3 viewVector, Material material, int currentRecursion) {
        //calc reflection ray
        Ray reflectionRay = new Ray(inter.interSectionPoint, Material.getReflectionVector(inter.normal, viewVector));

        //get distributed rays (in case of diffuse reflection)
        List<Ray> rays = material.getDistributedRays(reflectionRay, rayDistributionSamples);
        //recursively trace reflection rays
        RgbColor reflectionColor = traceRays(rays, currentRecursion + 1);

        return reflectionColor.multScalar(material.reflection);
    }

    private Vec2 pixelPosNormalized(float x, float y) {
        //Todo in camera verscvhieben
        return new Vec2(2 * ((x + 0.5f) / mBufferedImage.getWidth()) - 1,
                2 * ((y + 0.5f) / mBufferedImage.getHeight()) - 1);
    }

    private void generateAADistribution(AntiAliasingLevel antiAliasingLevel) {
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

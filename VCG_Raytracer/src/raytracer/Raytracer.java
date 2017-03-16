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
import scene.shape.Plane;
import scene.shape.Shape;
import ui.Window;
import utils.*;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Raytracer {

    private RgbColor BackgroundColor = RgbColor.BLACK;
    private BufferedImage mBufferedImage;
    private Scene scene;
    private Window mRenderWindow;

    private int mMaxRecursions;
    private AntiAliasingLevel antiAliasingLevel;
    ArrayList<Vec2> antiAliasingPositions = new ArrayList<>();

    public enum AntiAliasingLevel {
        disabled,
        x2,
        x4,
        x8
    }

//    private Vec2 antiAliasingResolution;

    public Raytracer(Scene scene, Window renderWindow, int recursions, AntiAliasingLevel antiAliasingLevel) {
        Log.print(this, "Init");
        mMaxRecursions = recursions;
        this.antiAliasingLevel = antiAliasingLevel;
        mBufferedImage = renderWindow.getBufferedImage();
//        antiAliasingResolution = new Vec2(mBufferedImage.getWidth() * antiAliasingLevel, mBufferedImage.getHeight() * antiAliasingLevel);


        this.scene = scene;
        mRenderWindow = renderWindow;
    }

    public void renderScene() {
        Log.print(this, "Start rendering");

        Camera cam = scene.camera;

        float ratio = (float) mBufferedImage.getWidth() / (float) mBufferedImage.getHeight();
        float windowHeight = (float) (2 * cam.focalLength * Math.tan(cam.viewAngle * Math.PI / 360));
//        float windowHeight = (float) (cam.focalLength * Math.tan(cam.viewAngle * Math.PI / 180));
        float windowWidth = ratio * windowHeight;
        Vec3 windowCenter = cam.getPosition().add(cam.view.multScalar(cam.focalLength));

        generateAADistribution();


//        Log.print(this, "resolution: " + mBufferedImage.getWidth() + " x " + mBufferedImage.getHeight());
//        Log.print(this, "ratio: " + ratio);
//        Log.print(this, "window height: " + windowHeight);
//        Log.print(this, "window width: " + windowWidth);
//        Log.print(this, "window center: " + windowCenter);

        Log.print(this, "0,0 = " + pixelPosNormalized(0, 0));
        Log.print(this, "800,600 = " + pixelPosNormalized(799, 599));
        Log.print(this, "0,600 = " + pixelPosNormalized(0, 599));
        Log.print(this, "800,0 = " + pixelPosNormalized(799, 0));


        // Rows
        for (int y = 0; y < mBufferedImage.getHeight(); y++) {
            // Columns
            for (int x = 0; x < mBufferedImage.getWidth(); x++) {

                Vec2 normPos = pixelPosNormalized(x, y);

                Vec3 colorVec = new Vec3(0, 0, 0);

                if (antiAliasingLevel == AntiAliasingLevel.disabled) {
                    Vec3 worldPos = screen2World(windowCenter, normPos, windowWidth, windowHeight);

                    Ray ray = new Ray(cam.getPosition(), worldPos.sub(cam.getPosition()));

                    mRenderWindow.setPixel(mBufferedImage, traceRay(ray, 0), new Vec2(x, y));

                } else {
                    for (Vec2 pos : antiAliasingPositions) {

                        Vec3 worldPos = screen2World(windowCenter, normPos.add(pos), windowWidth, windowHeight);

                        Ray ray = new Ray(cam.getPosition(), worldPos.sub(cam.getPosition()));

                        RgbColor color = traceRay(ray, 0);

                        colorVec = colorVec.add(new Vec3(color.red(), color.green(), color.blue()));
                    }

                    colorVec = colorVec.devideScalar(antiAliasingPositions.size());

                    mRenderWindow.setPixel(mBufferedImage, new RgbColor(colorVec.x, colorVec.y, colorVec.z), new Vec2(x, y));
                }
            }

//                if(y % 10 == 0 && x % 10 == 0) Log.print(this, "pixel: " + x + "," + y + "\nnormPos:  " + normPos + "\nworldPos: " + worldPos);
        }
    }

    private RgbColor traceRay(Ray ray, int currentRecursion) {

//        Log.print(this, "start tracing ray from " + ray.getStartPoint() + " current recursion level: " + currentRecursion);

        Intersection inter = ray.getIntersection(scene.shapeList);

        if (inter == null) return BackgroundColor;

//        if(inter.shape instanceof Plane) Log.print(this, "plane intersec");

//        if (debug) {
//            Log.print(this, "debug ray intersec: ");
//            Log.print(this, inter.toString());
//        }

        Material material = inter.shape.material;
        float refl = material.reflection;

        RgbColor color = material.getColor(inter.interSectionPoint, inter.normal, ray.getDirection().multScalar(-1), scene);

        if (currentRecursion >= mMaxRecursions || refl == 0) return color;
        else {
            //calc reflection ray
            Ray reflRay = new Ray(inter.interSectionPoint, Material.getReflectionVector(inter.normal, ray.getDirection().multScalar(-1)));
            //recursively trace reflection ray
            RgbColor reflectionColor = traceRay(reflRay, currentRecursion + 1);

            return color.add(reflectionColor.multScalar(material.reflection));
        }
    }

//    private RgbColor shade() {
//        return new RgbColor(0, 0, 0);
//    }
//
//    private RgbColor traceIllumination() {
//        return new RgbColor(0, 0, 0);
//    }

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
//            float pixelSize = windowHeight / (float) mBufferedImage.getHeight();
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

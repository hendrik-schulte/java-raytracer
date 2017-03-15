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
import java.util.Collections;
import java.util.Comparator;

public class Raytracer {

    private RgbColor BackgroundColor = RgbColor.BLACK;
    private BufferedImage mBufferedImage;
    private Scene scene;
    private Window mRenderWindow;

    private int mMaxRecursions;

    public Raytracer(Scene scene, Window renderWindow, int recursions) {
        Log.print(this, "Init");
        mMaxRecursions = recursions;
        mBufferedImage = renderWindow.getBufferedImage();

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


//        Log.print(this, "resolution: " + mBufferedImage.getWidth() + " x " + mBufferedImage.getHeight());
//        Log.print(this, "ratio: " + ratio);
//        Log.print(this, "window height: " + windowHeight);
//        Log.print(this, "window width: " + windowWidth);
//        Log.print(this, "window center: " + windowCenter);


        // Rows
        for (int y = 0; y < mBufferedImage.getHeight(); y++) {
            // Columns
            for (int x = 0; x < mBufferedImage.getWidth(); x++) {

                Vec2 normPos = pixelPosNormalized(x, y);
                Vec3 worldPos = windowCenter
                        .add(cam.camUp.multScalar(normPos.y * windowHeight / -2f))
                        .add(cam.side.multScalar(normPos.x * windowWidth / 2f));

                Ray ray = new Ray(cam.getPosition(), worldPos.sub(cam.getPosition()));

//                mRenderWindow.setPixel(mBufferedImage, rayToColor(ray), new Vec2(x,y));
                mRenderWindow.setPixel(mBufferedImage, traceRay(ray, 0), new Vec2(x, y));

//                if(y % 10 == 0 && x % 10 == 0) Log.print(this, "pixel: " + x + "," + y + "\nnormPos:  " + normPos + "\nworldPos: " + worldPos);
            }
        }
    }


    private RgbColor rayToColor(Ray ray) {
        return new RgbColor(ray.getDirection().normalize());
    }

//    private RgbColor sendPrimaryRay(Ray ray) {
//
//        return sendPrimaryRay(ray, false);
//    }
//
//    private RgbColor sendPrimaryRay(Ray ray, boolean debug) {
//
//        return traceRay(ray, 0);
//    }

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

        if(currentRecursion >= mMaxRecursions || refl == 0) return color;
        else {
            //calc reflection ray
            Ray reflRay = new Ray(inter.interSectionPoint, Material.getReflectionVector(inter.normal, ray.getDirection().multScalar(-1)));
            //recursively trace reflection ray
            RgbColor reflectionColor = traceRay(reflRay, currentRecursion + 1);

            return color.add(reflectionColor.multScalar(material.reflection));
        }
    }

    private RgbColor shade() {
        return new RgbColor(0, 0, 0);
    }

    private RgbColor traceIllumination() {
        return new RgbColor(0, 0, 0);
    }

    private Vec3 screen2World(float x, float y) {

        return new Vec3(2 * ((x + 0.5f) / 1) - 1, 0f, 0);
    }

    private Vec2 pixelPosNormalized(float x, float y) {
        return new Vec2(2 * ((x + 0.5f) / mBufferedImage.getWidth()) - 1,
                2 * ((y + 0.5f) / mBufferedImage.getHeight()) - 1);
    }
}

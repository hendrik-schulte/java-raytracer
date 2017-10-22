package raytracer;

import material.Material;
import scene.Scene;
import scene.camera.Camera;
import ui.Window;
import utils.Callback;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;
import utils.AntiAliasing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Raytracer {

    private RgbColor BackgroundColor = RgbColor.BLACK;
    private BufferedImage mBufferedImage;
    private Scene scene;
    private Window mRenderWindow;

    private Camera cam;

    private Settings settings;

    private int threadsFinished;
    private Callback renderCallback;

    private List<RenderBlock> renderBlocks = new ArrayList<>();
    private final int renderBlockSize = 10;

    /**
     * Initializes the Raytracer object.
     * @param scene
     * @param renderWindow
     * @param settings
     * @param callback
     */
    public Raytracer(Scene scene, Window renderWindow, Settings settings, Callback callback) {
        Log.print(this, "Init");

        renderCallback = callback;
        mBufferedImage = renderWindow.getBufferedImage();

        this.scene = scene;
        this.settings = settings;
        cam = scene.camera;

        mRenderWindow = renderWindow;
    }

    /**
     * Starts the raytracing action.
     */
    public void renderScene() {

        Log.print(this, "Preliminary calculation");

        settings.ANTIALIASING.Init();
        generateRenderBlocks();

        Log.print(this, "Start rendering");

        startRenderThreads();
    }

    private void renderPixel(int x, int y) {
//        Log.print(this, "calc Pixel (" + x + "," + y + ")");
        renderBlock(x, x + 1, y, y + 1);
    }

    /**
     * Devides the rendering image into small blocks according to renderBlockSize value and fills the renderBlocks
     * collection with it.
     */
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

        List<RenderBlock> jobs = Collections.synchronizedList(new ArrayList<>(renderBlocks));

        for (int i = 0; i < settings.MULTI_THREADING; i++) {

            Thread renderThread = new RenderThread(this, jobs, this::threadFinished);

            renderThread.start();
        }
    }

    /**
     * This is called whenever a render thread finished a render job and has no other job to work on.
     */
    private synchronized void threadFinished() {
        threadsFinished++;

        if (threadsFinished < settings.MULTI_THREADING) return;

        if (settings.ANTIALIASING.isInPreRenderingStage()) {

            Log.print(this, "PreRendering finished.");
            settings.ANTIALIASING.FinishPreRendering();
//                generateRenderBlocks();
            startRenderThreads();
            return;
        }


        Log.print(this, "Rendering completed!");

        renderCallback.callback();
    }

    /**
     * Renders the given RenderBlock.
     *
     * @param rect
     */
    public void renderBlock(RenderBlock rect) {
        renderBlock(rect.startX, rect.endX, rect.startY, rect.endY);
    }

    /**
     * Renders the given block of pixels by start and end coordinates.
     *
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     */
    public void renderBlock(int startX, int endX, int startY, int endY) {
        // Rows
        for (int y = startY; y < endY; y++) {
            // Columns
            for (int x = startX; x < endX; x++) {

                Vec2 normPos = cam.pixelPosNormalized(x, y);

                if (!settings.ANTIALIASING.isEnabled()) {

                    SingleRayCast(normPos, x, y);

                } else {

                    MultiSampleRayCast(normPos, x, y);
                }
            }
//                if(y % 10 == 0 && x % 10 == 0) Log.print(this, "pixel: " + x + "," + y + "\nnormPos:  " + normPos + "\nworldPos: " + worldPos);
        }
    }

    private RgbColor SingleRayCast(Vec2 normPos, int x, int y) {
        Ray ray = cam.calcPixelRay(normPos);

        RgbColor color = traceRay(ray, 0);

        mRenderWindow.setPixel(mBufferedImage, color, new Vec2(x, y));

        return color;
    }

    private void MultiSampleRayCast(Vec2 normPos, int x, int y) {

        switch (settings.ANTIALIASING.getRenderStage()) {
            case PreRendering:
                settings.ANTIALIASING.savePreRendering(x, y, SingleRayCast(normPos, x, y));
                break;

            case Adaptive:

                if (!settings.ANTIALIASING.aaIsNecessary(x, y)) return;

//                Log.print(this, "multisample pixel");

            case disabled:

                ArrayList<Ray> rays = new ArrayList<>();

                for (Vec2 pos : settings.ANTIALIASING.getAntiAliasingPositions()) {

                    rays.add(cam.calcPixelRay(normPos.add(pos)));
                }

                mRenderWindow.setPixel(mBufferedImage, traceRays(rays, 0), new Vec2(x, y));
                break;
        }
    }

    /**
     * Traces the given list of rays and returns the mean of their corresponding colors.
     *
     * @param rays
     * @param currentRecursion
     * @return
     */
    private RgbColor traceRays(List<Ray> rays, int currentRecursion) {

        Vec3 colorVec = new Vec3(0, 0, 0);

        for (Ray ray : rays) {
            RgbColor color = traceRay(ray, currentRecursion);

            colorVec = colorVec.add(new Vec3(color.red(), color.green(), color.blue()));
        }

        if (!rays.isEmpty()) colorVec = colorVec.divideScalar(rays.size());

        return new RgbColor(colorVec.x, colorVec.y, colorVec.z);
    }

    /**
     * Traces the given ray through the scene and returns the corresponding color.
     *
     * @param ray
     * @param currentRecursion
     * @return
     */
    private RgbColor traceRay(Ray ray, int currentRecursion) {

        Intersection intersection = ray.getIntersection(scene.root);

        if (intersection == null) return BackgroundColor;

        RgbColor color = RgbColor.BLACK;
        Material material = intersection.shape.material;
        Vec3 viewVector = ray.getDirection().negate().normalize();

        color = color.add(traceIllumination(intersection, viewVector, material));

        if (currentRecursion >= settings.RECURSIONS)
            return color;

        if (material.opacity < 1)
            color = color.add(traceRefraction(intersection, viewVector, material, currentRecursion));

        if (material.reflection > 0)
            color = color.add(traceReflection(intersection, viewVector, material, currentRecursion));

        return color;
    }

    /**
     * Calculates the local illumination model at the given intersection point.
     *
     * @param inter
     * @param viewVector
     * @param material
     * @return
     */
    private RgbColor traceIllumination(Intersection inter, Vec3 viewVector, Material material) {

        return material.getColor(inter.interSectionPoint, inter.normal, viewVector, scene);
    }

    /**
     * Traces a refraction ray through this intersection point and returns its resulting color.
     *
     * @param inter
     * @param viewVector
     * @param material
     * @param currentRecursion
     * @return
     */
    private RgbColor traceRefraction(Intersection inter, Vec3 viewVector, Material material, int currentRecursion) {
        //calc refraction vector
        Vec3 refractionVector = material.getRefractionVector(inter.normal, viewVector);
//        Vec3 refractionVector = material.getRefractionVectorDeGreve(inter.normal, viewVector);

        //calc ideal refraction ray
        Ray refractionRay = new Ray(inter.interSectionPoint, refractionVector);

        //get distributed rays (in case of diffuse refraction)
        List<Ray> rays = material.getDistributedRays(refractionRay, settings.RAY_DISTRIBUTION_SAMPLES);

        //recursively trace refraction rays
        RgbColor refractionColor = traceRays(rays, currentRecursion + 1);

        return refractionColor.multScalar(material.transparency);
    }

    /**
     * Traces a reflection ray from this intersection point and returns its resulting color.
     *
     * @param inter
     * @param viewVector
     * @param material
     * @param currentRecursion
     * @return
     */
    private RgbColor traceReflection(Intersection inter, Vec3 viewVector, Material material, int currentRecursion) {
        //calc reflection ray
        Ray reflectionRay = new Ray(inter.interSectionPoint, Material.getReflectionVector(inter.normal, viewVector));

        //get distributed rays (in case of diffuse reflection)
        List<Ray> rays = material.getDistributedRays(reflectionRay, settings.RAY_DISTRIBUTION_SAMPLES);
        //recursively trace reflection rays
        RgbColor reflectionColor = traceRays(rays, currentRecursion + 1);

        return reflectionColor.multScalar(material.reflection);
    }
}

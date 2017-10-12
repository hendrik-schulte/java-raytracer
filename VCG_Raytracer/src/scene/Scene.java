package scene;

import material.Lambert;
import material.Material;
import scene.camera.Camera;
import scene.light.AreaLight;
import scene.light.Light;
import scene.shape.Shape;
import scene.shape.Sphere;
import utils.RgbColor;
import utils.io.Log;

import java.util.ArrayList;

public class Scene {

    public ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<Light> pointLights = new ArrayList<>();
    private ArrayList<AreaLight> areaLights = new ArrayList<>();
    public Camera camera;
    public final float ambientIntensity;

    public Scene(float ambientIntensity) {

//        Log.print(this, "Init");
        this.ambientIntensity = ambientIntensity;
    }

    public void createShape(Shape shape) {
        if (shape != null) shapes.add(shape);
    }

    public void createLight(AreaLight light, boolean drawShape) {
        areaLights.add(light);

        if (drawShape) createShape(light.getShape());

//        for (Light shape : getLights()) {
//            createShape(new Sphere(shape.getPosition(), 0.1f, Lambert.DEBUG_GREEN));
//        }
    }

    public void createLight(Light light) {
        pointLights.add(light);
    }

    public void setCamera(Camera cam) {
        camera = cam;
    }

    /**
     * Returns all point lights and all sampled area lights.
     *
     * @return
     */
    public ArrayList<Light> getLights() {
        ArrayList<Light> lights = new ArrayList<Light>(pointLights);

        for (AreaLight areaLight : areaLights) {
            lights.addAll(areaLight.getLights());
        }

        return lights;
    }
}

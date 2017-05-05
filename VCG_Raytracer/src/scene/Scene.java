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

import java.util.*;

public class Scene {

    public ArrayList<Shape> shapeList = new ArrayList<>();
    private ArrayList<Light> pointLights = new ArrayList<>();
    private ArrayList<AreaLight> areaLights = new ArrayList<>();
    public Camera camera;
    public float AmbientIntensity;

    public Scene(float AmbientIntensity) {

        Log.print(this, "Init");
        this.AmbientIntensity = AmbientIntensity;
    }

    public void createShape(Shape shape) {
        shapeList.add(shape);
    }

    public void createLight(AreaLight light, boolean drawShape) {
        areaLights.add(light);

        if (drawShape) createShape(light.getShape());

//        Material mat = new Lambert(
//                RgbColor.BLACK,
//                RgbColor.BLACK,
//                RgbColor.GREEN,
//                0.0f,
//                1.0f,
//                1,
//                1f);
//
//        for (Light shape : getLights()
//                ) {
//            createShape(new Sphere(shape.getPosition(), 0.1f, mat));
//        }
    }

    public void createLight(Light light) {
        pointLights.add(light);
    }

    public void setCamera(Camera cam) {
        camera = cam;
    }

    public ArrayList<Light> getLights() {
        ArrayList<Light> lights = new ArrayList<Light>(pointLights);

        for (AreaLight areaLight : areaLights) {
            lights.addAll(areaLight.getLights());
        }

        return lights;
    }
}

package scene;

import scene.camera.Camera;
import scene.light.AreaLight;
import scene.light.Light;
import scene.shape.Shape;
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

    public void createLight(AreaLight light, boolean drawRect) {
        areaLights.add(light);

        if(drawRect) createShape(light.getRectangle());
    }

    public void createLight(Light light) {
        pointLights.add(light);
    }

    public void createCamera(Camera cam) {
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

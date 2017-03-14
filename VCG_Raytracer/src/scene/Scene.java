package scene;

import scene.camera.Camera;
import scene.light.Light;
import scene.shape.Shape;
import scene.shape.Sphere;
import utils.io.Log;

import java.util.*;

public class Scene {

    public ArrayList<Shape> shapeList = new ArrayList<>();
    public ArrayList<Light> lightList  = new ArrayList<>();
    public Camera camera;
    public float AmbientIntensity = 0.05f;

    public Scene(){
        Log.print(this, "Init");
    }

    public void createShape(Shape shape){
        shapeList.add(shape);
    }

    public void createLight(Light light){
        lightList.add(light);
    }

    public void createCamera(Camera cam){
        camera = cam;
    }
}

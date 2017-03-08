package scene;

import scene.camera.Camera;
import scene.light.Light;
import scene.shape.Shape;
import scene.shape.Sphere;
import utils.io.Log;

public class Scene {

    public Shape[] shapeList;
    public Light[] lightList;
    public Camera camera;

    public Scene(){
        Log.print(this, "Init");
    }

    public void createSphere(Sphere sphere){

    }

    public void createPlane(){

    }

    public void createPointLight(){

    }

    public void createCamera(Camera cam){
        camera = cam;
    }
}

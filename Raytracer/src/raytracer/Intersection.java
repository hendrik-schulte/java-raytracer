package raytracer;

import scene.SceneObject;
import utils.algebra.Vec3;

public class Intersection {

    public Vec3 interSectionPoint;
    public Vec3 normal;
    public  SceneObject shape;
    public double distancePWD;

    private double distance;

    public Intersection(Vec3 intersectionPoint, Vec3 normal, SceneObject shape, double distancePWD){
        this.interSectionPoint = intersectionPoint;
        this.normal = normal;
        this.shape = shape;
        this.distance = -1;
        this.distancePWD = distancePWD;
    }

    public String toString() {
        return "point: " + interSectionPoint + " normal: " + normal + " shape: " + shape + " distance: " + getDistance()+ " distancePWD: " + distancePWD;
    }

    public double getDistance(){
        if(distance < 0){
            distance = Math.sqrt(distancePWD);
        }

        return distance;
    }
}

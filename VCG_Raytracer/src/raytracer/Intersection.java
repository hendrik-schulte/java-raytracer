package raytracer;

import scene.shape.Shape;
import utils.algebra.Vec3;

public class Intersection {

    public Vec3 interSectionPoint;
    public Vec3 normal;
    public Shape shape;
    public double distance;
    public boolean incoming;

    public Intersection(Vec3 intersectionPoint, Vec3 normal, Shape shape, double distance, boolean incoming){
        this.interSectionPoint = intersectionPoint;
        this.normal = normal;
        this.shape = shape;
        this.distance = distance;
        this.incoming = incoming;
    }

//    public Ray calculateReflectionRay(){
//        return new Ray(new Vec3(), new Vec3());
//    }
//
//    public Ray calculateRefractionRay(){
//        return new Ray(new Vec3(), new Vec3());
//    }

    public String toString() {
        return "point: " + interSectionPoint + " normal: " + normal + " shape: " + shape + " distance: " + distance;
    }
}

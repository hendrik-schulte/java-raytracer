package raytracer;

import scene.shape.Shape;
import utils.algebra.Vec3;

public class Intersection {

    public Vec3 interSectionPoint;
    public Vec3 normal;
    public Shape shape;
    public double distance;
    public boolean incoming;
    public boolean hit;

    public Intersection(Shape shape){
        this.interSectionPoint = null;
        this.normal = new Vec3();
        this.shape = shape;
        this.distance = Double.MAX_VALUE;
        this.incoming = false;
        this.hit = false;
    }

    public Intersection(Vec3 intersectionPoint, Vec3 normal, Shape shape, double distance, boolean incoming, boolean hit){
        this.interSectionPoint = intersectionPoint;
        this.normal = normal;
        this.shape = shape;
        this.distance = distance;
        this.incoming = incoming;
        this.hit = hit;
    }

    public Ray calculateReflectionRay(){
        return new Ray(new Vec3(), new Vec3());
    }

    public Ray calculateRefractionRay(){
        return new Ray(new Vec3(), new Vec3());
    }

    public boolean isOutOfDistance(){
        return false;
    }

    @Override
    public String toString() {
        return "point: " + interSectionPoint + " normal: " + normal + " shape: " + shape + " distance: " + distance + " incoming: " + incoming;
    }
}

package raytracer;

import scene.shape.Shape;
import utils.algebra.Vec3;

import java.util.ArrayList;
import java.util.Collection;

public class Ray {

    private Vec3 startPoint;
    private Vec3 direction;
    public Vec3 endPoint;

    public Ray(Vec3 origin, Vec3 direction) {
        startPoint = origin;
        this.direction = direction.normalize();
//        if(this.direction.length() < 0.1f){
//            Log.print(this, "dir is zero");
//        }
    }

    public Vec3 getStartPoint() {
        return startPoint;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public Vec3 calcPoint(float distance) {
        return startPoint.add(direction.multScalar(distance));
    }

    public Vec3 calcPoint(double distance) {
        return startPoint.add(direction.multScalar((float) distance));
    }

    public Intersection getIntersection(Collection<Shape> shapeList) {

        return getIntersection(shapeList, null);
    }

    public Intersection getIntersection(Collection<Shape> shapeList, float maxDistance) {

        return getIntersection(shapeList, new ArrayList<>(), maxDistance);
    }

    public Intersection getIntersection(Collection<Shape> shapeList, Shape ignore) {

        return getIntersection(shapeList, ignore, Float.MAX_VALUE);
    }

    public Intersection getIntersection(Collection<Shape> shapeList, Shape ignore, float maxDistance) {

        ArrayList<Shape> ignoreList = new ArrayList<>();

        if (ignore != null) ignoreList.add(ignore);

        return getIntersection(shapeList, ignoreList, maxDistance);
    }

    public Intersection getIntersection(Collection<Shape> shapeList, Collection<Shape> ignore, float maxDistance) {

        ArrayList<Intersection> intersections = new ArrayList<>();

        for (Shape shape : shapeList) {

            if (ignore != null) if (ignore.contains(shape)) continue;

            Intersection[] tempIntersections = shape.intersect(this);

            if (tempIntersections == null) continue;

            for (Intersection intersection : tempIntersections) {

                if (intersection.distance > maxDistance) continue;
//                if (!intersection.incoming) continue;

//            Log.print(this, "intersec: " + intersection.shape + " dis: " + intersection.distance);

                intersections.add(intersection);
            }
        }

        Intersection closest = popClosest(intersections);
        Intersection secClosest = popClosest(intersections);

//        Log.print(this, "closest: " + closest);
//        Log.print(this, "sec closest: " + secClosest);


        if (closest == null) {
//            Log.print(this, "closest null");
            return null;
        }
        if (closest.distance < 0.001f) {

//            Log.print(this, "return sec");

            return secClosest;
        }

//        Log.print(this, "return first");

        return closest;
    }

    /**
     * Returns the closest intersection and removes it from the list.
     *
     * @param intersections
     * @return
     */
    private Intersection popClosest(ArrayList<Intersection> intersections) {
        Intersection closest = null;
        float currentDistance = Float.MAX_VALUE;

        for (Intersection i : intersections) {
            if (i.distance <= currentDistance) {
                closest = i;
                currentDistance = (float) i.distance;
            }
        }

        if (closest != null) intersections.remove(closest);

        return closest;
    }

    @Override
    public String toString() {
        return "origin: " + startPoint + " dir: " + direction;
    }
}

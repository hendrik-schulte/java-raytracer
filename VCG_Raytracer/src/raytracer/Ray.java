package raytracer;

import scene.shape.Shape;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Ray {

    private Vec3 startPoint;
    //    Vec3 endPoint;
    private Vec3 direction;
//    public float refractionIndex = 1;

    public Ray(Vec3 origin, Vec3 direction) {
        startPoint = origin;
        this.direction = direction.normalize();
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

    public Intersection getIntersection(Collection<Shape> shapeList) {

        return getIntersection(shapeList, null);
    }

    public Intersection getIntersection(Collection<Shape> shapeList, float maxDistance) {

        return getIntersection(shapeList, new ArrayList<>()/*, 0*/, maxDistance);
    }

//    public Intersection getIntersection(Collection<Shape> shapeList/*, float minDistance*/, float maxDistance) {
//
//        return getIntersection(shapeList, new ArrayList<>()/*, minDistance*/, maxDistance);
//    }

    public Intersection getIntersection(Collection<Shape> shapeList, Shape ignore) {

        return getIntersection(shapeList, ignore, Float.MAX_VALUE);
    }

    public Intersection getIntersection(Collection<Shape> shapeList, Shape ignore, float maxDistance) {

        ArrayList<Shape> ignoreList = new ArrayList<>();

        if (ignore != null) ignoreList.add(ignore);

        return getIntersection(shapeList, ignoreList,/* 0,*/ maxDistance);
    }

    public Intersection getIntersection(Collection<Shape> shapeList, Collection<Shape> ignore/*, float minDistance*/, float maxDistance) {

        ArrayList<Intersection> intersections = new ArrayList<>();

        for (Shape shape : shapeList) {

            if (ignore != null) if (ignore.contains(shape)) continue;

            Intersection intersection = shape.intersect(this);

            if (intersection == null) continue;
            if (intersection.distance > maxDistance/* || intersection.distance < minDistance */) continue;
            if (!intersection.incoming) continue;

            intersections.add(intersection);
        }

//        Collections.sort(intersections, (i1, i2) -> (int) (i1.distance - i2.distance));
//
//        if (intersections.isEmpty()) return null;
//
//        if(intersections.get(0).distance <= 0.01) intersections.remove(0);
//
//        return intersections.isEmpty() ? null : intersections.get(0);


//        Collections.sort(intersections, new Comparator<Intersection>() {
//            @Override
//            public int compare(Intersection i1, Intersection i2) {
//                return i1.isCloser(i2);
//            }
//        });


//        Intersection closest = null;
//        Intersection secClosest = null;
//        float currentDistance = Float.MAX_VALUE;
//
//        for (Intersection i : intersections) {
//            if (i.distance <= currentDistance) {
//                if (closest != null) secClosest = closest;
//                closest = i;
//                currentDistance = (float) i.distance;
//            }
//        }

        Intersection closest = popClosest(intersections);
        Intersection secClosest = popClosest(intersections);


        if (closest == null) {
//            Log.print(this, "closest null");
            return null;
        }
//        Log.print(this, "sorting");

//        Log.print(this, "dis: " + closest.distance);

        if (closest.distance < 0.01f) {

//            Log.print(this, "return sec");

            return secClosest;
        }

//        Log.print(this, "return first");


        return closest;
    }

    /**
     * Returns the closest intersection and removes it from the list.
     * @param intersections
     * @return
     */
    private Intersection popClosest(ArrayList<Intersection> intersections){
        Intersection closest = null;
        float currentDistance = Float.MAX_VALUE;

        for (Intersection i : intersections) {
            if (i.distance <= currentDistance) {
                closest = i;
                currentDistance = (float) i.distance;
            }
        }

        if(closest != null) intersections.remove(closest);

        return closest;
    }
}

package raytracer;

import scene.shape.Shape;
import utils.algebra.Vec3;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Ray {

    private Vec3 startPoint;
    //    Vec3 endPoint;
    private Vec3 direction;
//    float distance;

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

        if(ignore != null) ignoreList.add(ignore);

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

        if (intersections.isEmpty()) return null;

        Collections.sort(intersections, (i1, i2) -> (int) (i1.distance - i2.distance));

        return intersections.get(0);
    }
}

package raytracer;

import scene.SceneObject;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Ray {

    private static final float roundTolerance = 0.00001f;

    private Vec3 startPoint;
    private Vec3 direction;

    /**
     * If this ray has been transformed to a local coordinat system, this is a reference to the original ray in world space.
     */
    private Ray worldSpaceRay;

    public Ray(Vec3 origin, Vec3 direction) {
        startPoint = origin;
        this.direction = direction.normalize();
//        if(this.direction.length() < 0.1f){
//            Log.print(this, "dir is zero");
//        }
    }

    public Ray(Vec3 origin, Vec3 direction, Ray worldSpaceRay){
        this(origin, direction);

        this.worldSpaceRay = worldSpaceRay;
    }

    /**
     * Returns this ray in world space or itself if it was created in world space.
     * @return
     */
    public Ray getWorldSpaceRay(){
        if(worldSpaceRay == null) return this;

        return worldSpaceRay;
    }

    public Vec3 getStartPoint() {
        return startPoint;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public Vec3 calcPoint(float distance) {
        return startPoint.add(direction.scale(distance));
    }

    public Vec3 calcPoint(double distance) {
        return startPoint.add(direction.scale((float) distance));
    }

    public Intersection getIntersection(SceneObject root) {

        return getIntersection(root, -1);
    }

    /**
     * Returns true if there is an intersection between the origin of the ray until the given squared distance.
     * @param root
     * @param maxDistanceSquared
     * @return
     */
    public boolean shadowCheck(SceneObject root, float maxDistanceSquared) {

        return getIntersection(root, maxDistanceSquared) != null;
    }

    public Intersection getIntersection(SceneObject root, float maxDistanceSquared) {

        ArrayList<Intersection> intersections = root.intersectAll(this);

//        Log.print(this, "inter raw " + intersections.size());
//        int raw = intersections.size();

        if (intersections.isEmpty()) return null;

        if(maxDistanceSquared > -1) removeAboveDistance(intersections, maxDistanceSquared);


//        Log.print(this, "inter raw: + " + raw + " after " + intersections.size());

        Intersection closest = popClosest(intersections);
        Intersection secClosest = popClosest(intersections);


        if (closest == null) {
            return null;
        }

        if (closest.distancePWD < roundTolerance) {

            return secClosest;
        }

        return closest;
    }

    /**
     * Removes all intersections that have a squared distance greater than the given value.
     * @param intersections
     * @param maxDistanceSquared
     */
    private void removeAboveDistance(ArrayList<Intersection> intersections, float maxDistanceSquared){
        for (int i = intersections.size() - 1; i > -1; i--) {

            if (intersections.get(i).distancePWD > maxDistanceSquared) intersections.remove(i);
        }
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
            if (i.distancePWD <= currentDistance) {
                closest = i;
                currentDistance = (float) i.distancePWD;
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

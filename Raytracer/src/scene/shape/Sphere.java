package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;

public class Sphere extends SceneObject {

//    private Matrix4x4 transformation;
//    private Matrix4x4 inverseTransformation;
//
//    private float radius;

    public Sphere(Matrix4x4 transformation, Material material) {
        super("Sphere", transformation, material);

//        setTransformation(transformation);

        //calc approximate radius
//        Vec3 setScale = transformation.getUniformScale();
//        radius = setScale.x * setScale.y * setScale.z;
    }

    public Sphere(Vec3 pos, float radius, Material material) {
        super("Sphere", new Matrix4x4(pos, radius), material);

//        Log.print(this, "sphere world transform " + getWorldTransform());
//        setTransformation(new Matrix4x4(pos, radius));

//        this.radius = radius;
//        radiusSquared = (float) Math.pow(radius, 2);
    }

    @Override
    protected ArrayList<Intersection> intersectThis(Ray localRay) {

        //transform ray to spheres local coordinate system
        Vec3 pos = localRay.getStartPoint();
        Vec3 dir = localRay.getDirection();

        float B = 2 * (pos.x * dir.x + pos.y * dir.y + pos.z * dir.z);
        float C = (float) (Math.pow(pos.x, 2) + Math.pow(pos.y, 2) + Math.pow(pos.z, 2) - 1);

        double discriminant = Math.pow(B, 2) - 4 * C;

        if (discriminant < 0) {
            //no intersection
            return new ArrayList<>();
        }
        if (discriminant == 0) {
            //ray touches sphere
            double t = -B / 2f;

            if (t < 0) return new ArrayList<>();

            return toList(calcWorldSpaceIntersection(localRay, t));
        }
        if (discriminant > 0) {
            //two intersections

            double root = Math.sqrt(discriminant);

            double t0 = (-B - root) / 2f;
            double t1 = (-B + root) / 2f;

            if (t0 < 0 && t1 < 0) return new ArrayList<>();
            if (t0 > 0 && t1 <= 0) return toList(calcWorldSpaceIntersection(localRay, t0));
            if (t0 <= 0 && t1 > 0) return toList(calcWorldSpaceIntersection(localRay, t1));

            return toList(calcWorldSpaceIntersection(localRay, t0), calcWorldSpaceIntersection(localRay, t1));
        }

        return new ArrayList<>();
    }

    /**
     * Calculates the normal of the sphere given an point on the surface.
     *
     * @param pointOnSphere
     * @return
     */
    private Vec3 calcNormal(Vec3 pointOnSphere) {
//        Log.print(this, "calc sphere normal. length: " + pointOnSphere.sub(mWorldPosition).length() + " radius: " + radius);
        return pointOnSphere.sub(getWorldPosition()).normalize();
    }

    private Intersection calcWorldSpaceIntersection(Ray localRay, double t) {

        Vec3 intersectionPoint = getWorldTransform().multPoint(localRay.calcPoint(t));
        Vec3 normal = getWorldTransform().multVec(calcNormal(intersectionPoint)).normalize();
        double distancePWD = localRay.getWorldSpaceRay().getStartPoint().distanceSquared(intersectionPoint);

//        Vec3 localNormal = calcNormal(intersectionPoint);
//        Log.print(this, "normal: " + localNormal + "len: " + localNormal.length() +
//                " world space normal: " + normal + "len: " + normal.length());

        return new Intersection(intersectionPoint, normal, this, distancePWD);
    }
}

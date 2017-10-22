package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Sphere extends SceneObject {

    private Matrix4x4 transformation;
    private Matrix4x4 inverseTransformation;

    private float radius;

    public Sphere(Matrix4x4 transformation, Material material) {
        super(transformation.getTranslation(), material);

        setTransformation(transformation);

        //calc approximate radius
        Vec3 scale = transformation.getUniformScale();
        radius = scale.x * scale.y * scale.z;
    }

    public Sphere(Vec3 pos, float radius, Material material) {
        super(pos, material);

        setTransformation(new Matrix4x4(pos, radius));

        this.radius = radius;
//        radiusSquared = (float) Math.pow(radius, 2);
    }

    private void setTransformation(Matrix4x4 transformation){
        this.transformation = transformation;
        this.inverseTransformation = transformation.invert();
    }

    @Override
    protected ArrayList<Intersection> intersectThis(Ray ray) {

        //transform ray to spheres local coordinate system
        Ray transRay = inverseTransformation.multRay(ray);

        Vec3 pos = transRay.getStartPoint();
        Vec3 dir = transRay.getDirection();

        float B = 2 * (pos.x * dir.x + pos.y * dir.y + pos.z * dir.z);
        float C = (float) (Math.pow(pos.x, 2) + Math.pow(pos.y, 2) + Math.pow(pos.z, 2) - 1);

        double discriminant = Math.pow(B, 2) - 4 * C;

        if (discriminant < 0) {
            //no intersection
            return null;
        }
        if (discriminant == 0) {
            //ray touches sphere
            double t = -B / 2f;

            if (t < 0) return null;

            return toList(getIntersection(ray, transRay, t));
        }
        if (discriminant > 0) {
            //two intersections

            double root = Math.sqrt(discriminant);

            double t0 = (-B - root) / 2f;
            double t1 = (-B + root) / 2f;

            if (t0 < 0 && t1 < 0) return null;
            if (t0 > 0 && t1 <= 0) return toList(getIntersection(ray, transRay, t0));
            if (t0 <= 0 && t1 > 0) return toList(getIntersection(ray, transRay, t1));

            return toList(getIntersection(ray, transRay, t0), getIntersection(ray, transRay, t1));
        }

        return null;
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

    private Intersection getIntersection(Ray ray, Ray transRay, double t) {

        Vec3 intersectionPoint = transformation.multVec3(transRay.calcPoint(t), true);
        Vec3 normal = transformation.multVec3(calcNormal(intersectionPoint), false);
        double distancePWD = ray.getStartPoint().distanceSquared(intersectionPoint);

        Intersection inter = new Intersection(intersectionPoint, normal, this, distancePWD);

        return inter;
    }

    public float getRadius() {
        return radius;
    }
}

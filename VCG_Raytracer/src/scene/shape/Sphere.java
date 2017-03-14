package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Sphere extends Shape {

    private float radius;

    public Sphere(Vec3 pos, float radius, Material material) {
        super(pos, material);

        this.radius = radius;
    }

    @Override
    public Intersection intersect(Ray ray) {

        Vec3 pos = ray.getStartPoint().sub(mPosition);
//        Vec3 pos = ray.getStartPoint();
        Vec3 dir = ray.getDirection();

        float B = 2 * (pos.x * dir.x + pos.y * dir.y + pos.z * dir.z);
        float C = (float) (Math.pow(pos.x, 2) + Math.pow(pos.y, 2) + Math.pow(pos.z, 2) - Math.pow(radius, 2)); //TODO: pow einmal berechnen

        float discriminant = (float) Math.pow(B, 2) - 4 * C;

        if (discriminant < 0) {
            //no intersection
            return null;
        }
        if (discriminant == 0) {
            //ray touches sphere

            float t = -B / 2f;

            if (t < 0) return null;

            return getIntersection(ray, t);
        }
        if (discriminant > 0) {
            //two intersections

            float t0 = (-B - (float) Math.sqrt(discriminant)) / 2f;
            float t1 = (-B + (float) Math.sqrt(discriminant)) / 2f;

            if (t0 < 0 && t1 < 0) return null;
            if (t0 > 0 && t1 <= 0) return getIntersection(ray, t0);
            if (t0 <= 0 && t1 > 0) return getIntersection(ray, t1);

            return getIntersection(ray, Math.min(t0, t1));
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
        return pointOnSphere.sub(mPosition).normalize();
    }

    private Intersection getIntersection(Ray ray, float t) {
        Vec3 intersectionPoint = ray.calcPoint(t);

        return new Intersection(intersectionPoint, calcNormal(intersectionPoint), this, Math.abs(t), t > 0, true);
    }
}

package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;

public class Sphere extends Shape {

    private float radius;
    private float radiusSquared;

    public Sphere(Vec3 pos, float radius, Material material) {
        super(pos, material);

        this.radius = radius;
        radiusSquared = (float) Math.pow(radius, 2);
    }

    @Override
    public Intersection intersect(Ray ray) {

        Vec3 pos = ray.getStartPoint().sub(mPosition);
        Vec3 dir = ray.getDirection();

        float B = 2 * (pos.x * dir.x + pos.y * dir.y + pos.z * dir.z);
        float C = (float) (Math.pow(pos.x, 2) + Math.pow(pos.y, 2) + Math.pow(pos.z, 2) - radiusSquared);

        double discriminant = Math.pow(B, 2) - 4 * C;

        if (discriminant < 0) {
            //no intersection
            return null;
        }
        if (discriminant == 0) {
            //ray touches sphere

            double t = -B / 2f;

            if (t < 0) return null;

            return getIntersection(ray, t);
        }
        if (discriminant > 0) {
            //two intersections

            double t0 = (-B - Math.sqrt(discriminant)) / 2f;
            double t1 = (-B + Math.sqrt(discriminant)) / 2f;

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

    private Intersection getIntersection(Ray ray, double t) {
        Vec3 intersectionPoint = ray.calcPoint((float) t);

        return new Intersection(intersectionPoint, calcNormal(intersectionPoint), this, Math.abs(t), t > 0, true);
    }
}

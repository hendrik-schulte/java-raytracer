package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;

public class Plane extends Shape {

    private Vec3 normal;

    public Plane(Vec3 pos, Vec3 normal, Material material) {
        super(pos, material);

        this.normal = normal.normalize();
    }

    @Override
    public Intersection intersect(Ray ray) {

        Vec3 pos = ray.getStartPoint();
        Vec3 D = ray.getDirection();

        float denominator = normal.scalar(D);

        if (denominator == 0) {
            //no intersection
            return null;
        }

        float Q = mPosition.length();

        float t = (normal.scalar(pos) + Q) / denominator;

        return new Intersection(ray.calcPoint(t), normal, this, Math.abs(t), t > 0, true);
    }
}

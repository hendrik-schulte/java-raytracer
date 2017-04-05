package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;

public class Plane extends Shape {

    protected Vec3 normal;

    public Plane(Vec3 pos, Vec3 normal, Material material) {
        super(pos, material);

        this.normal = normal.normalize();
    }

    @Override
    public Intersection[] intersect(Ray ray) {

        Vec3 pos = ray.getStartPoint().sub(mPosition);
        Vec3 D = ray.getDirection();

        float denominator = normal.scalar(D);

        if (denominator == 0) return null;   //no intersection

        double t = 0;

        if (denominator < 0) {
            //intersection from front
            t = -(normal.scalar(pos)) / denominator;

        }

        if (denominator > 0) {
            //intersection from behind
            return null;
        }

        return new Intersection[]{new Intersection(ray.calcPoint((float) t), normal, this, Math.abs(t), t > 0)};
    }

    @Override
    public String toString() {
        return "Plane color " + material.ambient;
    }

    public Vec3 getNormal(){
        return new Vec3(normal);
    }
}

package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;
import utils.io.Log;

public class Plane extends Shape {

    private Vec3 normal;
    private double Q;

    public Plane(Vec3 pos, Vec3 normal, Material material) {
        super(pos, material);

        this.normal = normal.normalize();
        Q = mPosition.length();
    }

    @Override
    public Intersection intersect(Ray ray) {

        Vec3 pos = ray.getStartPoint();
        Vec3 D = ray.getDirection();

        float denominator = normal.scalar(D);

        if (denominator == 0) return null;   //no intersection

        double t = 0;

        if (denominator < 0) {
            //intersection from front
            t = -(normal.scalar(pos) + Q) / denominator;

        }

        if (denominator > 0) {
            //intersection from behind
//            t = -(normal.scalar(pos) + Q) / denominator;
            return null;
        }


//        t = -(normal.scalar(pos) + Q) / Math.abs(denominator);
//        t = (mPosition.sub(pos).scalar(normal)) / denominator;

//        Log.print(this, "d: " + denominator +  " t: " + t);

        return new Intersection(ray.calcPoint((float) t), normal, this, Math.abs(t), t > 0);
    }

    @Override
    public String toString() {
        return "Plane color " + material.ambient;
    }
}

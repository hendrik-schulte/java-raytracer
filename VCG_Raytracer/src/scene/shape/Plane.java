package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

public class Plane extends Shape {

    protected enum PlaneProjection {
        YZ,
        XZ,
        XY
    }

    protected Vec3 normal;
    private boolean drawBack;

    public Plane(Vec3 pos, Vec3 normal, boolean drawBack, Material material) {
        super(pos, material);

        this.normal = normal.normalize();
        this.drawBack = drawBack;
    }

    @Override
    public Intersection[] intersect(Ray ray) {

        Vec3 pos = ray.getStartPoint().sub(mPosition);
        Vec3 D = ray.getDirection();

        float denominator = normal.scalar(D);

        if (denominator == 0) return null;   //no intersection

        float t = 0;
        Vec3 tempNormal = normal;

        if (denominator < 0) {
            //intersection from front
            t = -(normal.scalar(pos)) / denominator;
        }

        if (denominator > 0) {
            //intersection from behind
            if (!drawBack) return null;

            tempNormal = normal.negate();
            t = (tempNormal.scalar(pos)) / denominator;
        }

        if (t < 0) return null;

        return new Intersection[]{new Intersection(ray.calcPoint(t), tempNormal, this, Math.abs(t), denominator < 0)};
    }

    @Override
    public String toString() {
        return "Plane color " + material.ambient;
    }

    public Vec3 getNormal() {
        return new Vec3(normal);
    }

    protected static PlaneProjection getPlaneProjection(Vec3 normal) {
        float x = Math.abs(normal.x), y = Math.abs(normal.y), z = Math.abs(normal.z);

        //project plane to axis-planes
        if (x >= y && x >= z) {
            //x is biggest normal component -> project to YZ-plane
            return PlaneProjection.YZ;
        } else if (y >= x && y >= z) {
            //y is biggest normal component -> project to XZ-plane
            return PlaneProjection.XZ;
        } else if (z >= x && z >= y) {
            //z is biggest normal component -> project to XY-plane
            return PlaneProjection.XY;
        }

        Log.error(Plane.class, "Plane projection failed! normal: " + normal);

        return null;
    }

    protected static Vec2 projectToAxisPlane(PlaneProjection projection, Vec3 vector) {
        switch (projection) {
            case YZ:
                return new Vec2(vector.y, vector.z);
            case XZ:
                return new Vec2(vector.x, vector.z);
            case XY:
                return new Vec2(vector.x, vector.y);
        }

        return Vec2.ZERO;
    }
}

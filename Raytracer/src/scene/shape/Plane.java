package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;

public class Plane extends SceneObject {

    protected enum PlaneProjection {
        YZ,
        XZ,
        XY
    }

    protected final Vec3 normal;
    private Vec3 reversedNormal;
    private final boolean drawBack;

    public Plane(Vec3 pos, Vec3 normal, boolean drawBack, Material material) {
        super("Plane" ,pos, material);

        this.normal = normal.normalize();
        this.drawBack = drawBack;

        if(!drawBack) return;

        reversedNormal = normal.negate();
    }

    @Override
    protected ArrayList<Intersection> intersectThis(Ray ray) {

        Vec3 pos = ray.getStartPoint().sub(getWorldPosition());
        Vec3 D = ray.getDirection();

        float denominator = normal.scalar(D);

        if (denominator == 0) return new ArrayList<>();   //no intersection

        float t = 0;
//        Vec3 tempNormal = normal;

        if (denominator < 0) {
            //intersection from front
            t = -(normal.scalar(pos)) / denominator;
        }

        if (denominator > 0) {
            //intersection from behind
            if (!drawBack) return new ArrayList<>();

            t = (reversedNormal.scalar(pos)) / denominator;
        }

        if (t < 0) return new ArrayList<>();

//        Intersection i = new Intersection(ray.calcPoint(t), normal, this, (t * t));
//        Log.print(this, "plane intersec found t: " + t + " squared: " + i.distancePWD);
//        return toList(i);

        return toList(new Intersection(ray.calcPoint(t), normal, this, (t * t)));
    }

    @Override
    public String toString() {
        return super.toString() + "color " + material.ambient;
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

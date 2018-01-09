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

    private Vec3 worldSpaceNormal;

    private final boolean drawBack;

    public Plane(Vec3 pos, Vec3 normal, boolean drawBack, Material material) {
        super("Plane", pos, material);

        this.normal = normal.normalize();
        this.drawBack = drawBack;

        if (!drawBack) return;

        reversedNormal = normal.negate();

        onTransformChange();
    }

    @Override
    protected void onTransformChange() {

        if (normal == null) return;

        worldSpaceNormal = getWorldTransform().multVec(normal).normalize();

        Log.print(this, "normal: " + normal + " world space normal: " + worldSpaceNormal);
    }

    @Override
    protected ArrayList<Intersection> intersectThis(Ray localRay) {

        Vec3 pos = localRay.getStartPoint();

//        Log.print(this, "plane normal: " + normal);
//        Log.print(this, "world ray: " + localRay.getWorldSpaceRay());
//        Log.print(this, "local ray: " + localRay);


        float denominator = normal.scalar(localRay.getDirection());

        if (denominator == 0) return new ArrayList<>();   //no intersection

        float t = 0;

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

        return toList(calcWorldSpaceIntersection(localRay, worldSpaceNormal, t));
    }

    @Override
    public String toString() {
        return super.toString() + " color " + material.ambient;
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

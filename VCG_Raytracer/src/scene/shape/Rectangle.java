package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Rectangle extends Plane {

    private enum PlaneProjection {
        YZ,
        XZ,
        XY
    }

    public Vec3 a;
    public Vec3 b;
    private PlaneProjection projectToPlane;

    private Vec2 corner1;
    private Vec2 corner2;

    public Rectangle(Vec3 pos, Vec3 a, Vec3 b, Material material) {

        super(pos, a.normalize().cross(b.normalize()), material);

        this.a = a;
        this.b = b;

        float x = Math.abs(normal.x), y = Math.abs(normal.y), z = Math.abs(normal.z);

        Vec2 projA = null;
        Vec2 projB = null;

        //project plane to axis-planes
        if (x >= y && x >= z) {
            //x is biggest normal component -> project to YZ-plane
            projectToPlane = PlaneProjection.YZ;
            projA = new Vec2(a.y, a.z);
            projB = new Vec2(b.y, b.z);
        } else if (y >= x && y >= z) {
            //y is biggest normal component -> project to XZ-plane
            projectToPlane = PlaneProjection.XZ;
            projA = new Vec2(a.x, a.z);
            projB = new Vec2(b.x, b.z);
        } else if (z >= x && z >= y) {
            //z is biggest normal component -> project to XY-plane
            projectToPlane = PlaneProjection.XY;
            projA = new Vec2(a.x, a.y);
            projB = new Vec2(b.x, b.y);
        }

        corner1 = new Vec2(projA.x + projB.x, projA.y + projB.y);
        corner2 = new Vec2(-(projA.x + projB.x), -(projA.y + projB.y));
    }

    @Override
    public Intersection intersect(Ray ray) {
        Intersection planeIntersection = super.intersect(ray);

        if (planeIntersection == null) return null;

        Vec3 intersecPoint = planeIntersection.interSectionPoint.sub(mPosition);
        Vec2 projIntersecPoint = null;

        switch (projectToPlane) {
            case YZ:
                projIntersecPoint = new Vec2(intersecPoint.y, intersecPoint.z);
                break;
            case XZ:
                projIntersecPoint = new Vec2(intersecPoint.x, intersecPoint.z);
                break;
            case XY:
                projIntersecPoint = new Vec2(intersecPoint.x, intersecPoint.y);
                break;
        }

        if (withinValues(corner1.x, corner2.x, projIntersecPoint.x) &&
                withinValues(corner1.y, corner2.y, projIntersecPoint.y)) return planeIntersection;


        return null;
    }

    private boolean withinValues(float border1, float border2, float value) {

        if (border1 < border2) {
            return value >= border1 && value <= border2;
        } else {
            return value >= border2 && value <= border1;
        }
    }
}

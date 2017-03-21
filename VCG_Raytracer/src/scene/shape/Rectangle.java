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

//    private Vec3 a;
//    private Vec3 b;
    private PlaneProjection projectToPlane;
    Vec2 projA = null, projB = null;

    public Rectangle(Vec3 pos, Vec3 a, Vec3 b, Material material) {

        super(pos, a.normalize().cross(b.normalize()), material);

//        this.a = a;
//        this.b = b;

        float x = Math.abs(normal.x), y = Math.abs(normal.y), z = Math.abs(normal.z);

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

        if (isWithinProjectedPlane(projIntersecPoint)) return planeIntersection;

        return null;
    }

    private boolean isWithinProjectedPlane(Vec2 pI) {

        Vec2 c1 = new Vec2(projA.x + projB.x, projA.y + projB.y);
        Vec2 c2 = new Vec2(-(projA.x + projB.x), -(projA.y + projB.y));

        return withinValues(c1.x, c2.x, pI.x) && withinValues(c1.y, c2.y, pI.y);
    }

    private boolean withinValues(float border1, float border2, float value) {

        if (border1 < border2) {
            return value >= border1 && value <= border2;
        } else {
            return value >= border2 && value <= border1;
        }
    }
}

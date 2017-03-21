package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

public class Rectangle extends Plane {

    private Vec3 a;
    private Vec3 b;
//    private float by_minus_bx;
//    private float ax_dividedBy_ay;

    public Rectangle(Vec3 pos, Vec3 a, Vec3 b, Material material) {

        super(pos, a.normalize().cross(b.normalize()), material);

        Log.print(this, "normal: " + normal);

        this.a = a;
        this.b = b;

//        by_minus_bx = b.y - b.x;
//        ax_dividedBy_ay = a.x / a.y;
//
//        Log.print(this, "by_minus_bx: " + by_minus_bx);
//        Log.print(this, "ax_dividedby_ay: " + ax_dividedBy_ay);

    }

    @Override
    public Intersection intersect(Ray ray) {
        Intersection planeIntersection = super.intersect(ray);

        if (planeIntersection == null) return null;

        Vec3 intersecPoint = planeIntersection.interSectionPoint.sub(mPosition);
        Vec2 pA = null, pB = null, pI = null;

        //projecting to axis planes
        float x = Math.abs(normal.x), y = Math.abs(normal.y), z = Math.abs(normal.z);
        if (x >= y && x >= z) {
            //x is biggest normal component -> project to ZY-plane
            pA = new Vec2(a.y, a.z);
            pB = new Vec2(b.y, b.z);
            pI = new Vec2(intersecPoint.y, intersecPoint.z);
        } else if (y >= x && y >= z) {
            //y is biggest normal component -> project to XZ-plane
            pA = new Vec2(a.x, a.z);
            pB = new Vec2(b.x, b.z);
            pI = new Vec2(intersecPoint.x, intersecPoint.z);
        } else if (z >= x && z >= y) {
            //z is biggest normal component -> project to XY-plane
            pA = new Vec2(a.x, a.y);
            pB = new Vec2(b.x, b.y);
            pI = new Vec2(intersecPoint.x, intersecPoint.y);
        }

        if (isWithinProjectedPlane(pA, pB, pI)) return planeIntersection;

        return null;
    }

    private boolean isWithinProjectedPlane(Vec2 pA, Vec2 pB, Vec2 pI) {

        Vec2 c1 = new Vec2(pA.x + pB.x, pA.y + pB.y);
        Vec2 c2 = new Vec2(-(pA.x + pB.x), -(pA.y + pB.y));

        return withinValues(c1.x, c2.x, pI.x) && withinValues(c1.y, c2.y, pI.y);
    }

    private boolean withinValues(float border1, float border2, float value) {

        if (border1 < border2) {
            return value >= border1 && value <= border2;
        } else {
            return value >= border2 && value <= border1;
        }
    }

//    public Intersection intersect(Ray ray) {
//        Intersection planeIntersection = super.intersect(ray);
//
//        if (planeIntersection == null) return null;
//
//        Vec3 intersecPoint = planeIntersection.interSectionPoint;
//
////        float tb = ((intersecPoint.y * ax_dividedBy_ay) - intersecPoint.x) / (by_minus_bx);
//        float tb = (((intersecPoint.y - mPosition.y) * ax_dividedBy_ay) - intersecPoint.x + mPosition.x) / (by_minus_bx);
//        float ta = (-tb * b.x + intersecPoint.x - mPosition.x) / a.x;
//
//        //
////        intersecPoint = mPosition.add(a.multScalar(ta).add(b.multScalar(tb)));
//
////        intersecPoint.x = ta * a.x + tb * b.x + mPosition.x;
////        intersecPoint.y = ta * a.y + tb * b.y;
//
//
////        intersecPoint.y = ((-tb * b.x + intersecPoint.x) / a.x) * a.y + tb * b.y;
//
////        Log.print(this, "ta: " + ta);
////        Log.print(this, "tb: " + tb);
//
////        Vec3 checkintersecPoint = mPosition.add(a.multScalar(ta).add(b.multScalar(tb)));
////
////        Log.print(this, "intersecPoint:     " + intersecPoint);
////        Log.print(this, "check with result: " + checkintersecPoint);
//
//        if (Math.abs(ta) >= 1 || Math.abs(tb) >= 1) return null;
//
////        Log.print(this, "rect intersect");
//
//        return planeIntersection;
//    }
}

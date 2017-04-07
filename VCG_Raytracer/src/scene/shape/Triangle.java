package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

public class Triangle extends Plane {

    private Vec2 projA;
    private Vec2 projB;
    private Vec2 projC;
    private Vec2 projAB;
    private Vec2 projAC;

    private PlaneProjection projectToPlane;

    public Triangle(Vec3 a, Vec3 b, Vec3 c, boolean drawBack, Material material) {
        super(a, b.sub(a).cross(c.sub(a)), drawBack, material);

        projectToPlane = getPlaneProjection(normal);

        projA = projectToAxisPlane(projectToPlane, a);
        projB = projectToAxisPlane(projectToPlane, b);
        projC = projectToAxisPlane(projectToPlane, c);
        projAB = projB.sub(projA);
        projAC = projC.sub(projA);
    }

    @Override
    public Intersection[] intersect(Ray ray) {
        Intersection[] planeIntersection = super.intersect(ray);

        if (planeIntersection == null) return null;

        //adapted from http://blackpawn.com/texts/pointinpoly/
        Vec2 projAP = projectToAxisPlane(projectToPlane, planeIntersection[0].interSectionPoint).sub(projA);

        float dot00 = projAC.scalar(projAC);
        float dot01 = projAC.scalar(projAB);
        float dot02 = projAC.scalar(projAP);
        float dot11 = projAB.scalar(projAB);
        float dot12 = projAB.scalar(projAP);

        float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        if ((u >= 0) && (v >= 0) && (u + v < 1)) {
//            Log.print(this, "triangle intersec");
            return planeIntersection;
        }

        return null;
    }
}

package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.MathEx;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Rectangle extends Plane {

    public Vec3 a;
    public Vec3 b;
    private PlaneProjection projectToPlane;

    private Vec2 corner1;
    private Vec2 corner2;

    public Rectangle(Vec3 pos, Vec3 a, Vec3 b, boolean drawBack, Material material) {

        super(pos, a.normalize().cross(b.normalize()), drawBack, material);

        name = "Rectangle";

        this.a = a;
        this.b = b;

        projectToPlane = getPlaneProjection(normal);

        Vec2 projA = projectToAxisPlane(projectToPlane, a);
        Vec2 projB = projectToAxisPlane(projectToPlane, b);

        corner1 = new Vec2(projA.x + projB.x, projA.y + projB.y);
        corner2 = new Vec2(-(projA.x + projB.x), -(projA.y + projB.y));
    }

    @Override
    protected ArrayList<Intersection> intersectThis(Ray ray) {
        ArrayList<Intersection> planeIntersection = super.intersectThis(ray);

        if (planeIntersection.isEmpty()) return planeIntersection;

        Vec3 intersecPoint = planeIntersection.get(0).interSectionPoint.sub(getWorldPosition());

        Vec2 projIntersecPoint = projectToAxisPlane(projectToPlane, intersecPoint);

        if (MathEx.isWithinValues(corner1.x, corner2.x, projIntersecPoint.x) &&
                MathEx.isWithinValues(corner1.y, corner2.y, projIntersecPoint.y)) return planeIntersection;

        return new ArrayList<>();
    }

}

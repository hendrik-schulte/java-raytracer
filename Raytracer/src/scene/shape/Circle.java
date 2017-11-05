package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Circle extends Plane
{
    public final float radius;
    private final float radiusSquared;

    public Circle(Vec3 pos, Vec3 normal, float radius, boolean drawBack, Material material) {
        super(pos, normal, drawBack, material);

        this.name = "Circle";
        this.radius = radius;
        radiusSquared = (float) Math.pow(radius, 2);
    }

    @Override
    public ArrayList<Intersection> intersectThis(Ray ray) {
        ArrayList<Intersection> intersec = super.intersectThis(ray);

        if(intersec.isEmpty()) return intersec;

        Vec3 intersecPoint = intersec.get(0).interSectionPoint.sub(getWorldPosition());

        if(radiusSquared < intersecPoint.distanceSquared(Vec3.ZERO)) return new ArrayList<>();

        return intersec;
    }
}

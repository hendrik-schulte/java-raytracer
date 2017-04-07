package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;

public class Circle extends Plane
{
    public float radius;
    private float radiusSquared;

    public Circle(Vec3 pos, Vec3 normal, float radius, boolean drawBack, Material material) {
        super(pos, normal, drawBack, material);

        this.radius = radius;
        radiusSquared = (float) Math.pow(radius, 2);
    }

    @Override
    public Intersection[] intersect(Ray ray) {
        Intersection[] intersec = super.intersect(ray);

        if(intersec == null) return null;

        Vec3 intersecPoint = intersec[0].interSectionPoint.sub(mPosition);

        if(radiusSquared < intersecPoint.distanceSquared(Vec3.ZERO)) return null;

        return intersec;
    }
}

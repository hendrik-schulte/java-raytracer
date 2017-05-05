package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

import java.util.ArrayList;

public abstract class Shape extends SceneObject {

    public Material material;

    public Shape(Vec3 pos, Material material) {

        super(pos);

        this.material = material;
    }

    public abstract Intersection[] intersect(Ray ray);

    protected static Intersection[] getIntersection(Ray ray, ArrayList<Shape> shapes) {
        ArrayList<Intersection> result = new ArrayList<>();

        for (Shape shape : shapes) {
            Intersection[] rectIntersection = shape.intersect(ray);

            if (rectIntersection == null) continue;
            if (rectIntersection.length == 0) continue;

            ArrayList<Intersection> temp = new ArrayList<>();
            temp.add(rectIntersection[0]);
            result.addAll(temp);
        }

        return result.toArray(new Intersection[result.size()]);
    }
}

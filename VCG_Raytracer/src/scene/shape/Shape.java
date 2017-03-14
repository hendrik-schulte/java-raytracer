package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

public abstract class Shape extends SceneObject {

    public Material material;

    public Shape(Vec3 pos, Material material) {

        super(pos);

        this.material = material;
    }

    public abstract Intersection intersect(Ray ray);
}

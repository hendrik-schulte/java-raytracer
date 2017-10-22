package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Cube extends SceneObject {


    public Cube(Vec3 center, Vec3 side, Vec3 up, Vec3 forward, Material material) {
        super(center, material);

//        triangles.add(new Rectangle());
    }

//    @Override
//    public ArrayList<Intersection> intersectThis(Ray ray) {
//
//        return getIntersection(ray, rectangles);
//    }
}

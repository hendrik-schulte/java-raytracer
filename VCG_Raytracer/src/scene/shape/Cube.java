package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Cube extends Shape {

    ArrayList<Rectangle> rectangles = new ArrayList<>();

    public Cube(Vec3 center, Vec3 dimension, Material material) {
        super(center, material);


    }

    @Override
    public Intersection[] intersect(Ray ray) {
        return new Intersection[0];
    }
}

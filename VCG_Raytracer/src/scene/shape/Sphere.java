package scene.shape;

import utils.algebra.Vec3;

public class Sphere extends Shape {

    private float radius;

    public Sphere(Vec3 pos, float radius) {
        super(pos);

        this.radius = radius;
    }
}

package scene.shape;

import utils.algebra.Vec3;

public class Plane extends Shape {

    private Vec3 normal;

    public Plane(Vec3 pos, Vec3 normal) {
        super(pos);

        this.normal = normal;
    }
}

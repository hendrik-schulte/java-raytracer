package raytracer;

import utils.algebra.Vec3;

public class Ray {

    Vec3 startPoint;
    Vec3 endPoint;
    Vec3 direction;
    float distance;

    public Ray(Vec3 origin, Vec3 direction){
        startPoint = origin;
        this.direction = direction.normalize();
    }
}

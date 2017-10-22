package scene.shape;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;

public class Triangle extends Plane {

    private boolean initializationFailed = false;
    private Vec2 projA;
    private Vec2 projAB;
    private Vec2 projAC;

    private float dot00;
    private float dot01;
    private float dot11;

    private float invDenom;


    private PlaneProjection projectToPlane;

    public Triangle(Vec3 a, Vec3 b, Vec3 c, boolean drawBack, Material material) {
        super(a, b.sub(a).cross(c.sub(a)), drawBack, material);

        if(Float.isNaN(normal.x) || Float.isNaN(normal.y) || Float.isNaN(normal.z)){
            Log.error(this, "Triangle is invalid!");
            initializationFailed = true;
            return;
        }

        projectToPlane = getPlaneProjection(normal);

        projA = projectToAxisPlane(projectToPlane, a);
        Vec2 projB = projectToAxisPlane(projectToPlane, b);
        Vec2 projC = projectToAxisPlane(projectToPlane, c);
        projAB = projB.sub(projA);
        projAC = projC.sub(projA);

        dot00 = projAC.scalar(projAC);
        dot01 = projAC.scalar(projAB);
        dot11 = projAB.scalar(projAB);

        invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    }

    @Override
    protected ArrayList<Intersection> intersectThis(Ray ray) {
        ArrayList<Intersection> planeIntersection = super.intersectThis(ray);

        if (planeIntersection == null) return null;

        //adapted from http://blackpawn.com/texts/pointinpoly/
        Vec2 projAP = projectToAxisPlane(projectToPlane, planeIntersection.get(0).interSectionPoint).sub(projA);

        float dot02 = projAC.scalar(projAP);
        float dot12 = projAB.scalar(projAP);

        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        if ((u >= 0) && (v >= 0) && (u + v < 1)) {
            return planeIntersection;
        }

        return null;
    }

    public boolean isValidTriangle(){
        return !initializationFailed;
//        return !Float.isNaN(normal.x) && !Float.isNaN(normal.y) && !Float.isNaN(normal.z);
    }
}

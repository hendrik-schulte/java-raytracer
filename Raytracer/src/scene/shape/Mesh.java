package scene.shape;

import javafx.scene.shape.MeshView;
import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

import java.util.ArrayList;

public class Mesh extends SceneObject {

    ArrayList<SceneObject> triangles = new ArrayList<>();

    public Mesh(Vec3 center, Vec3 scale/*, Material material*/, MeshView[] meshView) {
        super("Mesh",center, null);

        for (MeshView mesh : meshView) {

//            triangles.add(new Mesh(center, scale, mesh));
        }

    }

//    public Mesh(Vec3 center, Vec3 scale/*, Material material*/, MeshView mesh) {
//        super(center, null);
//
//        Material material = Material.parseMaterial(mesh.getMaterial());
//
//        TriangleMesh triangleMesh = (TriangleMesh) mesh.getMesh();
//
////        Log.print(this, "mesh type is " + triangleMesh.getClass().getName());
//
//        float[] dimensions = triangleMesh.getPoints().toArray(new float[triangleMesh.getPoints().size()]);
//
//        ArrayList<Vec3> points = getPoints(dimensions);
//
//        generateTriangles(points, scale, material);
//
//        for (Vec3 point : points) {
//
//
//        }
//
//    }

    private ArrayList<Vec3> getPoints(float[] dimensions) {
        ArrayList<Vec3> points = new ArrayList<>();

        for (int i = 2; i < dimensions.length; i = i + 3) {
            points.add(new Vec3(dimensions[i - 2], dimensions[i - 1], dimensions[i]));
        }

        return points;
    }

    private void generateTriangles(ArrayList<Vec3> points, Vec3 scale, Material material) {


        for (int i = 2; i < points.size(); i = i + 3) {

            Vec3 a = getWorldPosition().add(points.get(i - 2).scale(scale));
            Vec3 b = getWorldPosition().add(points.get(i - 1).scale(scale));
            Vec3 c = getWorldPosition().add(points.get(i).scale(scale));

//            Triangle triangle;

            Triangle triangle = new Triangle(a, b, c, material.opacity < 1, material);

            if (triangle.isValidTriangle()) triangles.add(triangle);

//            Log.print(this, "created triangle");
        }
    }

    @Override
    protected ArrayList<Intersection> intersectThis(Ray ray) {

        return getIntersection(ray, triangles);
    }
}

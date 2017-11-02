package scene;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;

public class SceneObject {

    protected Vec3 mLocalPosition;
    private Vec3 mWorldPosition;

    public Material material;

    private SceneObject parent;
    private ArrayList<SceneObject> children = new ArrayList<>();

    public SceneObject() {
        mLocalPosition = Vec3.ZERO;
    }

    public SceneObject(Vec3 localPosition) {

        mLocalPosition = localPosition;
    }

    public SceneObject(Vec3 localPosition, Material material) {

        mLocalPosition = localPosition;
        this.material = material;
    }

    public Vec3 getWorldPosition() {

        if (mWorldPosition == null) {
            calcWorldPos();
        }

        return mWorldPosition;
    }

    /**
     * Returns the position of this object relative to its parent.
     *
     * @return
     */
    public Vec3 getLocalPosition() {

        return mLocalPosition;
    }

    /**
     * Sets the given object as a child of this.
     * @param child
     */
    public void setChild(SceneObject child){
        child.setParent(this);
    }

    /**
     * Sets the parent of this object. Use null to
     *
     * @param parent
     */
    public void setParent(SceneObject parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        this.parent = parent;

        if (parent != null) {
            parent.children.add(this);
        }

        calcWorldPos();
    }

    public void calcWorldPos() {
        if (parent != null)
            mWorldPosition = parent.getWorldPosition().add(mLocalPosition);
        else
            mWorldPosition = mLocalPosition;
    }

    /**
     * Calculates the intersection point of the given ray with this object without concerning children.
     *
     * @param ray
     * @return
     */
    protected ArrayList<Intersection> intersectThis(Ray ray) {
        return new ArrayList<>();
    }

    protected static ArrayList<Intersection> getIntersection(Ray ray, ArrayList<SceneObject> shapes) {
        ArrayList<Intersection> result = new ArrayList<>();

        for (SceneObject shape : shapes) {
            result.addAll(shape.intersectAll(ray));
        }

        return result;
    }

    /**
     * Calculates the intersection of the given ray with this object and all its children recursively.
     *
     * @param ray
     * @return
     */
    public ArrayList<Intersection> intersectAll(Ray ray) {

        ArrayList<Intersection> selfIntersec = intersectThis(ray);
        ArrayList<Intersection> childIntersec = intersectChildren(ray);

//        Log.print(this, " self intersec: " + selfIntersec.size());
//        Log.print(this, " child intersec: " + childIntersec.size());

        selfIntersec.addAll(childIntersec);

//        Log.print(this, " total intersec: " + selfIntersec.size());


        return selfIntersec;
    }


    private ArrayList<Intersection> intersectChildren(Ray ray) {

        return getIntersection(ray, children);
    }

    public ArrayList<SceneObject> getChildren() {
        return new ArrayList<>(children);
    }

    protected ArrayList<Intersection> toList(Intersection intersection) {
        ArrayList<Intersection> result = new ArrayList<>();

        result.add(intersection);

        return result;
    }

    protected ArrayList<Intersection> toList(Intersection inter1, Intersection inter2) {
        ArrayList<Intersection> result = toList(inter1);

        result.add(inter2);

//        Log.print(this,"two intersection sphere " + result.size());

        return result;
    }
//
//    protected Ray WorldToLocal(Ray worldRay){
//        Vec3 pos = worldRay.getStartPoint();
//        Vec3 dir = worldRay.getDirection();
//
//        return new Ray(pos, dir);
//    }

    public String getHirarchyDescription(){
        String desc = "";

        return desc;
    }
}

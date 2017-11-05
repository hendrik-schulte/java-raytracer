package scene;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;

public class SceneObject {

    public String name;

    protected Vec3 mLocalPosition;
    private Vec3 mWorldPosition;

    public Material material;

    private SceneObject parent;
    private ArrayList<SceneObject> children = new ArrayList<>();

    public SceneObject(String name, Vec3 localPosition) {
        this(name, localPosition, null);
    }

    public SceneObject(String name) {
        this(name, Vec3.ZERO);
    }

    public SceneObject() {
        this("", Vec3.ZERO, null);
    }

    public SceneObject(Vec3 localPosition) {
        this("", localPosition, null);
    }

    public SceneObject(Vec3 localPosition, Material material) {
        this("", localPosition, material);
    }

    public SceneObject(String name, Vec3 localPosition, Material material) {

        this.name = name;
        mLocalPosition = localPosition;
        this.material = material;
    }

    /**
     * Sets the local position of this object to the given value. Refreshes world positions for all children.
     *
     * @param localPosition
     */
    public void setLocalPosition(Vec3 localPosition) {
        mLocalPosition = localPosition;

        calcWorldPos();
    }

    /**
     * Sets the local position of this object to the given value. Refreshes world positions for all children.
     *
     * @param worldPosition
     */
    public void setWorldPosition(Vec3 worldPosition) {

        if (hasParent()) {
            setLocalPosition(worldPosition.sub(parent.getWorldPosition()));
        } else {
            setLocalPosition(worldPosition);
        }
    }

    /**
     * Returns the position of this object in world space.
     *
     * @return
     */
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
     *
     * @param child
     */
    public void setChild(SceneObject child) {
        child.setParent(this);
    }

    /**
     * Sets the parent of this object.
     *
     * @param parent
     */
    public void setParent(SceneObject parent) {
        if (hasParent()) {
            this.parent.children.remove(this);
        }

        this.parent = parent;

        if (hasParent()) {
            parent.children.add(this);
        }

        calcWorldPos();
//        Log.print(this, this.toString() + ":" + " parented to " + parent.toString());
//        Log.print(this, " parented to " + parent.toString() + " children " + children.size());
    }

    protected void calcWorldPos() {
        if (parent != null)
            mWorldPosition = parent.getWorldPosition().add(mLocalPosition);
        else
            mWorldPosition = mLocalPosition;

        for (SceneObject child : children) {
            child.calcWorldPos();
        }
    }

    /**
     * Returns true if this object has a parent.
     *
     * @return
     */
    public boolean hasParent() {
        return parent != null;
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

    public void printRecursively() {
        Log.print(new SceneObject(), "Printing Scenegraph");
        printRecursively(0);
    }

    private void printRecursively(int depth) {

        String out = "";

        for (int i = -1; i < depth; i++) {
            out += "   ";
        }
        out += "-> ";

//        out += toString();
        Log.print(new SceneObject(), out + toString());

        for (SceneObject child : children) {
            child.printRecursively(depth + 1);
        }

    }

    @Override
    public String toString() {
        return super.toString() + " " + name + "(" + children.size() + ")";
    }
}

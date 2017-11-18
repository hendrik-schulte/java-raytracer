package scene;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Quaternion;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;

public class SceneObject {

    //region Fields

    public String name;

    private Vec3 mLocalPosition;
    private Vec3 mWorldPosition;

    private Vec3 mLocalScale;
    private Vec3 mWorldScale;

    private Quaternion mLocalRotation;
    private Quaternion mWorldRotation;

    public Material material;

    private SceneObject parent;
    private ArrayList<SceneObject> children = new ArrayList<>();

    //endregion

    //region Contructors

    public SceneObject(String name, Vec3 localPosition, Quaternion localRotation, Vec3 localScale, Material material) {
        this.name = name;
        mLocalPosition = localPosition;
        mLocalRotation = localRotation;
        mLocalScale = localScale;
        this.material = material;
    }

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

    public SceneObject(Vec3 localPosition, Quaternion localRotation, Vec3 localScale, Material material) {
        this("", localPosition, localRotation, localScale, material);
    }

    public SceneObject(Vec3 localPosition, Material material) {
        this("", localPosition, material);
    }

    public SceneObject(String name, Vec3 localPosition, Material material) {
        this(name, localPosition, Quaternion.IDENTITY, Vec3.ONE, material);
    }

    //endregion

    //region Parenting

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

        calcWorldPosition();
        calcWorldRotation();
        calcWorldScale();
        //        Log.print(this, this.toString() + ":" + " parented to " + parent.toString());
//        Log.print(this, " parented to " + parent.toString() + " children " + children.size());
    }


    /**
     * Returns true if this object has a parent.
     *
     * @return
     */
    public boolean hasParent() {
        return parent != null;
    }


    protected ArrayList<Intersection> toList(Intersection intersection) {
        ArrayList<Intersection> result = new ArrayList<>();

        result.add(intersection);

        return result;
    }

    //endregion

    //region Position

    /**
     * Returns the position of this object in world space.
     *
     * @return
     */
    public Vec3 getWorldPosition() {

        if (mWorldPosition == null) calcWorldPosition();

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

    private void calcWorldPosition() {
        if (parent != null)
            mWorldPosition = parent.getWorldPosition().add(mLocalPosition);
        else
            mWorldPosition = mLocalPosition;

        for (SceneObject child : children) {
            child.calcWorldPosition();
        }
    }

    /**
     * Sets the local position of this object to the given value. Refreshes world positions for all children.
     *
     * @param localPosition
     */
    public void setLocalPosition(Vec3 localPosition) {
        mLocalPosition = localPosition;

        calcWorldPosition();
    }

    /**
     * Sets the world space position of this object to the given value. Refreshes world positions for all children.
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

    //endregion

    //region Rotation

    /**
     * Returns the rotation of this object in World space.
     *
     * @return
     */
    public Quaternion getWorldRotation() {

        if (mWorldRotation == null) calcWorldRotation();

        return mWorldRotation;
    }

    /**
     * Returns the rotation of this object relative to its parent.
     *
     * @return
     */
    public Quaternion getLocalRotation() {

        return mLocalRotation;
    }

    private void calcWorldRotation() {
        if (hasParent())
            mWorldRotation = parent.getWorldRotation().mult(mLocalRotation);
        else
            mWorldRotation = mLocalRotation;

        for (SceneObject child : children) {
            child.calcWorldRotation();
        }
    }

    /**
     * Sets the local rotation of this object to the given value. Refreshes world rotations for all children.
     *
     * @param localRotation
     */
    public void setLocalRotation(Quaternion localRotation) {
        mLocalRotation = localRotation;

        calcWorldRotation();
    }

    /**
     * Sets the world space rotation of this object to the given value. Refreshes world positions for all children.
     *
     * @param worldRotation
     */
    public void setWorldRotation(Quaternion worldRotation) {

        if (hasParent()) {
            setLocalRotation(parent.getWorldRotation().inverse().mult(worldRotation));
        } else {
            setLocalRotation(worldRotation);
        }
    }

    //endregion

    // region Scale

    /**
     * Returns the scale of this object in World space.
     *
     * @return
     */
    public Vec3 getWorldScale() {

        if (mWorldScale == null) calcWorldScale();

        return mWorldScale;
    }

    /**
     * Returns the scale of this object relative to its parent.
     *
     * @return
     */
    public Vec3 getLocalScale() {

        return mLocalScale;
    }

    private void calcWorldScale() {
        if (hasParent())
            mWorldScale = parent.getWorldScale().scale(mLocalScale);
        else
            mWorldScale = mLocalScale;

        for (SceneObject child : children) {
            child.calcWorldScale();
        }
    }

    /**
     * Sets the local scale of this object to the given value. Refreshes world scale for all children.
     *
     * @param localScale
     */
    public void setLocalScale(Vec3 localScale) {
        mLocalScale = localScale;

        calcWorldScale();
    }

    /**
     * Sets the world space scale of this object to the given value. Refreshes world scale for all children.
     *
     * @param worldScale
     */
    public void setWorldScale(Vec3 worldScale) {

        if (hasParent()) {
            setLocalScale(parent.getWorldScale().inverseScale().scale(worldScale));
        } else {
            setLocalScale(worldScale);
        }
    }

    //endregion

    //region Intersection

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

        //transform ray from parent to local space
        Vec3 rayPos = ray.getStartPoint()/*.sub(mLocalPosition)*/;
        Vec3 rayDir = ray.getDirection();

//        Ray localRay = new Ray(mLocalRotation.mult(ray.getStartPoint()), mLocalRotation.mult(ray.getDirection()));
        Ray localRay = new Ray(rayPos, rayDir);

        ArrayList<Intersection> selfIntersec = intersectThis(localRay);
        ArrayList<Intersection> childIntersec = intersectChildren(localRay);

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

    //endregion

    //region Debug

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

    //endregion
}

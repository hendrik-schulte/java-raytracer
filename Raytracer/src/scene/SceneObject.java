package scene;

import material.Material;
import raytracer.Intersection;
import raytracer.Ray;
import utils.algebra.Matrix4x4;
import utils.algebra.Quaternion;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;

public class SceneObject {

    //region Fields

    public String name;

    private Matrix4x4 mLocalTransform;
    private Matrix4x4 mLocalTransformInverse;
    private Matrix4x4 mWorldTransform;
    private Matrix4x4 mWorldTransformInverse;

    public Material material;

    private SceneObject parent;
    private ArrayList<SceneObject> children = new ArrayList<>();

    //endregion

    //region Contructors

    public SceneObject(String name, Matrix4x4 localTransform, Material material) {
        this.name = name;
        this.material = material;
        this.setLocalTransform(localTransform);
    }

    public SceneObject(String name, Matrix4x4 localTransform) {
        this(name, localTransform, null);
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

    public SceneObject(Vec3 localPosition, Material material) {

        this("", localPosition, material);
    }

    public SceneObject(String name, Vec3 localPosition, Material material) {
        this.name = name;
        this.material = material;
        this.setLocalPosition(localPosition);
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

        calcWorldTransform();
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

    /**
     * walks up the scene graph and returns its root.
     *
     * @return
     */
    public SceneObject getRoot() {
        if (hasParent()) return parent.getRoot();

        return this;
    }

    //endregion

    //region Transformation

    /**
     * Returns the position of this object in world space.
     *
     * @return
     */
    public Vec3 getWorldPosition() {

        if (mWorldTransform == null) calcWorldTransform();

        return mWorldTransform.getTranslation();
//        return mWorldTransform.mult(Vec3.ZERO, true);
    }

    /**
     * Returns the transform of this object in world space.
     *
     * @return
     */
    public Matrix4x4 getWorldTransform() {

        if (mWorldTransform == null) calcWorldTransform();

        return mWorldTransform;
    }

    /**
     * Returns the inverse of the transform of this object in world space.
     *
     * @return
     */
    public Matrix4x4 getWorldTransformInverse() {

        if (mWorldTransformInverse == null) calcWorldTransform();

        return mWorldTransformInverse;
    }

    /**
     * Returns the transform of this object in local space.
     *
     * @return
     */
    public Matrix4x4 getLocalTransform() {

        return mLocalTransform;
    }

    /**
     * Returns the inverse of the transform of this object in local space.
     *
     * @return
     */
    public Matrix4x4 getLocalTransformInverse() {

        return mLocalTransformInverse;
    }

    /**
     * Returns the position of this object relative to its parent including its local scale.
     *
     * @return
     */
    public Vec3 getLocalPosition() {
//        return mLocalTransform.mult(Vec3.ZERO, true);
        return mLocalTransform.getTranslation();
    }

    /**
     * Calculates the world transform of this object and all of its children.
     */
    private void calcWorldTransform() {

        if (parent != null)
            mWorldTransform = mLocalTransform.mult(parent.getWorldTransform());
        else
            mWorldTransform = new Matrix4x4(mLocalTransform);

        mWorldTransformInverse = mWorldTransform.invert();

        onTransformChange();

        for (SceneObject child : children) {
            child.calcWorldTransform();
        }
    }

    /**
     * This is called when the world transform of this object is recalculated.
     */
    protected void onTransformChange(){
    }

    /**
     * Sets the local position of this object to the given value. Refreshes world positions for all children.
     *
     * @param localPosition
     */
    public void setLocalPosition(Vec3 localPosition) {

        if (mLocalTransform == null) mLocalTransform = new Matrix4x4();

        mLocalTransform.setTranslation(localPosition);
        mLocalTransformInverse = mLocalTransform.invert();

        calcWorldTransform();
    }

    public void setLocalTransform(Matrix4x4 localTransform) {
        mLocalTransform = localTransform;
        mLocalTransformInverse = mLocalTransform.invert();

        calcWorldTransform();
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

    //region Intersection

    /**
     * Calculates the intersection point of the given ray with this object without concerning children.
     * This need to be overridden for inheriting classes.
     *
     * @param localRay ray in local space of this object.
     * @return
     */
    protected ArrayList<Intersection> intersectThis(Ray localRay) {
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
        Ray localRay = mLocalTransformInverse.mult(ray);

        ArrayList<Intersection> selfIntersec = intersectThis(localRay);
        ArrayList<Intersection> childIntersec = intersectChildren(localRay);

        selfIntersec.addAll(childIntersec);

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

    protected Intersection calcWorldSpaceIntersection(Ray localRay, Vec3 worldNormal, double t) {

        Vec3 intersectionPoint = getWorldTransform().multPoint(localRay.calcPoint(t));
        double distancePWD = localRay.getWorldSpaceRay().getStartPoint().distanceSquared(intersectionPoint);

        return new Intersection(intersectionPoint, worldNormal, this, distancePWD);
    }

//    protected Intersection getWorldSpaceIntersectionLocalNormal(Ray localRay, Vec3 localNormal, double t) {
//
//        Vec3 intersectionPoint = getWorldTransform().mult(localRay.calcPoint(t), true);
//        Vec3 normal = getWorldTransform().mult(localNormal, false);
//        double distancePWD = localRay.getWorldSpaceRay().getStartPoint().distanceSquared(intersectionPoint);
//
//        return new Intersection(intersectionPoint, normal, this, distancePWD);
//    }

    //endregion

    //region Debug

    public void printRecursively() {
        Log.print(getRoot(), "Printing Scenegraph");
        printRecursively(0);
    }

    private void printRecursively(int depth) {

        String out = "";

        for (int i = 0; i < depth; i++) {
            out += "   ";
        }
        out += "-> ";

//        out += toString();
        Log.print(getRoot(), out + toString());

        for (SceneObject child : children) {
            child.printRecursively(depth + 1);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " " + name + " local: " + getLocalPosition() + " world: " + getWorldPosition();
    }

    //endregion
}

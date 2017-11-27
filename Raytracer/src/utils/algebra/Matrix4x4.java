package utils.algebra;

import raytracer.Ray;

public class Matrix4x4 {

    private Matrix mBaseMatrix;

    //region Contructors

    /**
     * Constructs and initializes a Matrix4f from the quaternion,
     * translation, and setScale values; the setScale is applied only to the
     * rotational components of the matrix (upper 3x3) and not to the
     * translational components.
     * <p>
     * Adapted from: https://github.com/hharrison/vecmath/blob/master/src/javax/vecmath/Matrix4f.java
     *
     * @param q           the quaternion value representing the rotational component
     * @param translation the translational component of the matrix
     * @param s           the setScale value applied to the rotational components
     */
    public Matrix4x4(Quaternion q, Vec3 translation, float s) {

        mBaseMatrix = new Matrix(4, 4);

        set(0, 0, (s * (1.0 - 2.0 * q.y * q.y - 2.0 * q.z * q.z)));
        set(1, 0, (s * (2.0 * (q.x * q.y + q.w * q.z))));
        set(2, 0, (s * (2.0 * (q.x * q.z - q.w * q.y))));

        set(0, 1, (s * (2.0 * (q.x * q.y - q.w * q.z))));
        set(1, 1, (s * (1.0 - 2.0 * q.x * q.x - 2.0 * q.z * q.z)));
        set(2, 1, (s * (2.0 * (q.y * q.z + q.w * q.x))));

        set(0, 2, (s * (2.0 * (q.x * q.z + q.w * q.y))));
        set(1, 2, (s * (2.0 * (q.y * q.z - q.w * q.x))));
        set(2, 2, (s * (1.0 - 2.0 * q.x * q.x - 2.0 * q.y * q.y)));

        setTranslation(translation);

        set(3, 0, 0);
        set(3, 1, 0);
        set(3, 2, 0);
        set(3, 3, 1);
    }

    /**
     * Constructs and initializes a Matrix4f from the quaternion,
     * translation, and setScale values; the setScale is applied only to the
     * rotational components of the matrix (upper 3x3) and not to the
     * translational components.
     * <p>
     * Adapted from: https://github.com/hharrison/vecmath/blob/master/src/javax/vecmath/Matrix4f.java
     *
     * @param q           the quaternion value representing the rotational component
     * @param translation the translational component of the matrix
     * @param s           the setScale value applied to the rotational components
     */
    public Matrix4x4(Quaternion q, Vec3 translation, Vec3 s) {

        mBaseMatrix = new Matrix(4, 4);

        set(0, 0, (s.x * (1.0 - 2.0 * q.y * q.y - 2.0 * q.z * q.z)));
        set(1, 0, (s.y * (2.0 * (q.x * q.y + q.w * q.z))));
        set(2, 0, (s.z * (2.0 * (q.x * q.z - q.w * q.y))));

        set(0, 1, (s.x * (2.0 * (q.x * q.y - q.w * q.z))));
        set(1, 1, (s.y * (1.0 - 2.0 * q.x * q.x - 2.0 * q.z * q.z)));
        set(2, 1, (s.z * (2.0 * (q.y * q.z + q.w * q.x))));

        set(0, 2, (s.x * (2.0 * (q.x * q.z + q.w * q.y))));
        set(1, 2, (s.y * (2.0 * (q.y * q.z - q.w * q.x))));
        set(2, 2, (s.z * (1.0 - 2.0 * q.x * q.x - 2.0 * q.y * q.y)));

        setTranslation(translation);

        set(3, 0, 0);
        set(3, 1, 0);
        set(3, 2, 0);
        set(3, 3, 1);
    }

    /**
     * Constructs and initializes a Matrix4x4 from the quaternion
     * and translation.
     * <p>
     * Adapted from: https://github.com/hharrison/vecmath/blob/master/src/javax/vecmath/Matrix4f.java
     *
     * @param q           the quaternion value representing the rotational component
     * @param translation the translational component of the matrix
     */
    public Matrix4x4(Quaternion q, Vec3 translation) {
        this(q, translation, 1);
    }

    /**
     * Constructs and initializes a Matrix4x4 from the rotation quaternion.
     * <p>
     * Adapted from: https://github.com/hharrison/vecmath/blob/master/src/javax/vecmath/Matrix4f.java
     *
     * @param q the quaternion value representing the rotational component
     */
    public Matrix4x4(Quaternion q) {
        this(q, Vec3.ZERO, 1);
    }

    /**
     * The standard constructor will produce an identity matrix
     **/
    public Matrix4x4() {
        mBaseMatrix = Matrix.identity(4, 4);
    }

    public Matrix4x4(Vec3 position) {
        mBaseMatrix = Matrix.identity(4, 4);

        setTranslation(position);
    }

    public Matrix4x4(Vec3 position, float scale) {
        mBaseMatrix = Matrix.identity(4, 4);

        setTranslation(position);
        setScale(scale);
    }

    public Matrix4x4(Vec3 position, Vec3 scale) {
        mBaseMatrix = Matrix.identity(4, 4);

        setTranslation(position);
        setScale(scale);
    }

    private Matrix4x4(Matrix mat) {
        mBaseMatrix = mat;
    }

    /**
     * Creates a deep copy of the given matrix.
     * @param copy
     */
    public Matrix4x4(Matrix4x4 copy) {
        mBaseMatrix = Matrix.identity(4, 4);

        set(0,0, copy.get(0,0));
        set(0,1, copy.get(0,1));
        set(0,2, copy.get(0,2));
        set(0,3, copy.get(0,3));

        set(1,0, copy.get(1,0));
        set(1,1, copy.get(1,1));
        set(1,2, copy.get(1,2));
        set(1,3, copy.get(1,3));

        set(2,0, copy.get(2,0));
        set(2,1, copy.get(2,1));
        set(2,2, copy.get(2,2));
        set(2,3, copy.get(2,3));

        set(3,0, copy.get(3,0));
        set(3,1, copy.get(3,1));
        set(3,2, copy.get(3,2));
        set(3,3, copy.get(3,3));
    }

    //endregion

    /**
     * Translate Matrix in 4D - watch for the homogeneous coordinate
     **/
    public Matrix4x4 translateXYZW(Vec4 vec) {
        Matrix4x4 out = new Matrix4x4(mBaseMatrix);

        out.set(0, 3, vec.x);
        out.set(1, 3, vec.y);
        out.set(2, 3, vec.z);
        out.set(3, 3, vec.w);

        return out;
    }

    /**
     * Transforms the given ray by this matrix and returns the result.
     *
     * @param ray
     * @return
     */
    public Ray mult(Ray ray) {

        Vec3 position = multPoint(ray.getStartPoint());
        Vec3 direction = multVec(ray.getDirection());

        return new Ray(position, direction, ray.getWorldSpaceRay());
    }

    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1.
     *
     * Adapted from: https://github.com/hharrison/vecmath/blob/master/src/javax/vecmath/Matrix4f.java
     *
     * @param rhs the other matrix
     */
    public final Matrix4x4 mult(Matrix4x4 rhs) {
        Matrix4x4 result = new Matrix4x4();

        result.set(0, 0, this.get(0, 0) * rhs.get(0, 0) + this.get(0, 1) * rhs.get(1, 0) +
                this.get(0, 2) * rhs.get(2, 0) + this.get(0, 3) * rhs.get(3, 0));
        result.set(0, 1, this.get(0, 0) * rhs.get(0, 1) + this.get(0, 1) * rhs.get(1, 1) +
                this.get(0, 2) * rhs.get(2, 1) + this.get(0, 3) * rhs.get(3, 1));
        result.set(0, 2, this.get(0, 0) * rhs.get(0, 2) + this.get(0, 1) * rhs.get(1, 2) +
                this.get(0, 2) * rhs.get(2, 2) + this.get(0, 3) * rhs.get(3, 2));
        result.set(0, 3, this.get(0, 0) * rhs.get(0, 3) + this.get(0, 1) * rhs.get(1, 3) +
                this.get(0, 2) * rhs.get(2, 3) + this.get(0, 3) * rhs.get(3, 3));

        result.set(1, 0, this.get(1, 0) * rhs.get(0, 0) + this.get(1, 1) * rhs.get(1, 0) +
                this.get(1, 2) * rhs.get(2, 0) + this.get(1, 3) * rhs.get(3, 0));
        result.set(1, 1, this.get(1, 0) * rhs.get(0, 1) + this.get(1, 1) * rhs.get(1, 1) +
                this.get(1, 2) * rhs.get(2, 1) + this.get(1, 3) * rhs.get(3, 1));
        result.set(1, 2, this.get(1, 0) * rhs.get(0, 2) + this.get(1, 1) * rhs.get(1, 2) +
                this.get(1, 2) * rhs.get(2, 2) + this.get(1, 3) * rhs.get(3, 2));
        result.set(1, 3, this.get(1, 0) * rhs.get(0, 3) + this.get(1, 1) * rhs.get(1, 3) +
                this.get(1, 2) * rhs.get(2, 3) + this.get(1, 3) * rhs.get(3, 3));

        result.set(2, 0, this.get(2, 0) * rhs.get(0, 0) + this.get(2, 1) * rhs.get(1, 0) +
                this.get(2, 2) * rhs.get(2, 0) + this.get(2, 3) * rhs.get(3, 0));
        result.set(2, 1, this.get(2, 0) * rhs.get(0, 1) + this.get(2, 1) * rhs.get(1, 1) +
                this.get(2, 2) * rhs.get(2, 1) + this.get(2, 3) * rhs.get(3, 1));
        result.set(2, 2, this.get(2, 0) * rhs.get(0, 2) + this.get(2, 1) * rhs.get(1, 2) +
                this.get(2, 2) * rhs.get(2, 2) + this.get(2, 3) * rhs.get(3, 2));
        result.set(2, 3, this.get(2, 0) * rhs.get(0, 3) + this.get(2, 1) * rhs.get(1, 3) +
                this.get(2, 2) * rhs.get(2, 3) + this.get(2, 3) * rhs.get(3, 3));

        result.set(3, 0, this.get(3, 0) * rhs.get(0, 0) + this.get(3, 1) * rhs.get(1, 0) +
                this.get(3, 2) * rhs.get(2, 0) + this.get(3, 3) * rhs.get(3, 0));
        result.set(3, 1, this.get(3, 0) * rhs.get(0, 1) + this.get(3, 1) * rhs.get(1, 1) +
                this.get(3, 2) * rhs.get(2, 1) + this.get(3, 3) * rhs.get(3, 1));
        result.set(3, 2, this.get(3, 0) * rhs.get(0, 2) + this.get(3, 1) * rhs.get(1, 2) +
                this.get(3, 2) * rhs.get(2, 2) + this.get(3, 3) * rhs.get(3, 2));
        result.set(3, 3, this.get(3, 0) * rhs.get(0, 3) + this.get(3, 1) * rhs.get(1, 3) +
                this.get(3, 2) * rhs.get(2, 3) + this.get(3, 3) * rhs.get(3, 3));

        return result;
    }

    /**
     * Scale uniform by factor s
     **/
    public Matrix4x4 setScale(double s) {
        Matrix4x4 out = new Matrix4x4(mBaseMatrix);

        out.set(0, 0, s);
        out.set(1, 1, s);
        out.set(2, 2, s);

        return out;
    }

    /**
     * Scale non-uniform by x, y and z
     **/
    private Matrix4x4 setScale(Vec3 vec) {
        Matrix4x4 out = new Matrix4x4(mBaseMatrix);

        out.set(0, 0, vec.x);
        out.set(1, 1, vec.y);
        out.set(2, 2, vec.z);

        return out;
    }

    /**
     * Returns the translation encoded in this matrix.
     *
     * @return
     */
    public Vec3 getTranslation() {
        Vec3 vec = new Vec3(
                (float) get(0, 3),
                (float) get(1, 3),
                (float) get(2, 3));

        return vec;
    }

    public Vec3 getUniformScale() {
        Vec3 vec = new Vec3(
                (float) get(0, 0),
                (float) get(1, 1),
                (float) get(2, 2));

        return vec;
    }

    /**
     * Sets the translation encoded in this matrix.
     *
     * @param translation
     */
    public void setTranslation(Vec3 translation) {
        set(0, 3, translation.x);
        set(1, 3, translation.y);
        set(2, 3, translation.z);
    }

    /**
     * Transpose matrix
     **/
    public Matrix4x4 transpose() {
        return new Matrix4x4(mBaseMatrix.transpose());
    }

    /**
     * Invert matrix
     **/
    public Matrix4x4 invert() {
        return new Matrix4x4(mBaseMatrix.inverse());
    }

    /**
     * Set 'value' in matrix at position row and column
     **/
    public void set(int row, int col, double value) {
        mBaseMatrix.set(row, col, value);
    }

    /**
     * Get 'value' in matrix from position row and column
     **/
    public double get(int row, int col) {
        return mBaseMatrix.get(row, col);
    }

    /**
     * Multiply a 3D point OR vector with the matrix
     **/
    public Vec4 mult(Vec4 vec) {
        return new Vec4(
                vec.x * (float) this.get(0, 0) + vec.y * (float) this.get(0, 1) + vec.z * (float) this.get(0, 2) + vec.w * (float) this.get(0, 3),
                vec.x * (float) this.get(1, 0) + vec.y * (float) this.get(1, 1) + vec.z * (float) this.get(1, 2) + vec.w * (float) this.get(1, 3),
                vec.x * (float) this.get(2, 0) + vec.y * (float) this.get(2, 1) + vec.z * (float) this.get(2, 2) + vec.w * (float) this.get(2, 3),
                vec.x * (float) this.get(3, 0) + vec.y * (float) this.get(3, 1) + vec.z * (float) this.get(3, 2) + vec.w * (float) this.get(3, 3)
        );
    }

//    /**
//     * Multiply a 3D point OR vector with the matrix
//     **/
//    public Vec3 mult(Vec3 vec, Boolean isPoint) {
//
//        float w = isPoint ? 1 : 0;
//
//        Vec3 out = new Vec3(
//                vec.x * (float) this.get(0, 0) + vec.y * (float) this.get(0, 1) + vec.z * (float) this.get(0, 2) + w * (float) this.get(0, 3),
//                vec.x * (float) this.get(1, 0) + vec.y * (float) this.get(1, 1) + vec.z * (float) this.get(1, 2) + w * (float) this.get(1, 3),
//                vec.x * (float) this.get(2, 0) + vec.y * (float) this.get(2, 1) + vec.z * (float) this.get(2, 2) + w * (float) this.get(2, 3)
//        );
//
//        return new Vec3(out.x, out.y, out.z);
//    }

    /**
     * Multiply a 3D  vector with the matrix
     **/
    public Vec3 multVec(Vec3 vec) {

        return new Vec3(
                vec.x * (float) this.get(0, 0) + vec.y * (float) this.get(0, 1) + vec.z * (float) this.get(0, 2),
                vec.x * (float) this.get(1, 0) + vec.y * (float) this.get(1, 1) + vec.z * (float) this.get(1, 2),
                vec.x * (float) this.get(2, 0) + vec.y * (float) this.get(2, 1) + vec.z * (float) this.get(2, 2)
        );
    }

    /**
     * Multiply a 3D point with the matrix
     **/
    public Vec3 multPoint(Vec3 vec) {

        return new Vec3(
                vec.x * (float) this.get(0, 0) + vec.y * (float) this.get(0, 1) + vec.z * (float) this.get(0, 2) + (float) this.get(0, 3),
                vec.x * (float) this.get(1, 0) + vec.y * (float) this.get(1, 1) + vec.z * (float) this.get(1, 2) + (float) this.get(1, 3),
                vec.x * (float) this.get(2, 0) + vec.y * (float) this.get(2, 1) + vec.z * (float) this.get(2, 2) + (float) this.get(2, 3)
        );
    }

    /**
     * Print values of matrix
     **/
    @Override
    public String toString() {
        return "\n" +
                this.get(0, 0) + "\t\t\t\t\t\t" + this.get(0, 1) + "\t\t\t\t\t\t" + this.get(0, 2) + "\t\t\t\t\t\t" + this.get(0, 3) + "\t\t\n" +
                this.get(1, 0) + "\t\t\t\t\t\t" + this.get(1, 1) + "\t\t\t\t\t\t" + this.get(1, 2) + "\t\t\t\t\t\t" + this.get(1, 3) + "\t\t\n" +
                this.get(2, 0) + "\t\t\t\t\t\t" + this.get(2, 1) + "\t\t\t\t\t\t" + this.get(2, 2) + "\t\t\t\t\t\t" + this.get(2, 3) + "\t\t\n" +
                this.get(3, 0) + "\t\t\t\t\t\t" + this.get(3, 1) + "\t\t\t\t\t\t" + this.get(3, 2) + "\t\t\t\t\t\t" + this.get(3, 3) + "\t\t\n";
    }
}

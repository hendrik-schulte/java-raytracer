package utils.algebra;

import raytracer.Ray;
import utils.MathEx;

import static java.lang.Math.*;

public class Quaternion {

    private final static double EPS2 = 1.0e-30;
    /**
     * Constant for slerp
     */
    private final static double DOT_THRESHOLD = 0.9995;

    public static final Quaternion IDENTITY = new Quaternion(0, 0, 0, 1);

    /**
     * The x coordinate.
     */
    public float x;
    /**
     * The y coordinate.
     */
    public float y;
    /**
     * The z coordinate.
     */
    public float z;
    /**
     * The w coordinate.
     */
    public float w;

    //region Constructors

    /**
     * Constructs and initializes a Quaternion from the specified xyzw coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w scalar component
     */
    public Quaternion(float x, float y, float z, float w) {
        float mag;
        mag = (float) (1.0 / Math.sqrt(x * x + y * y + z * z + w * w));
        this.x = x * mag;
        this.y = y * mag;
        this.z = z * mag;
        this.w = w * mag;
    }

    /**
     * Constructs and initializes a Quaternion from the specified xyzw coordinates of a vector.
     *
     * @param v vector
     */
    public Quaternion(Vec4 v) {
        this(v.x, v.y, v.z, v.w);
    }

    /**
     * Constructs and initializes a Quaternionto (0.0,0.0,0.0,0.0).
     */
    public Quaternion() {
    }

    /**
     * Constructs and initializes a Quaternion to with the same values as the given Quaternion.
     */
    public Quaternion(Quaternion q) {
        this(q.x, q.y, q.z, q.w);
    }

    /**
     * Constructs a Quaternion by the rotational component of
     * the passed matrix.
     *
     * @param m1 the Matrix4x4
     */
    public Quaternion(Matrix4x4 m1) {
        float ww = (float) (0.25f * (m1.get(0, 0) + m1.get(1, 1) + m1.get(2, 2) + m1.get(3, 3)));

        if (ww >= 0) {
            if (ww >= EPS2) {
                this.w = (float) Math.sqrt((double) ww);
                ww = 0.25f / this.w;
                this.x = ((float) m1.get(2, 1) - (float) m1.get(1, 2)) * ww;
                this.y = ((float) m1.get(0, 2) - (float) m1.get(2, 0)) * ww;
                this.z = ((float) m1.get(1, 0) - (float) m1.get(0, 1)) * ww;
                return;
            }
        } else {
            this.w = 0;
            this.x = 0;
            this.y = 0;
            this.z = 1;
            return;
        }

        this.w = 0;
        ww = -0.5f * ((float) m1.get(1, 1) + (float) m1.get(2, 2));

        if (ww >= 0) {
            if (ww >= EPS2) {
                this.x = (float) Math.sqrt((double) ww);
                ww = 1.0f / (2.0f * this.x);
                this.y = (float) m1.get(1, 0) * ww;
                this.z = (float) m1.get(2, 0) * ww;
                return;
            }
        } else {
            this.x = 0;
            this.y = 0;
            this.z = 1;
            return;
        }

        this.x = 0;
        ww = 0.5f * (1.0f - (float) m1.get(2, 2));

        if (ww >= EPS2) {
            this.y = (float) Math.sqrt((double) ww);
            this.z = (float) m1.get(2, 1) / (2.0f * this.y);
            return;
        }

        this.y = 0;
        this.z = 1;
    }

    //endregion

    /**
     * Sets the value of this quaternion to the conjugate of itself.
     */
    public final void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    /**
     * Returns the conjugate of this quaternion.
     */
    public final Quaternion conjugated() {
        Quaternion result = new Quaternion(this);
        result.conjugate();
        return result;
    }

    /**
     * Returns the inverse of this quatenrion.
     */
    public final Quaternion inverse() {
        float norm = 1.0f / (w * w + x * x + y * y + z * z);

        return new Quaternion(norm * w, -norm * x, -norm * y, -norm * z);
    }

    /**
     * Normalizes the value of this quaternion in place.
     */
    public final void normalize() {
        float norm;

        norm = (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);

        if (norm > 0.0f) {
            norm = 1.0f / (float) Math.sqrt(norm);
            this.x *= norm;
            this.y *= norm;
            this.z *= norm;
            this.w *= norm;
        } else {
            this.x = (float) 0.0;
            this.y = (float) 0.0;
            this.z = (float) 0.0;
            this.w = (float) 0.0;
        }
    }

    /**
     * Returns a normalized version of this Quaternion.
     */
    public Quaternion normalized() {
        Quaternion result = new Quaternion(this);
        result.normalize();
        return result;
    }

    /**
     * Multiplies this quaternion with the given
     * quaternion q and returns the result (result = this * q).
     *
     * @param q the other quaternion
     */
    public Quaternion mult(Quaternion q) {
        Quaternion result = new Quaternion();

        result.w = result.w * q.w - result.x * q.x - result.y * q.y - result.z * q.z;
        result.x = result.w * q.x + q.w * result.x + result.y * q.z - result.z * q.y;
        result.y = result.w * q.y + q.w * result.y - result.x * q.z + result.z * q.x;
        result.z = result.w * q.z + q.w * result.z + result.x * q.y - result.y * q.x;

        return result;
    }

    /**
     * Rotates the given vector by this quaternion.
     * Based on: https://blog.molecular-matters.com/2013/05/24/a-faster-quaternion-vector-multiplication/
     **/
    public Vec3 mult(Vec3 v) {

        Vec3 xyz = XYZ();

        Vec3 t = xyz.cross(v).multScalar(2);
        return v.add(t.multScalar(w)).add(xyz.cross(t));
    }

    /**
     * Rotates the given ray by this quaternion.
     * @param ray
     * @return
     */
    public Ray mult(Ray ray){

        return new Ray(mult(ray.getStartPoint()), mult(ray.getDirection()));
    }

    /**
     * Performs a linear interpolation between a and b by the lerp value where lerp = 0 returns a and lerp = 1 returns b.
     *
     * @param a
     * @param b
     * @param lerp
     * @return
     */
    public static Quaternion lerp(Quaternion a, Quaternion b, float lerp) {
        return new Quaternion(
                MathEx.lerp(a.x, b.x, lerp),
                MathEx.lerp(a.y, b.y, lerp),
                MathEx.lerp(a.z, b.z, lerp),
                MathEx.lerp(a.w, b.w, lerp)
        );
    }

    /**
     * Performs a spherical interpolation between a and b by the slerp value where slerp = 0 returns a and slerp = 1 returns b.
     * Adapted from: https://en.wikipedia.org/wiki/Slerp
     *
     * @param a
     * @param b
     * @param slerp
     * @return
     */
    public static Quaternion slerp(Quaternion a, Quaternion b, float slerp) {

        // Only unit quaternions are valid rotations.
        // Normalize to avoid undefined behavior.
        Vec4 v0 = new Vec4(a.normalized());
        Vec4 v1 = new Vec4(b.normalized());

        // Compute the cosine of the angle between the two vectors.
        float dot = v0.scalar(v1);

        if (abs(dot) > DOT_THRESHOLD) {
            // If the inputs are too close for comfort, linearly interpolate
            // and normalize the result.

            return new Quaternion((v1.sub(v0)).mult(slerp).add(v0).normalize());
        }

        // If the dot product is negative, the quaternions
        // have opposite handed-ness and slerp won't take
        // the shorter path. Fix by reversing one quaternion.
        if (dot < 0.0f) {
            v1 = v1.negate();
            dot = -dot;
        }

        MathEx.clamp(dot, -1, 1);           // Robustness: Stay within domain of acos()
        double theta_0 = acos(dot);  // theta_0 = angle between input vectors
        double theta = theta_0 * slerp;    // theta = angle between v0 and result

        Vec4 v2 = v1.sub(v0.mult(dot));
        v2.normalize();              // { v0, v2 } is now an orthonormal basis

        return new Quaternion(v0.mult((float) cos(theta)).add(v2.mult((float) sin(theta))));
    }

    /**
     * Returns a new Quaternion with all components negated.
     *
     * @return
     */
    public Quaternion negate() {
        return new Quaternion(-x, -y, -z, -w);
    }

    public Vec3 XYZ() {
        return new Vec3(x, y, z);
    }

    /**
     * Creates a quaternion from the given euler angles.
     * Adapted from: https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Quaternion euler(float x, float y, float z) {

        // Abbreviations for the various angular functions
        float cy = (float) cos(z * 0.5);
        float sy = (float) sin(z * 0.5);
        float cr = (float) cos(x * 0.5);
        float sr = (float) sin(x * 0.5);
        float cp = (float) cos(y * 0.5);
        float sp = (float) sin(y * 0.5);

//        float w = (cy * cr * cp + sy * sr * sp);
//        float x = (float) (cy * sr * cp - sy * cr * sp);
//        float y = (float) (cy * cr * sp + sy * sr * cp);
//        float z = (float) (sy * cr * cp - cy * sr * sp);

        return new Quaternion(
                cy * sr * cp - sy * cr * sp,
                cy * cr * sp + sy * sr * cp,
                sy * cr * cp - cy * sr * sp,
                cy * cr * cp + sy * sr * sp);
    }

    /**
     * Returns the euler angle representation of this quaternion.
     * Adapted from: https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
     *
     * @return
     */
    public Vec3 eulerAngles() {
        // roll (x-axis rotation)
        double sinr = 2.0 * (w * x + y * z);
        double cosr = 1.0 - 2.0 * (x * x + y * y);
        float roll = (float) atan2(sinr, cosr);

        // pitch (y-axis rotation)
        double sinp = 2.0 * (w * y - z * x);
        float pitch;
        if (abs(sinp) >= 1)
            pitch = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            pitch = (float) asin(sinp);

        // yaw (z-axis rotation)
        double siny = 2.0 * (w * z + x * y);
        double cosy = 1.0 - 2.0 * (y * y + z * z);
        float yaw = (float) atan2(siny, cosy);

        return new Vec3(roll, pitch, yaw);
    }
}


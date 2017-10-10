package utils.algebra;

public class Vec3 {
    public float x;
    public float y;
    public float z;

    public static Vec3 ZERO = new Vec3(0, 0, 0);
    public static Vec3 ONE = new Vec3(1, 1, 1);

    /**
     * Standard 3D constructor taking all values given
     **/
    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Standard 3D constructor setting all values to 0
     **/
    public Vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3(Vec3 copy) {
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
    }

    /**
     * Compare two vectors to check if they are equal
     **/
    public boolean equals(Vec3 inputVec) {
        return (this.x == inputVec.x) && (this.y == inputVec.y) && (this.z == inputVec.z);
    }

    /**
     * Get normalized vector
     **/
    public Vec3 normalize() {
        float length = this.length();
        return new Vec3(this.x / length, this.y / length, this.z / length);
    }

    /**
     * Get length of vector
     **/
    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Get sum of vector with the given vector
     **/
    public Vec3 add(Vec3 inputVec) {
        return new Vec3(this.x + inputVec.x, this.y + inputVec.y, this.z + inputVec.z);
    }

    /**
     * Get difference between vector and the given vector
     **/
    public Vec3 sub(Vec3 inputVec) {
        return new Vec3(this.x - inputVec.x, this.y - inputVec.y, this.z - inputVec.z);
    }

    /**
     * Get opposite vector
     **/
    public Vec3 negate() {
        return new Vec3(-this.x, -this.y, -this.z);
    }

    /**
     * Get scalar product of vector and given vector
     **/
    public float scalar(Vec3 inputVec) {
        return this.x * inputVec.x + this.y * inputVec.y + this.z * inputVec.z;
    }

    /**
     * Get new vector with the given value multiplied to every component
     **/
    public Vec3 multScalar(float value) {
        return new Vec3(this.x * value, this.y * value, this.z * value);
    }

    /**
     * Get new vector with the given value multiplied to every component
     **/
    public Vec3 multScalar(double value) {
        return new Vec3(this.x * (float) value, this.y * (float) value, this.z * (float) value);
    }

    /**
     * Get new vector with the given value multiplied to every component
     **/
    public Vec3 divideScalar(float value) {
        return new Vec3(this.x / value, this.y / value, this.z / value);
    }


    /**
     * Get new vector through the cross product of the vector and the given vector
     **/
    public Vec3 cross(Vec3 inputVec) {
        return new Vec3(
                this.y * inputVec.z - inputVec.y * this.z,
                this.z * inputVec.x - inputVec.z * this.x,
                this.x * inputVec.y - inputVec.x * this.y
        );
    }

    public float distance(Vec3 v) {

        return sub(v).length();
    }

    public float distanceSquared(Vec3 v) {

        Vec3 sub = sub(v);

        return (float) Math.pow(sub.x, 2) +
                (float) Math.pow(sub.y, 2) +
                (float) Math.pow(sub.z, 2);
    }

    public Vec3 multComponentWise(Vec3 mult) {
        return new Vec3(x * mult.x, y * mult.y, z * mult.z);
    }

    /**
     * Returns a normalized orthogonal vector to the given one
     *
     * @return
     */
    public Vec3 getOrthogonal() {

//        float xN = Math.abs(x), yN = Math.abs(y), zN = Math.abs(z);
//
//        //project plane to axis-planes
//        if (xN >= yN && xN >= zN) {
//            //x is biggest normal component
//            return new Vec3(0, z, -y).normalize();
//
//        } else if (yN >= xN && yN >= zN) {
//            //y is biggest normal component
//            return new Vec3(z, 0, -x).normalize();
//
//        } else if (zN >= xN && zN >= yN) {
//            //z is biggest normal component
//            return new Vec3(y, -x, 0).normalize();
//
//        }
//        return null;

        return cross(new Vec3(3 + x * 0.53433435f, -1 + y * -1.53123215f, 5 + z * 0.34656868646f)).normalize();

    }


    /**
     * Returns random vector with length of 1.
     * @return
     */
    public static Vec3 Random() {
        double x0 = -1.0 + Math.random() * 2.0;
        double x1 = -1.0 + Math.random() * 2.0;
        double x2 = -1.0 + Math.random() * 2.0;
        double x3 = -1.0 + Math.random() * 2.0;
        while (x0 * x0 + x1 * x1 + x2 * x2 + x3 * x3 >= 1) {
            x0 = -1.0 + Math.random() * 2.0;
            x1 = -1.0 + Math.random() * 2.0;
            x2 = -1.0 + Math.random() * 2.0;
            x3 = -1.0 + Math.random() * 2.0;
        }
        double a = x0 * x0 + x1 * x1 + x2 * x2 + x3 * x3;
        double x = 2 * (x1 * x3 + x0 * x2) / a;
        double y = 2 * (x2 * x3 - x0 * x1) / a;
        double z = (x0 * x0 + x3 * x3 - x1 * x1 - x2 * x2) / a;
        return new Vec3((float) x, (float) y, (float) z);
    }

    /**
     * Print values
     **/
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}

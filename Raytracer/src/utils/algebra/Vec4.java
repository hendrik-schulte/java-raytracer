package utils.algebra;

import utils.MathEx;

public class Vec4 {
    public float x;
    public float y;
    public float z;
    public float w;

    /**
     * Standard 4D constructor taking all values given
     **/
    public Vec4(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Construts a Vec4 by the values of the given quaternion.
     * @param q
     */
    public Vec4(Quaternion q){
        this(q.x,q.y,q.z,q.w);
    }

    /**
        Standard 4D constructor setting all values to 0
     **/
    public Vec4(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    /**
        Compare two vectors to check if they are equal
     **/
    public boolean equals(Vec4 inputVec){
        return (this.x == inputVec.x) && (this.y == inputVec.y) && (this.z == inputVec.z) && (this.w == inputVec.w);
    }

    /**
        Get normalized vector
     **/
    public Vec4 normalize(){
        double length = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        return new Vec4( (float) (((double) this.x) / length), (float) (((double) this.y) / length), (float) (((double) this.z) / length), (float) (((double) this.w) / length));
    }

    /**
        Get length of vector
     **/
    public float length(){
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    /**
        Get sum of vector with the given vector
     **/
    public Vec4 add(Vec4 inputVec){
        return new Vec4(this.x + inputVec.x, this.y + inputVec.y, this.z + inputVec.z, this.w + inputVec.w);
    }

    /**
        Get difference between vector and the given vector
     **/
    public Vec4 sub(Vec4 inputVec){
        return new Vec4(this.x - inputVec.x, this.y - inputVec.y, this.z - inputVec.z, this.w - inputVec.w);
    }

    /**
        Get opposite vector
     **/
    public Vec4 negate(){
        return new Vec4(-this.x, -this.y, -this.z, -this.w);
    }

    /**
        Get scalar product of vector and given vector
     **/
    public float scalar(Vec4 inputVec){
        return this.x * inputVec.x + this.y * inputVec.y + this.z * inputVec.z + this.w * inputVec.w;
    }

    /**
        Get new vector with the given value multiplied to every component
     **/
    public Vec4 mult(float value){
        return new Vec4(this.x * value, this.y * value, this.z * value, this.w * value);
    }

    /**
        Get new vector through the cross product of the vector and the given vector
     **/
    public Vec4 cross(Vec4 inputVec){
        return new Vec4(
                this.y * inputVec.z - inputVec.y * this.z,
                this.z * inputVec.w - inputVec.z * this.w,
                this.w * inputVec.x - inputVec.w * this.x,
                this.x * inputVec.y - inputVec.y * this.x
        );
    }


    /**
     * Performs a linear interpolation between a and b by the lerp value where lerp = 0 returns a and lerp = 1 returns b.
     * @param a
     * @param b
     * @param lerp
     * @return
     */
    public static Vec4 lerp(Vec4 a, Vec4 b, float lerp){
        return new Vec4(
                MathEx.lerp(a.x, b.x, lerp),
                MathEx.lerp(a.y, b.y, lerp),
                MathEx.lerp(a.z, b.z, lerp),
                MathEx.lerp(a.w, b.w, lerp)
        );
    }

    /**
        Print values
     **/
    @Override
    public String toString(){
        return "( " + this.x + ", " + this.y + ", " + this.z + ")";
    }
}

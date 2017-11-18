package utils.algebra;


import utils.MathEx;

public class Vec2 {
    public float x;
    public float y;

    public static Vec2 ZERO = new Vec2(0, 0);
    public static Vec2 ONE = new Vec2(1, 1);

    /**
        Standard 2D constructor taking all values given
     **/
    public Vec2(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
        Standard 2D constructor setting all values to 0
     **/
    public Vec2(){
        this.x = 0;
        this.y = 0;
    }

    /**
        Compare two vectors to check if they are equal
     **/
    public boolean equals(Vec2 inputVec){
        return (this.x == inputVec.x) && (this.y == inputVec.y);
    }

    /**
        Get normalized vector
     **/
    public Vec2 normalize(){
        float length = this.length();
        return new Vec2(this.x / length, this.y / length);
    }

    /**
        Get length of vector
     **/
    public float length(){
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
        Get sum of vector with the given vector
     **/
    public Vec2 add(Vec2 inputVec){
        return new Vec2(this.x + inputVec.x, this.y + inputVec.y);
    }

    /**
        Get difference between vector and the given vector
     **/
    public Vec2 sub(Vec2 inputVec){
        return new Vec2(this.x - inputVec.x, this.y - inputVec.y);
    }

    /**
        Get opposite vector
     **/
    public Vec2 negate(){
        return new Vec2(-this.x, -this.y);
    }

    /**
        Get scalar product of vector and given vector
     **/
    public float scalar(Vec2 inputVec){
        return this.x * inputVec.x + this.y * inputVec.y;
    }

    /**
        Get new vector with the given value multiplied to every component
     **/
    public Vec2 multScalar(float value){
        return new Vec2(this.x * value, this.y * value);
    }

    /**
        Get new vector through the cross product of the vector and the given vector
     **/
    public Vec2 cross(Vec2 inputVec){
        return new Vec2(
                this.y * inputVec.x - inputVec.y * this.x,
                this.x * inputVec.y - inputVec.x * this.y
        );
    }


    /**
     * Performs a linear interpolation between a and b by the lerp value where lerp = 0 returns a and lerp = 1 returns b.
     * @param a
     * @param b
     * @param lerp
     * @return
     */
    public static Vec2 lerp(Vec2 a, Vec2 b, float lerp){
        return new Vec2(
                MathEx.lerp(a.x, b.x, lerp),
                MathEx.lerp(a.y, b.y, lerp)
        );
    }

    /**
        Print values
     **/
    @Override
    public String toString(){
        return "( " + this.x + ", " + this.y + ")";
    }
}

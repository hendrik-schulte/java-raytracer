package utils;

public class MathEx {

    /**
     * Clamps the given value to the given minimum and maximum values.
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Returns true if the given value is within the the borders border1 and border2 (both inclusive).
     * @param border1
     * @param border2
     * @param value
     * @return
     */
    public static boolean isWithinValues(float border1, float border2, float value) {

        if (border1 < border2) {
            return value >= border1 && value <= border2;
        } else {
            return value >= border2 && value <= border1;
        }
    }

    /**
     * Performs a linear interpolation between a and b by the lerp value where lerp = 0 returns a and lerp = 1 returns b.
     * @param a
     * @param b
     * @param lerp
     * @return
     */
    public static float lerp(float a, float b, float lerp){
        float n = 1 - lerp;

        return n * a + lerp * b;
    }

    /**
     * Performs a linear interpolation between a and b by the lerp value where lerp = 0 returns a and lerp = 1 returns b.
     * @param a
     * @param b
     * @param lerp
     * @return
     */
    public static double lerp(double a, double b, float lerp){
        float n = 1 - lerp;

        return n * a + lerp * b;
    }
}

package utils;

public class MathEx {

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }


    public static boolean isWithinValues(float border1, float border2, float value) {

        if (border1 < border2) {
            return value >= border1 && value <= border2;
        } else {
            return value >= border2 && value <= border1;
        }
    }
}

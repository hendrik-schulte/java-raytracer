package utils.io;


public class Log {
    public static void print(Object classObj, String message){
        System.out.println((char)27 +  "[36m" + String.valueOf("Raytracer >> " + classObj.getClass().getSimpleName()) + " :: " + message);
    }

    public static void warn(Object classObj, String message){
        System.out.println((char)27 +  "[33m" + String.valueOf("Raytracer >> " + classObj.getClass().getSimpleName()) + " :: " + message);
    }

    public static void error(Object classObj, String message){
        System.out.println((char)27 +  "[31m" + String.valueOf("Raytracer >> " + classObj.getClass().getSimpleName()) + " :: " + message);
    }
}

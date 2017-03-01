// ************************************************************ //
//                      Hochschule Duesseldorf                  //
//                                                              //
//                     Vertiefung Computergrafik                //
// ************************************************************ //


/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    1. Documentation:    Did you comment your code shortly but clearly?
    2. Structure:        Did you clean up your code and put everything into the right bucket?
    3. Performance:      Are all loops and everything inside really necessary?
    4. Theory:           Are you going the right way?

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 <<< YOUR TEAM NAME >>>

     Master of Documentation:
     Master of Structure:
     Master of Performance:
     Master of Theory:

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

import raytracer.Raytracer;
import ui.Window;
import scene.Scene;

// Main application class. This is the routine called by the JVM to run the program.
public class Main {

    /** BOX_DIMENSION **/

    static int IMAGE_WIDTH = 800;
    static int IMAGE_HEIGHT = 600;

    /** RAYTRACER **/

    static int RECURSIONS = 1;

    /**
        Initial method. This is where the show begins.
     **/
    public static void main(String[] args){
        long tStart = System.currentTimeMillis();

        Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

        draw(renderWindow);

        renderWindow.exportRendering(String.valueOf(stopTime(tStart)), RECURSIONS, 0);
    }

    /**
        Draw the scene that was set up
     **/
    private static void draw(Window renderWindow){
        Scene renderScene = new Scene();

        raytraceScene(renderWindow, renderScene);
    }

    /**
        Raytrace through the scene
     **/
    private static void raytraceScene(Window renderWindow, Scene renderScene){
        Raytracer raytracer = new Raytracer(renderScene, renderWindow, 0);

        raytracer.renderScene();
    }

    /**
        Stop the time for debug only
     **/
    private static double stopTime(long tStart){
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        return tDelta / 1000.0;
    }
}
/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    1. Send primary ray
    2. intersection test with all shapes
    3. if hit:
    3a: send secondary ray to the light source
    3b: 2
        3b.i: if hit:
            - Shape is in the shade
            - Pixel color = ambient value
        3b.ii: in NO hit:
            - calculate local illumination
    4. if NO hit:
        - set background color

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package raytracer;

import scene.Scene;

import ui.Window;
import utils.*;
import utils.algebra.Vec2;
import utils.io.DataExporter;
import utils.io.Log;

import java.awt.image.BufferedImage;

public class Raytracer {

    private BufferedImage mBufferedImage;
    private Scene mScene;
    private Window mRenderWindow;

    private int mMaxRecursions;

    public Raytracer(Scene scene, Window renderWindow, int recursions){
        Log.print(this, "Init");
        mMaxRecursions = recursions;
        mBufferedImage = renderWindow.getBufferedImage();

        mScene = scene;
        mRenderWindow = renderWindow;
    }

    public void renderScene(){
        Log.print(this, "Start rendering");

        RgbColor boringBackground = RgbColor.DARK_GRAY;

        // Rows
        for (int y = 0; y < mBufferedImage.getHeight(); y++) {
            // Columns
            for (int x = 0; x < mBufferedImage.getWidth(); x++) {

                    mRenderWindow.setPixel(mBufferedImage, boringBackground, new Vec2(x,y));
            }
        }
    }
}

package raytracer;


import utils.Callback;
import utils.io.Log;

public class RenderThread extends Thread {

    private Raytracer raytracer;
    private int startX;
    private int endX;
    private int startY;
    private int endY;
    private Callback callback;

    public RenderThread(Raytracer raytracer, int startX, int endX, int startY, int endY, Callback callback) {
        this.raytracer = raytracer;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.callback = callback;
    }

    public void run() {
        Log.print(this, "Render thread x: (" + startX + "-" + endX + ") y: (" + startY + "-" + endY + ") started");

        raytracer.renderBlock(startX, endX, startY, endY);

        Log.print(this, "Render thread x: (" + startX + "-" + endX + ") y: (" + startY + "-" + endY + ") finished");

        callback.callback();
    }
}
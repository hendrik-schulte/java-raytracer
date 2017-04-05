package raytracer;

import com.sun.org.apache.regexp.internal.RE;
import utils.Callback;
import utils.io.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderThread extends Thread {

    public enum ThreadOption {
        dynamic,
        fixed
    }

    private ThreadOption threadOption;

    private Raytracer raytracer;
    private RenderBlock renderBlock;
    private Callback callback;

    private List<RenderBlock> renderBlocks;

    public RenderThread(Raytracer raytracer, List<RenderBlock> renderBlocks, Callback callback) {
        this.raytracer = raytracer;
        this.renderBlocks = renderBlocks;
        this.callback = callback;

        threadOption = ThreadOption.dynamic;
    }

    public RenderThread(Raytracer raytracer, RenderBlock renderBlock, Callback callback) {
        this.raytracer = raytracer;
        this.renderBlock = renderBlock;
        this.callback = callback;

        threadOption = ThreadOption.fixed;
    }


    public void run() {
//        Log.print(this, "Render thread x: (" + startX + "-" + endX + ") y: (" + startY + "-" + endY + ") started");

        switch (threadOption) {
            case fixed:
                raytracer.renderBlock(renderBlock);
                break;

            case dynamic:
                while (!renderBlocks.isEmpty()) {
                    List<RenderBlock> saveList = Collections.synchronizedList(renderBlocks);

                    RenderBlock block;

//                    synchronized (saveList){
                        block = saveList.remove(0);
//                    }

                    raytracer.renderBlock(block);

//                    Log.print(this, "finished block " + renderBlocks.size() + " remaining");
                }
                break;
        }

//        Log.print(this, "Render thread x: (" + startX + "-" + endX + ") y: (" + startY + "-" + endY + ") finished");

        callback.callback();
    }
}
package utils;

import utils.algebra.Vec2;
import utils.io.Log;

import java.util.ArrayList;

public class AntiAliasing {

    public enum RenderStage {
        disabled,
        PreRendering,
        Adaptive
    }

    public enum Level {
        disabled,
        x4,
        x8,
        x16
    }

    public final float colorThreshold = (float) Math.pow(0.04f, 2);

    private ArrayList<Vec2> antiAliasingPositions;
    private Level level;
    private boolean adaptive;
    private RenderStage renderStage;
    private boolean[][] aaNecessary;
    private RgbColor[][] preRendering;
    private int imageWidth;

    public AntiAliasing(Level level, boolean adaptive, int imageWidth, int imageHeight) {
        this.level = level;
        this.adaptive = adaptive;
        this.imageWidth = imageWidth;
        if (!adaptive) renderStage = RenderStage.disabled;

        if (!isEnabledAndAdaptive()) return;

        renderStage = RenderStage.PreRendering;
        aaNecessary = new boolean[imageWidth][imageHeight];
        preRendering = new RgbColor[imageWidth][imageHeight];
    }

    public void Init() {
        generateAADistribution();
    }

    public void FinishPreRendering() {
        checkForAAEdges();

        Log.print(this, "Finished adaptive AA calculation.");

        renderStage = RenderStage.Adaptive;
    }


    private void checkForAAEdges() {
        for (int x = 1; x < aaNecessary.length - 1; x++) {
            // Columns
            for (int y = 1; y < aaNecessary[x].length - 1; y++) {
                CheckNeighbors(x, y);
            }
        }
    }

    /**
     * Checks for edges within the pre rendering. When an pixel on an edge is found, the representing flag in the
     * aaNecessary array is set.
     *
     * @param x
     * @param y
     */
    private void CheckNeighbors(int x, int y) {
        RgbColor c = preRendering[x][y];

        if (ColorDifference(c, preRendering[x - 1][y]) > colorThreshold) aaNecessary[x][y] = true;
        else if (ColorDifference(c, preRendering[x + 1][y]) > colorThreshold) aaNecessary[x][y] = true;
        else if (ColorDifference(c, preRendering[x][y + 1]) > colorThreshold) aaNecessary[x][y] = true;
        else if (ColorDifference(c, preRendering[x][y - 1]) > colorThreshold) aaNecessary[x][y] = true;
    }

    /**
     * Returns the squared distance between two color vectors.
     *
     * @param c1
     * @param c2
     * @return
     */
    private float ColorDifference(RgbColor c1, RgbColor c2) {
        return c1.colors.distanceSquared(c2.colors);
    }

    /**
     * Frees the memory allocatedfor adaptive anti aliasing.
     */
    public void cleanup() {
        aaNecessary = null;
        preRendering = null;
    }

    private void generateAADistribution() {
        if (!isEnabled()) return;

        antiAliasingPositions = new ArrayList<>();

        float pixelSizeNormPos = 2 / ((float) imageWidth); //relative pixel size relative to resolution

        switch (level) {
            case x4:
                float q4 = pixelSizeNormPos / 4;

                antiAliasingPositions.add(new Vec2(-q4, -q4));
                antiAliasingPositions.add(new Vec2(-q4, q4));
                antiAliasingPositions.add(new Vec2(q4, -q4));
                antiAliasingPositions.add(new Vec2(q4, q4));
                break;

            case x8:
                float q8 = pixelSizeNormPos / 8;

                //upper left corner
                antiAliasingPositions.add(new Vec2(-3 * q8, q8));
                antiAliasingPositions.add(new Vec2(-q8, 3 * q8));
                //bottom left corner
                antiAliasingPositions.add(new Vec2(-3 * q8, -q8));
                antiAliasingPositions.add(new Vec2(-q8, -3 * q8));
                //upper right corner
                antiAliasingPositions.add(new Vec2(3 * q8, q8));
                antiAliasingPositions.add(new Vec2(q8, 3 * q8));
                //bottom right
                antiAliasingPositions.add(new Vec2(3 * q8, -q8));
                antiAliasingPositions.add(new Vec2(q8, -3 * q8));
                break;

            case x16:
                q8 = pixelSizeNormPos / 8;

                //upper left corner
                antiAliasingPositions.add(new Vec2(-q8, q8));
                antiAliasingPositions.add(new Vec2(-3 * q8, 3 * q8));
                antiAliasingPositions.add(new Vec2(-3 * q8, q8));
                antiAliasingPositions.add(new Vec2(-q8, 3 * q8));
                //bottom left corner
                antiAliasingPositions.add(new Vec2(-q8, -q8));
                antiAliasingPositions.add(new Vec2(-3 * q8, -3 * q8));
                antiAliasingPositions.add(new Vec2(-3 * q8, -q8));
                antiAliasingPositions.add(new Vec2(-q8, -3 * q8));
                //upper right corner
                antiAliasingPositions.add(new Vec2(q8, q8));
                antiAliasingPositions.add(new Vec2(3 * q8, 3 * q8));
                antiAliasingPositions.add(new Vec2(3 * q8, q8));
                antiAliasingPositions.add(new Vec2(q8, 3 * q8));
                //bottom right
                antiAliasingPositions.add(new Vec2(q8, -q8));
                antiAliasingPositions.add(new Vec2(3 * q8, -3 * q8));
                antiAliasingPositions.add(new Vec2(3 * q8, -q8));
                antiAliasingPositions.add(new Vec2(q8, -3 * q8));
                break;
        }
    }

    /**
     * Returns the percentage of multisampled pixels that were saved by adaptve anti aliasing technique.
     *
     * @return
     */
    public float calcAdaptivePerformance() {
        if (!adaptive) return 100;

        int saved = 0, multisampled = 0;

        for (int x = 0; x < aaNecessary.length; x++) {
            // Columns
            for (int y = 0; y < aaNecessary[x].length; y++) {
                if (aaNecessary[x][y]) multisampled++;
                else saved++;
            }
        }

        return (saved / ((float) saved + multisampled)) * 100;
    }

    @Override
    public String toString() {
        if (level == Level.disabled) return "AA: disabled";

        if (adaptive) return "AA: adaptive x" + getSamplingNumber();
        else return "AA: x" + getSamplingNumber();
    }

    /**
     * Returns the multisampling facrtor.
     *
     * @return
     */
    public int getSamplingNumber() {

        switch (level) {
            case disabled:
                return 0;
            case x4:
                return 4;
            case x8:
                return 8;
            case x16:
                return 16;
        }
        return -1;
    }

    public Level getLevel() {
        return level;
    }

    public RenderStage getRenderStage() {
        return renderStage;
    }

    public boolean isAdaptive() {
        return adaptive;
    }

    public boolean isEnabled() {
        return level != Level.disabled;
    }

    public boolean isEnabledAndAdaptive() {
        return isEnabled() && adaptive;
    }

    public ArrayList<Vec2> getAntiAliasingPositions() {
        return antiAliasingPositions;
    }

    public boolean aaIsNecessary(int x, int y) {
        return aaNecessary[x][y];
    }

    public void savePreRendering(int x, int y, RgbColor color) {
        preRendering[x][y] = color;
    }

    public boolean isInPreRenderingStage() {
        return renderStage == AntiAliasing.RenderStage.PreRendering;
    }
}

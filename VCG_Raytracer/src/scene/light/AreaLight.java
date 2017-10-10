package scene.light;

import scene.shape.Shape;
import utils.MathEx;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A multisampled light for soft shadows.
 */
public abstract class AreaLight {

    protected float sample;
    protected ArrayList<Light> sampleLights = new ArrayList<>();
    protected int sampleAmount;
    protected float individualIntensity;

    /**
     * Creates an multisampled are light.
     *
     * @param resolution number of light sample points.
     * @param intensity  total intensity of all light sources accumulated.
     * @param sample     fraction of Lights sampled when evaluating light.
     */
    AreaLight(int resolution, float intensity, float sample) {
//        Log.print(this, "Area Light const");

        this.sample = MathEx.clamp(sample, 0, 1);
        individualIntensity = getIndividualIntensity(resolution, intensity);
    }

    protected float getIndividualIntensity(int amount, float intensity) {
        return intensity / (amount * sample);
    }

    protected Vec2 getNormalizedPosition(Vec2 resolution, float x, float y) {
        return new Vec2(2 * ((x + 0.5f) / resolution.x) - 1,
                2 * ((y + 0.5f) / resolution.y) - 1);
    }

    protected Vec3 norm2WorldRect(Vec3 center, Vec3 ortho1, Vec3 ortho2, Vec2 normPos, float rectWidth, float rectHeight) {

        return center
                .add(ortho1.multScalar(normPos.x * rectWidth / 2f))
                .add(ortho2.multScalar(normPos.y * rectHeight / 2f));
    }

    public ArrayList<Light> getLights() {

        if (sampleAmount == 0) return new ArrayList<>();

        ArrayList<Light> lights = new ArrayList<>(sampleLights);

        Collections.shuffle(lights);

        return new ArrayList<>(lights.subList(0, sampleAmount));
    }

    public abstract Shape getShape();
}

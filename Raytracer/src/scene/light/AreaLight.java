package scene.light;

import scene.SceneObject;
import scene.shape.Shape;
import utils.MathEx;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A multi-sampled light for soft shadows.
 */
public abstract class AreaLight {

    protected final float sample;
    protected final ArrayList<Light> sampleLights = new ArrayList<>();
    protected int sampleAmount;
    protected final float individualIntensity;

    /**
     * Creates an multi-sampled light.
     *
     * @param resolution number of light sample points.
     * @param intensity  total intensity of all light sources accumulated.
     * @param sample     fraction of Lights sampled when evaluating light.
     */
    AreaLight(int resolution, float intensity, float sample) {

        this.sample = MathEx.clamp(sample, 0, 1);
        individualIntensity = getIndividualIntensity(resolution, intensity);
    }

    protected float getIndividualIntensity(int amount, float intensity) {
        return intensity / (((float) amount) * sample);
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

    public abstract SceneObject getShape();
}

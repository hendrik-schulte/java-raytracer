package scene.light;

import scene.shape.Shape;
import scene.shape.Sphere;
import utils.RgbColor;
import utils.algebra.Vec3;

public class VolumeLight extends AreaLight {

    private Sphere sphere;

    public VolumeLight(RgbColor color, float intensity, Sphere sphere, float scale, int resolution, float sample) {
        super(resolution, intensity, sample);

        this.sphere = sphere;

        for (int x = 0; x < resolution; x++) {

            Vec3 position = sphere.getPosition().add(Vec3.Random().multScalar(scale * sphere.getRadius()));

            sampleLights.add(new Light(position, color, individualIntensity));
        }

        sampleAmount = (int) (sampleLights.size() * sample);
    }

    @Override
    public Shape getShape() {
        return sphere;
    }
}
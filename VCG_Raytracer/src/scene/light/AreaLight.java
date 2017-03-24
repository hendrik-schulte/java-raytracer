package scene.light;

import scene.shape.Rectangle;
import utils.MathEx;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AreaLight {

    private float sample;
    private Rectangle rect;
    private ArrayList<Light> sampleLights = new ArrayList<>();

    public AreaLight(RgbColor color, float intensity, Rectangle rect, float planeOffset, Vec2 resolution, float sample) {

        this.rect = rect;
        this.sample = MathEx.clamp(sample, 0, 1);

        float individualIntensity = intensity / (resolution.x * resolution.y * sample);
        Vec3 center = rect.getPosition().add(rect.getNormal().multScalar(planeOffset));
        float width = rect.a.length() * 2;
        float height = rect.b.length() * 2;

        for (int x = 0; x < resolution.x; x++) {
            for (int y = 0; y < resolution.y; y++) {
                Vec2 normPos = getNormalizedPosition(resolution, x, y);
                Vec3 position = norm2World(center, normPos, width, height);

                sampleLights.add(new Light(position, color, individualIntensity));
            }
        }
    }

    private Vec2 getNormalizedPosition(Vec2 resolution, float x, float y) {
        return new Vec2(2 * ((x + 0.5f) / resolution.x) - 1,
                2 * ((y + 0.5f) / resolution.y) - 1);
    }

    private Vec3 norm2World(Vec3 center, Vec2 normPos, float rectWidth, float rectHeight) {

        return center
                .add(rect.a.multScalar(normPos.x * rectWidth / 2f))
                .add(rect.b.multScalar(normPos.y * rectHeight / 2f));
    }

    public ArrayList<Light> getLights() {

        ArrayList<Light> lights = new ArrayList<>(sampleLights);

        int sampleAmount = (int) (lights.size() * sample);

        if(sampleAmount == 0) return new ArrayList<>();

//        Random random = new Random();
//
//        while(lights.size() > sampleAmount){
//            lights.remove(random.nextInt(lights.size()));
//        }

        Collections.shuffle(lights);

        return new ArrayList<>(lights.subList(0, sampleAmount));
    }

    public Rectangle getRectangle() {
        return rect;
    }
}

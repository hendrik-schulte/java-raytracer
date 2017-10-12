package scene.light;

import scene.shape.Rectangle;
import scene.shape.Shape;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class RectLight extends AreaLight {

    private final Rectangle rect;

    public RectLight(RgbColor color, float intensity, Rectangle rect, float planeOffset, float scale, Vec2 resolution, float sample) {
        super((int) (resolution.x * resolution.y), intensity, sample);

        this.rect = rect;

        Vec3 center = rect.getPosition().add(rect.getNormal().multScalar(planeOffset));
        float width = rect.a.length() * 2 * scale;
        float height = rect.b.length() * 2 * scale;

        for (int x = 0; x < resolution.x; x++) {
            for (int y = 0; y < resolution.y; y++) {
                Vec2 normPos = getNormalizedPosition(resolution, x, y);
                Vec3 position = norm2WorldRect(center, rect.a, rect.b, normPos, width, height);

                sampleLights.add(new Light(position, color, individualIntensity));
            }
        }

        sampleAmount = (int) (sampleLights.size() * sample);
    }

    @Override
    public Shape getShape() {
        return rect;
    }
}

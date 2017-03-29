package scene.light;

import scene.shape.Circle;
import scene.shape.Rectangle;
import scene.shape.Shape;
import utils.MathEx;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.Log;

import java.util.ArrayList;
import java.util.Collections;

public class AreaLight {

    private float sample;
    private Rectangle rect;
    private Circle circle;
    private ArrayList<Light> sampleLights = new ArrayList<>();

    private int sampleAmount;

    public AreaLight(RgbColor color, float intensity, Rectangle rect, float planeOffset, float scale, Vec2 resolution, float sample) {

        this.rect = rect;
        this.sample = MathEx.clamp(sample, 0, 1);

        float individualIntensity = getIndividualIntensity(resolution, intensity);
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

    public AreaLight(RgbColor color, float intensity, Circle circle, float planeOffset, float scale, Vec2 resolution, float sample) {

        this.circle = circle;
        this.sample = MathEx.clamp(sample, 0, 1);

        float individualIntensity = getIndividualIntensity(resolution, intensity);
        Vec3 center = circle.getPosition().add(circle.getNormal().multScalar(planeOffset));
        float diameter = circle.radius * 2 * scale;

//        Log.print(this, "indIntens: " + individualIntensity);

        Vec3 ortho1 = circle.getNormal().getOrthogonal();
        Vec3 ortho2 = circle.getNormal().cross(ortho1).normalize();

//        Log.print(this, "normal: " + circle.getNormal());
//        Log.print(this, "ortho1: " + ortho1);
//        Log.print(this, "ortho2: " + ortho2);


        //radius
        for (int y = 1; y <= resolution.y; y++) {

            float radius = (float) Math.sqrt(y / resolution.y);

            //angle
            for (int x = 0; x < resolution.x; x++) {

                float angle = (float) (x * Math.PI * 2 / resolution.x);

//                if (y == 1) Log.print(this, "angle: " + angle);

                Vec2 normPos = new Vec2(radius * (float) Math.cos(angle), radius * (float) Math.sin(angle));

//                Log.print(this, "radius: " + radius);
//                Log.print(this, "light norm pos: " + normPos);

                Vec3 position = norm2WorldRect(center, ortho1, ortho2, normPos, diameter, diameter);

                sampleLights.add(new Light(position, color, individualIntensity));
            }
        }

        sampleLights.add(new Light(center, color, individualIntensity));

        sampleAmount = (int) (sampleLights.size() * sample);
    }

    private float getIndividualIntensity(Vec2 resolution, float intensity) {
        return intensity / ((resolution.x * resolution.y + 1) * sample);
    }

    private Vec2 getNormalizedPosition(Vec2 resolution, float x, float y) {
        return new Vec2(2 * ((x + 0.5f) / resolution.x) - 1,
                2 * ((y + 0.5f) / resolution.y) - 1);
    }

    private Vec3 norm2WorldRect(Vec3 center, Vec3 ortho1, Vec3 ortho2, Vec2 normPos, float rectWidth, float rectHeight) {

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

    public Shape getShape() {

        if (circle != null) return circle;
        else return rect;
    }
}

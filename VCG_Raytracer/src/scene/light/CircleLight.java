package scene.light;

import scene.shape.Circle;
import scene.shape.Shape;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class CircleLight extends AreaLight {

    private Circle circle;

    public CircleLight(RgbColor color, float intensity, Circle circle, float planeOffset, float scale, Vec2 resolution, float sample) {
        super((int) (resolution.x * resolution.y + 1), intensity, sample);

        this.circle = circle;

        Vec3 center = circle.getPosition().add(circle.getNormal().multScalar(planeOffset));
        float diameter = circle.radius * 2 * scale;

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

    @Override
    public Shape getShape() {
        return circle;
    }
}

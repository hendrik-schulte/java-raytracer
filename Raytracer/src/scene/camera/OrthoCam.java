package scene.camera;

import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class OrthoCam extends Camera {

    public OrthoCam(Vec3 pos, Vec3 lookAt, Vec3 upVector, float width, int imageWidth, int imageHeight) {
        super(pos,lookAt, upVector, imageWidth, imageHeight);

        windowWidth = width;
        windowHeight = windowWidth / ratio;
        windowCenter = getWorldPosition();

//        Log.print(this, "windowWidth: " + windowWidth);
//        Log.print(this, "windowHeight: " + windowHeight);
    }

    @Override
    public Ray calcPixelRay(Vec2 normPos) {

        return new Ray(norm2World(normPos), view);
    }
}

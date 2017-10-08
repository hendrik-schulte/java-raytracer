package scene.camera;

import raytracer.Ray;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class PerspCam extends Camera {

    public final float viewAngle;
    public final float focalLength;

    public PerspCam(Vec3 pos, Vec3 lookAt, Vec3 upVector, float viewAngle, float focalLength, int imageWidth, int imageHeight) {
        super(pos, lookAt, upVector, imageWidth, imageHeight);

        this.viewAngle = viewAngle;
        this.focalLength = focalLength;

        calcViewProperties();
    }

    protected void calcViewProperties() {

        windowHeight = (float) (2 * focalLength * Math.tan(viewAngle * Math.PI / 360));
        windowWidth = ratio * windowHeight;
        windowCenter = getPosition().add(view.multScalar(focalLength));

//        Log.print(this, "windowWidth: " + windowWidth);
//        Log.print(this, "windowHeight: " + windowHeight);
    }

    @Override
    public Ray calcPixelRay(Vec2 normPos) {

        return new Ray(mPosition, norm2World(normPos).sub(mPosition));
    }
}

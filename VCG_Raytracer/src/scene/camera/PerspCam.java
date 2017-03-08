package scene.camera;

import utils.algebra.Vec3;

public class PerspCam extends Camera {

    public PerspCam(Vec3 pos, Vec3 lookAt, Vec3 upVector, float viewAngle, float focalLength) {
        super(pos, lookAt, upVector, viewAngle, focalLength);
    }
}

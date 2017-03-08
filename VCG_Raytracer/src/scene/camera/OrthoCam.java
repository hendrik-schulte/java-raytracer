package scene.camera;

import utils.algebra.Vec3;

public class OrthoCam extends Camera {

    public OrthoCam(Vec3 pos, Vec3 lookAt, Vec3 upVector) {
        super(pos,lookAt, upVector, 0, 0);
    }
}

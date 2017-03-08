package scene.camera;

import scene.SceneObject;
import utils.algebra.Vec3;
import utils.io.Log;

public class Camera extends SceneObject {

    public Vec3 lookAt;
    public Vec3 upVector;
    public float viewAngle;
    public float focalLength;

    public Vec3 view;
    public Vec3 side;
    public Vec3 camUp;

    public Camera(Vec3 pos, Vec3 lookAt, Vec3 upVector, float viewAngle, float focalLength) {
        super(pos);

        this.lookAt = lookAt;
        this.upVector = upVector;
        this.viewAngle = viewAngle;
        this.focalLength = focalLength;

        calcCoordinates();
    }

    public Vec3 calculateDestinationPoint(){
        return new Vec3();
    }

    private void calcCoordinates(){
        view = calcView();
        side = calcSide();
        camUp = calcCamUp();

        Log.print(this, "camera coordinates: \nview: " + view + "\nside: " + side + "\nup: " + camUp);
    }

    private Vec3 calcView(){
        return lookAt.sub(mPosition).normalize();
    }

    private Vec3 calcSide(){
        return view.cross(upVector).normalize();
    }

    private Vec3 calcCamUp(){
        return side.cross(view).normalize();
    }
}

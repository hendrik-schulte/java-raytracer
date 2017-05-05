package scene.camera;

import raytracer.Ray;
import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public abstract class Camera extends SceneObject {

    public Vec3 lookAt;
    public Vec3 upVector;

    public Vec3 view;
    public Vec3 side;
    public Vec3 camUp;

    public float ratio;
    public float windowHeight;
    public float windowWidth;
    public Vec3 windowCenter;


    public Camera(Vec3 pos, Vec3 lookAt, Vec3 upVector, int imageWidth, int imageHeight) {
        super(pos);

        this.lookAt = lookAt;
        this.upVector = upVector.normalize();


        calcCoordinateAxis();

        ratio = (float) imageWidth / (float) imageHeight;
    }

    /***
     * Converts the given normalised pixel position (from [-1,-1] to [1,1]) to World space position.
     *
     * @param normPos
     * @return
     */
    protected Vec3 norm2World(Vec2 normPos) {
        return windowCenter
                .add(camUp.multScalar(normPos.y * windowHeight / -2f))
                .add(side.multScalar(normPos.x * windowWidth / 2f));
    }

    public abstract Ray calcPixelRay(Vec2 normPos);

    private void calcCoordinateAxis(){
        view = calcView();
        side = calcSide();
        camUp = calcCamUp();

//        Log.print(this, "camera coordinates: \nview: " + view + "\nside: " + side + "\nup:   " + camUp);
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

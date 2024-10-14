package wtf.bhopper.nonsense.event.impl;

public class EventPostMotion {

    public final double x, y, z;
    public final float yaw, pitch;
    public final boolean onGround;

    public EventPostMotion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }


}

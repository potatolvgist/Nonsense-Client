package wtf.bhopper.nonsense.event.impl;

public class EventPreMotion {

    public double x, y, z;
    public float yaw, pitch;
    public boolean onGround, sprinting, sneaking;

    public EventPreMotion(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean sprinting, boolean sneaking) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.sprinting = sprinting;
        this.sneaking = sneaking;
    }


}

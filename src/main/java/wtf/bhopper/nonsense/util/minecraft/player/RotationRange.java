package wtf.bhopper.nonsense.util.minecraft.player;

public class RotationRange {

    public float yaw;
    public float pitch;
    public float range;

    public RotationRange(float yaw, float pitch, float range) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.range = range;
    }

    public RotationRange(Rotation rotation, float range) {
        this.yaw = rotation.yaw;
        this.pitch = rotation.pitch;
        this.range = range;
    }


}

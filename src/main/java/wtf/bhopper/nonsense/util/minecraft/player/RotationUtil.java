package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.util.minecraft.MathUtil;

public class RotationUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float serverYaw = 0.0F;
    public static float serverPitch = 0.0F;

    public static void updateServerRotations(float yaw, float pitch) {
        serverYaw = mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYawHead = yaw;
        serverPitch = mc.thePlayer.rotationPitchHead = pitch;
    }

    public static Rotation getRotations(Entity entity, double posX, double posY, double posZ) {
        double x = posX - entity.posX;
        double y = posY - (entity.posY + (double)entity.getEyeHeight());
        double z = posZ - entity.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new Rotation(yaw, pitch);
    }

    public static Rotation getRotations(double posX, double posY, double posZ) {
        return getRotations(mc.thePlayer, posX, posY, posZ);
    }

    public static Rotation getRotations(Vec3 vec) {
        return getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static Rotation getRotations(BlockPos blockPos) {
        return getRotations(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    public static Rotation getRotations(BlockPos blockPos, EnumFacing facing) {
        return getRotations(getHitVec(blockPos, facing));
    }

    public static Rotation getRotations(Entity entity) {
        return getRotations(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }

    public static Rotation getRotationsOptimized(Entity entity) {
        double eyeX = mc.thePlayer.posX;
        double eyeY = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight();
        double eyeZ = mc.thePlayer.posZ;
        return getRotations(MathUtil.closestPoint(entity.getEntityBoundingBox(), eyeX, eyeY, eyeZ));
    }

    public static Vec3 getHitVec(BlockPos blockPos, EnumFacing facing) {
        return new Vec3(blockPos)
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3(
                        facing.getDirectionVec().getX() * 0.5,
                        facing.getDirectionVec().getY() * 0.5,
                        facing.getDirectionVec().getZ() * 0.5)
                );
    }

}

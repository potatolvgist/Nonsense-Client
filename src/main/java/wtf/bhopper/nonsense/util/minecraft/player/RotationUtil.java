package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.Set;

public class RotationUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float serverYaw = 0.0F;
    public static float serverPitch = 0.0F;
    public static float prevServerYaw = 0.0F;
    public static float prevServerPitch = 0.0F;

    public static void updateServerRotations(float yaw, float pitch) {
        prevServerYaw = serverYaw;
        prevServerPitch = serverPitch;
        serverYaw = mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYawHead = yaw;
        serverPitch = mc.thePlayer.rotationPitchHead = pitch;
    }

    public static Rotation getRotations(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
        double x = rotX - startX;
        double y = rotY - startY;
        double z = rotZ - startZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new Rotation(yaw, pitch, new Vec3(rotX, rotY, rotZ));
    }

    public static Rotation getRotations(double posX, double posY, double posZ) {
        return getRotations(posX, posY, posZ, mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
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
    public static Rotation getRotations(Entity target, Entity entity) {
        return getRotations(target.posX, target.posY + target.getEyeHeight(), target.posZ, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }

    public static Rotation getRotationsOptimized(AxisAlignedBB boundingBox) {
        double eyeX = mc.thePlayer.posX;
        double eyeY = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight();
        double eyeZ = mc.thePlayer.posZ;
        return getRotations(MathUtil.closestPoint(boundingBox, eyeX, eyeY, eyeZ));
    }

    public static Rotation getRotationsRandomOptimized(AxisAlignedBB boundingBox, float maxRange) {
        Vec3 eyes = PlayerUtil.eyesPos();
        Vec3 point = MathUtil.randomPoint(boundingBox, eyes.xCoord, eyes.yCoord, eyes.zCoord, maxRange);
        if (point == null) {
            return getRotationsOptimized(boundingBox);
        }
        return getRotations(point);
    }

    public static Rotation getRotationsRandomOptimizedQuick(AxisAlignedBB boundingBox, double splitFactor, float maxRange) {
        Set<Vec3> hitVecs = MathUtil.samplePointsOnSurface(boundingBox, splitFactor);
        hitVecs.removeIf(vec3 -> vec3.distanceTo(PlayerUtil.eyesPos()) > maxRange);
        if (hitVecs.isEmpty()) {
            return getRotationsOptimized(boundingBox);
        }
        Vec3 hitVec = GeneralUtil.randomElement(hitVecs);
        return getRotations(hitVec);
    }

    public static float rayCastRange(Vec3 pos, AxisAlignedBB boundingBox) {
        Vec3 closestPoint = MathUtil.closestPoint(boundingBox, pos);
        return (float)pos.distanceTo(closestPoint);
    }

    public static float rayCastRange(AxisAlignedBB boundingBox) {
        return rayCastRange(PlayerUtil.eyesPos(), boundingBox);
    }

    public static Vec3 getRotationVec(Entity entity, float delta) {
        return getRotationVec(entity.rotationYaw, entity.rotationPitch, entity.prevRotationYaw, entity.prevRotationPitch, delta);
    }

    public static Vec3 getRotationVec(float yaw, float pitch, float prevYaw, float prevPitch, float delta) {
        if (delta == 1.0F) {
            return getRotationVec(yaw, pitch);
        }

        float fixedYaw = MathUtil.lerp(prevYaw, yaw, delta);
        float fixedPitch = MathUtil.lerp(prevPitch, pitch, delta);
        return getRotationVec(fixedYaw, fixedPitch);
    }

    public static Vec3 getRotationVec(float yaw, float pitch) {
        float f = MathHelper.cos(-yaw * MathHelper.deg2Rad - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * MathHelper.deg2Rad - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * MathHelper.deg2Rad);
        float f3 = MathHelper.sin(-pitch * MathHelper.deg2Rad);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static Vec3 getHitVec(BlockPos blockPos, EnumFacing facing) {
        return new Vec3(blockPos)
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3(
                        facing.getDirectionVec().getX() * 0.5,
                        facing.getDirectionVec().getY() * 0.5,
                        facing.getDirectionVec().getZ() * 0.5
                ));
    }

    public static Vec3 getHitVecOptimized(BlockPos blockPos, EnumFacing facing) {
        Vec3 eyes = PlayerUtil.eyesPos();
        double x, y, z;

        return MathUtil.closestPointOnFace(new AxisAlignedBB(blockPos, blockPos.add(1, 1, 1)), facing, eyes);
    }

}

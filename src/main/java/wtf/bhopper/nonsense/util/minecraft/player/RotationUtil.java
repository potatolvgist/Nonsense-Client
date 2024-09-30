package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import wtf.bhopper.nonsense.util.Util;
import wtf.bhopper.nonsense.util.minecraft.MathUtil;

import java.util.Set;

public class RotationUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float serverYaw = 0.0F;
    public static float serverPitch = 0.0F;

    public static void updateServerRotations(float yaw, float pitch) {
        serverYaw = mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYawHead = yaw;
        serverPitch = mc.thePlayer.rotationPitchHead = pitch;
    }

    public static Rotation getRotations(double posX, double posY, double posZ) {
        double x = posX - mc.thePlayer.posX;
        double y = posY - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double z = posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new Rotation(yaw, pitch);
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
        Vec3 hitVec = Util.randomElement(hitVecs);
        return getRotations(hitVec);
    }

    public static float rayCastRange(AxisAlignedBB boundingBox) {
        Vec3 eyes = PlayerUtil.eyesPos();
        Vec3 closestPoint = MathUtil.closestPoint(boundingBox, eyes);
        return (float)eyes.distanceTo(closestPoint);
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

        switch (facing) {
            case DOWN:
                x = Math.max(blockPos.getX(), Math.min(eyes.xCoord, blockPos.getX() + 1));
                y = eyes.yCoord;
                z = Math.max(blockPos.getZ(), Math.min(eyes.xCoord, blockPos.getZ() + 1));
                break;

            case UP:
                x = Math.max(blockPos.getX(), Math.min(eyes.xCoord, blockPos.getX() + 1));
                y = eyes.yCoord + 1;
                z = Math.max(blockPos.getZ(), Math.min(eyes.xCoord, blockPos.getZ() + 1));
                break;

            case NORTH:
                x = Math.max(blockPos.getX(), Math.min(eyes.xCoord, blockPos.getX() + 1));
                y = Math.max(blockPos.getY(), Math.min(eyes.xCoord, blockPos.getY() + 1));
                z = eyes.zCoord;
                break;

            case SOUTH:
                x = Math.max(blockPos.getX(), Math.min(eyes.xCoord, blockPos.getX() + 1));
                y = Math.max(blockPos.getY(), Math.min(eyes.xCoord, blockPos.getY() + 1));
                z = eyes.zCoord + 1;
                break;

            case WEST:
                x = eyes.xCoord;
                y = Math.max(blockPos.getY(), Math.min(eyes.xCoord, blockPos.getY() + 1));
                z = Math.max(blockPos.getZ(), Math.min(eyes.xCoord, blockPos.getZ() + 1));
                break;

            case EAST:
                x = eyes.xCoord + 1;
                y = Math.max(blockPos.getY(), Math.min(eyes.xCoord, blockPos.getY() + 1));
                z = Math.max(blockPos.getZ(), Math.min(eyes.xCoord, blockPos.getZ() + 1));
                break;

            default:
                throw new IllegalArgumentException("Invalid face: " + facing);
        }

        return new Vec3(x, y, z);
    }

}

package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil implements MinecraftInstance {

    public static boolean canUpdate() {
        return mc != null && mc.thePlayer != null && mc.theWorld != null;
    }

    public static Vec3 eyesPos() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static void swing(boolean silent) {
        if (silent) {
            PacketUtil.send(new C0APacketAnimation());
        } else {
            mc.thePlayer.swingItem();
        }
    }

    public static boolean isBlockUnder(final double height) {
        return isBlockUnder(height, true);
    }

    public static boolean isBlockUnder(final double height, final boolean boundingBox) {
        if (boundingBox) {
            for (int offset = 0; offset < height; offset += 2) {
                final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }
        } else {
            for (int offset = 0; offset < height; offset++) {
                if (BlockUtil.getRelativeBlock(0, -offset, 0).isFullBlock()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBlockUnder() {
        return isBlockUnder(mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
    }

    public static boolean isOnSameTeam(final EntityPlayer player) {

        if (player == null) {
            return false;
        }

        if (player.getTeam() != null && mc.thePlayer.getTeam() != null) {
            final char c1 = player.getDisplayName().getFormattedText().charAt(1);
            final char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }

    public static PredictionResult predictProjectilePath() {
        return predictProjectilePath(1.0F);
    }

    public static PredictionResult predictProjectilePath(float delta) {

        ItemStack stack = mc.thePlayer.getHeldItem();
        if (stack == null) {
            return null;
        }

        double velocity;
        double gravity;

        Item item = stack.getItem();
        if (item instanceof ItemEnderPearl || item instanceof ItemEgg || item instanceof ItemSnowball) {
            velocity = 1.5;
            gravity = 0.03;
        } else if (item instanceof ItemPotion) {
            velocity = 0.5;
            gravity = 0.05;
        } else if (item instanceof ItemBow) {

            int bowUseDuration = item.getMaxItemUseDuration(stack) - mc.thePlayer.getItemInUseCount();
            float arrowVelocity = (float)bowUseDuration / 20.0F;
            arrowVelocity = (arrowVelocity * arrowVelocity + arrowVelocity * 2.0F) / 3.0F;

            if ((double)arrowVelocity < 0.1D) {
                return null;
            }

            if (arrowVelocity > 1.0F)
            {
                arrowVelocity = 1.0F;
            }

            velocity = arrowVelocity * 3.0;
            gravity = 0.05;

        } else {
            return null;
        }

        Vec3 eyes = mc.thePlayer.getPositionEyes(delta);
        float yaw = mc.thePlayer.prevRotationYaw + (mc.thePlayer.rotationYaw - mc.thePlayer.prevRotationYaw) * delta;
        float pitch = mc.thePlayer.prevRotationPitch + (mc.thePlayer.rotationPitch - mc.thePlayer.prevRotationPitch) * delta;

        return predictProjectilePath(eyes.xCoord, eyes.yCoord, eyes.zCoord, yaw, pitch, velocity, gravity);
    }

    public static PredictionResult predictProjectilePath(double x, double y, double z, float yaw, float pitch, double velocity, double gravity) {
        return predictProjectilePath(x, y, z, yaw, pitch, velocity, gravity, 100);
    }

    public static PredictionResult predictProjectilePath(double x, double y, double z, float yaw, float pitch, double velocity, double gravity, int maxPredictions) {

        x -= MathHelper.cos(yaw * MathHelper.deg2Rad) * 0.16;
        y -= 0.1;
        z -= MathHelper.sin(yaw * MathHelper.deg2Rad) * 0.16;

        double mx = -MathHelper.sin(yaw * MathHelper.deg2Rad) * MathHelper.cos(pitch * MathHelper.deg2Rad) * 0.4;
        double my = MathHelper.cos(yaw * MathHelper.deg2Rad) * MathHelper.cos(pitch * MathHelper.deg2Rad) * 0.4;
        double mz = -MathHelper.sin(pitch * MathHelper.deg2Rad) * 0.4;

        float distFactor = MathHelper.sqrt_double(mx * mx + my * my + mz * mz);
        mx = (mx / distFactor) * velocity;
        my = (my / distFactor) * velocity;
        mz = (mz / distFactor) * velocity;

        List<Vec3> points = new ArrayList<>();
        points.add(new Vec3(x, y, z));

        MovingObjectPosition rayCast = null;

        for (int i = 0; i < maxPredictions; i++) {

            Vec3 currentPos = new Vec3(x, y, z);
            Vec3 newPos = new Vec3(x + mx, y + my, z + mz);

            points.add(newPos);

            rayCast = mc.theWorld.rayTraceBlocks(currentPos, newPos);

            if (rayCast != null && rayCast.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
                break;
            }

            x += mx;
            y += my;
            z += mz;

            mx *= 0.99;
            my *= 0.99;
            mz *= 0.99;
            my -= gravity;

        }

        return new PredictionResult(points, new Vec3(x, y, z), rayCast);
    }

    public static boolean selfDamage(double value, final boolean groundCheck, final boolean hurtTimeCheck) {
        if (groundCheck && !mc.thePlayer.onGround) {
            return false;
        }

        if (hurtTimeCheck && mc.thePlayer.hurtTime > 0) {
            return false;
        }

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        double fallDistance = 3.1;
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            fallDistance += (float) (amplifier + 1);
        }

        int packetCount = (int)Math.ceil(fallDistance / value);
        for (int i = 0; i < packetCount; i++) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + value, z, false));
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
        }

        PacketUtil.sendNoEvent(new C03PacketPlayer(true));
        return true;
    }

    public static class PredictionResult {
        public final List<Vec3> path;
        public final Vec3 pos;
        public final MovingObjectPosition object;

        public PredictionResult(List<Vec3> path, Vec3 pos, MovingObjectPosition object) {
            this.path = path;
            this.pos = pos;
            this.object = object;
        }

    }


}

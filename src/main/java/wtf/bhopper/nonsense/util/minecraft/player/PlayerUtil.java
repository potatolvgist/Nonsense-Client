package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

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

    public static boolean isOnSameTeam(final EntityPlayer player) {
        if (player.getTeam() != null && mc.thePlayer.getTeam() != null) {
            final char c1 = player.getDisplayName().getFormattedText().charAt(1);
            final char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }

    public static List<Vec3> predictProjectilePath() {
        return predictProjectilePath(1.0F);
    }

    public static List<Vec3> predictProjectilePath(float delta) {

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

    public static List<Vec3> predictProjectilePath(double x, double y, double z, float yaw, float pitch, double velocity, double gravity) {
        return predictProjectilePath(x, y, z, yaw, pitch, velocity, gravity, 100);
    }

    public static List<Vec3> predictProjectilePath(double x, double y, double z, float yaw, float pitch, double velocity, double gravity, int maxPredictions) {

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

        for (int i = 0; i < maxPredictions; i++) {

            Vec3 currentPos = new Vec3(x, y, z);
            Vec3 newPos = new Vec3(x + mx, y + my, z + mz);

            points.add(newPos);

            MovingObjectPosition rayCast = mc.theWorld.rayTraceBlocks(currentPos, newPos);

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

        return points;
    }


}

package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventSlowDown;

public class MoveUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F;
    }

    public static double getMotion() {
        double x = mc.thePlayer.motionX;
        double z = mc.thePlayer.motionZ;
        return MathHelper.sqrt_double(x * x + z * z);
    }

    public static double getSpeed() {
        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        return MathHelper.sqrt_double(x * x + z * z);
    }

    public static double baseSpeed() {
        double baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * 2.873;
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            baseSpeed /= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

    public static void setSpeed(double speed) {
        setSpeed(null, speed);
    }

    public static void setSpeed(EventMove event, double speed) {
        setSpeed(event, speed, mc.thePlayer.rotationYaw, mc.thePlayer.moveForward, mc.thePlayer.moveStrafing);
    }

    public static void setSpeed(EventMove event, double speed, float yaw, double forward, double strafe) {

        if (mc.thePlayer.isUsingItem() && !mc.thePlayer.isRiding()) {
            EventSlowDown eventSlowDown = new EventSlowDown(0.2F);
            Nonsense.INSTANCE.eventBus.post(eventSlowDown);
            if (!eventSlowDown.isCancelled()) {
                speed *= eventSlowDown.factor;
            }
        }

        double motion = speed;

        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45.0F : 45.0F);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45.0F : -45.0F);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
        }

        double mx = Math.cos(Math.toRadians(yaw + 90.0F));
        double mz = Math.sin(Math.toRadians(yaw + 90.0F));
        mc.thePlayer.motionX = forward * motion * mx + strafe * motion * mz;
        mc.thePlayer.motionZ = forward * motion * mz - strafe * motion * mx;
        if (event != null) {
            event.x = mc.thePlayer.motionX;
            event.y = mc.thePlayer.motionY;
        }
    }

    public static void setVertical(EventMove event, double speed) {
        mc.thePlayer.motionY = speed;
        if (event != null) {
            event.y = speed;
        }
    }

    public static void setVertical(double speed) {
        setSpeed(null, speed);
    }

    public static void jump(EventMove event, double motion) {
        mc.thePlayer.jump(motion);
        event.y = motion;
    }

    public static double lastDistance() {
        double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;

        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }

}

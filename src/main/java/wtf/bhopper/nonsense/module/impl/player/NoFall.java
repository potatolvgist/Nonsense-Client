package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class NoFall extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.SPOOF, value -> this.timer.setDisplayed(value == Mode.PACKET));
    private final BooleanSetting timer = new BooleanSetting("Timer", "Helps bypass some anti-cheats", false);

    public NoFall() {
        super("No Fall", "Prevents fall damage", Category.PLAYER);
        this.addSettings(mode, timer);
    }

    private boolean timerSetback = false;

    @EventHandler
    public void onPreMotion(EventPreMotion event) {

        if (this.timerSetback) {
            mc.timer.timerSpeed = 1.0F;
            this.timerSetback = false;
        }

        switch (mode.get()) {
            case SPOOF:
                if (this.willTakeDamage()) {
                    event.onGround = true;
                    mc.thePlayer.fallDistance = 0.0F;
                }
                break;

            case PACKET:
                if (this.willTakeDamage()) {
                    PacketUtil.send(new C03PacketPlayer(true));
                    mc.thePlayer.fallDistance = 0.0F;

                    if (this.timer.get()) {
                        mc.timer.timerSpeed = 0.5F;
                        this.timerSetback = true;
                    }

                }
                break;

            case NO_GROUND:
                event.onGround = false;
                break;

            case VERUS:
                if (this.willTakeDamage()) {
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.fallDistance = 0.0F;
                    mc.thePlayer.motionX *= 0.6;
                    mc.thePlayer.motionZ *= 0.6;
                    event.onGround = true;
                }
                break;

            case MINIBLOX:
                Vec3 start = mc.thePlayer.getPositionEyes(1.0F);
                Vec3 end = start.subtract(0.0, start.yCoord, 0.0);
                MovingObjectPosition result = mc.theWorld.rayTraceBlocks(start, end, false, false, false);
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3.0 && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, result.hitVec.yCoord, mc.thePlayer.posZ, true));
                    mc.thePlayer.fallDistance = 0.0F;
                }
                break;
        }

    }

    private boolean willTakeDamage() {
        return mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3.0;
    }


    @Override
    public String getSuffix() {
        return mode.getDisplayValue();
    }

    enum Mode {
        SPOOF,
        PACKET,
        NO_GROUND,
        VERUS,
        MINIBLOX
    }

}

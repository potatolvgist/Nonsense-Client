package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class NoFall extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.SPOOF);

    public NoFall() {
        super("No Fall", "Prevents fall damage", Category.PLAYER);
        this.addSettings(mode);
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {

        switch (mode.get()) {
            case SPOOF:
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3.0) {
                    event.onGround = true;
                    mc.thePlayer.fallDistance = 0.0F;
                }
                break;

            case PACKET:
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3.0) {
                    PacketUtil.send(new C03PacketPlayer(true));
                    mc.thePlayer.fallDistance = 0.0F;
                }
                break;

            case NO_GROUND:
                event.onGround = false;
                break;

            case VERUS:
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3.0) {
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.fallDistance = 0.0F;
                    mc.thePlayer.motionX *= 0.6;
                    mc.thePlayer.motionZ *= 0.6;
                    event.onGround = true;
                }
                break;
        }

    }

    @Override
    public String getSuffix() {
        return mode.getDisplayValue();
    }

    enum Mode {
        SPOOF,
        PACKET,
        NO_GROUND,
        VERUS
    }

}

package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.impl.player.Scaffold;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

public class AntiFall extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.PACKET);
    private final FloatSetting height = new FloatSetting("Height", "Fall height", 1.0F, 10.0F, 2.0F);
    private final BooleanSetting voidOnly = new BooleanSetting("Void Only", "Only prevents you from falling if you fall into the void", true);
    private final BooleanSetting scaffold = new BooleanSetting("Scaffold", "Enables scaffold upon saving", false);

    private BlockPos lastGroundPos = null;

    public AntiFall() {
        super("Anti Fall", "Prevents you from falling off edges", Category.MOVEMENT);
        this.addSettings(mode, height, voidOnly, scaffold);
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {

        if (BlockUtil.isSolid(mc.thePlayer.getPosition().down())) {
            lastGroundPos = mc.thePlayer.getPosition();
        }

        if (this.shouldSave()) {
            switch (mode.get()) {
                case SET_BACK:
                    mc.thePlayer.setPosition(lastGroundPos.getX() + 0.5, lastGroundPos.getY(), lastGroundPos.getZ() + 0.5);
                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
                    lastGroundPos = null;
                    break;

                case PACKET:
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition());
                    break;
            }

            if (this.scaffold.get()) {
                Nonsense.module(Scaffold.class).toggle(true);
            }

        }

    }

    private boolean shouldSave() {
        return mc.thePlayer.fallDistance > this.height.get() && (!voidOnly.get() || !PlayerUtil.isBlockUnder());
    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    private enum Mode {
        SET_BACK,
        PACKET
    }

}

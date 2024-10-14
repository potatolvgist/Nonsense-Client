package wtf.bhopper.nonsense.module.impl.other;

import io.netty.util.internal.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.module.setting.util.Description;

public class Derp extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.SPIN);
    private final FloatSetting pitchSet = new FloatSetting("Pitch", "Pitch", -90.0F, 90.0F, 0.0F);
    private final FloatSetting speed = new FloatSetting("Speed", "Speed", 1.0F, 20.0F, 5.0F);
    private final BooleanSetting sneak = new BooleanSetting("Sneak", "Sneak spam", false);
    private final BooleanSetting lockView = new BooleanSetting("Lock View", "Gives you autism", false);

    private float spinYaw = 0.0F;

    public Derp() {
        super("Derp", "Anti aim", Category.OTHER);
        this.addSettings(mode, pitchSet, speed, sneak, lockView);
    }

    @Override
    public void onEnable() {
        spinYaw = mc.thePlayer.rotationYaw;
    }

    @Override
    public void onDisable() {
        if (sneak.get()) {
            mc.thePlayer.setSneaking(false);
        }
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        float yaw = event.yaw;
        float pitch = event.pitch;

        switch (mode.get()) {
            case SPIN:
                yaw = spinYaw += speed.get();
                pitch = pitchSet.get();
                break;

            case CRAZY:
                yaw = ThreadLocalRandom.current().nextInt(-180, 180);
                pitch = ThreadLocalRandom.current().nextInt(-90, 90);
                break;

            case BACKWARDS:
                yaw += 180.0F;
                pitch = -pitch;
                break;

            case DEATH:
                pitch = 180.0F;
                break;
        }

        event.yaw = yaw;
        event.pitch = pitch;

        if (lockView.get()) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }

        if (sneak.get()) {
            mc.thePlayer.setSneaking(mc.thePlayer.ticksExisted % 2 == 0);
        }

    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    public enum Mode {
        SPIN,
        CRAZY,
        BACKWARDS,
        @Description("Will likely ban on most anticheats") DEATH
    }

}

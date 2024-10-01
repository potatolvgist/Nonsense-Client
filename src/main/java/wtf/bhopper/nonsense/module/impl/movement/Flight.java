package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

public class Flight extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.VANILLA);

    private final GroupSetting vanillaSpeed = new GroupSetting("Vanilla Speed", "Vanilla speed", this);
    private final FloatSetting vanillaHorizonal = new FloatSetting("Horizontal", "Horizontal speed", 0.1F, 10.0F, 1.0F);
    private final FloatSetting vanillaVertical = new FloatSetting("Vertical", "Vertical speed", 0.1F, 10.0F, 1.0F);

    public Flight() {
        super("Flight", "Allows you to fly", Category.MOVEMENT);
        this.vanillaSpeed.add(vanillaHorizonal, vanillaVertical);
        this.addSettings(this.mode, vanillaSpeed);
    }

    @EventHandler
    public void onMove(EventMove event) {

        switch (mode.get()) {
            case VANILLA: {
                MoveUtil.setSpeed(event, vanillaHorizonal.get());
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.setVertical(event, vanillaVertical.get());
                } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.setVertical(event, -vanillaVertical.get());
                } else {
                    MoveUtil.setVertical(event, 0.0);
                }
                break;
            }
        }

    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    enum Mode {
        VANILLA
    }

}

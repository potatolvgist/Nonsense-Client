package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;

public class Sprint extends Module {

    public final BooleanSetting omniSprint = new BooleanSetting("Omni Sprint", "Allows you to sprint in all directions", false);
    private final BooleanSetting keepSprint = new BooleanSetting("Keep Sprint", "Prevents you from being slowed down after attacking", true);

    public Sprint() {
        super("Sprint", "Makes you sprint", Category.MOVEMENT);
        this.addSettings(this.omniSprint, this.keepSprint);
    }

    @EventHandler
    public void onPreTick(EventPreTick event) {
        mc.gameSettings.keyBindSprint.setPressed(true);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.setPressed(false);
        mc.thePlayer.setSprinting(false);
    }

    public boolean keepSprint() {
        return this.isEnabled() && this.keepSprint.get();
    }
}

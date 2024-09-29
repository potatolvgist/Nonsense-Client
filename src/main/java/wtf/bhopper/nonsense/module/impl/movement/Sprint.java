package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;

public class Sprint extends Module {

    public final BooleanSetting omniSprint = new BooleanSetting("Omni Sprint", "Allows you to sprint in all directions", false);

    public Sprint() {
        super("Sprint", "Makes you sprint", Category.MOVEMENT);
        this.addSettings(this.omniSprint);
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
}

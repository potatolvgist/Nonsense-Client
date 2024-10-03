package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;

public class NoJumpDelay extends Module {

    public NoJumpDelay() {
        super("No Jump Delay", "Removes the jump delay", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        if (mc.inGameHasFocus) {
            mc.thePlayer.jumpTicks = 0;
        }
    }

}

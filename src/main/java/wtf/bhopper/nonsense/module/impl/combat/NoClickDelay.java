package wtf.bhopper.nonsense.module.impl.combat;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;

public class NoClickDelay extends Module {

    public NoClickDelay() {
        super("No Click Delay", "Removes the click delay after missing an attack", Category.COMBAT);
    }

    @EventHandler
    public void onPreTick(EventPreTick event) {
        mc.leftClickCounter = 0;
    }

}

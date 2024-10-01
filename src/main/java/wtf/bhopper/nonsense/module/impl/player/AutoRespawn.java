package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;

public class AutoRespawn extends Module {

    private final BooleanSetting instant = new BooleanSetting("Instant", "Respawn instantly", false);

    public AutoRespawn() {
        super("Auto Respawn", "Automatically respawns upon death", Category.PLAYER);
        this.addSettings(this.instant);
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        if (instant.get()) {
            if (mc.thePlayer.getHealth() <= 0.0F) {
                mc.thePlayer.respawnPlayer();
            }
        } else {
            if (mc.thePlayer.isDead) {
                mc.thePlayer.respawnPlayer();
            }
        }
    }

}

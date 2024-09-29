package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;

public class GameSpeed extends Module {

    private final FloatSetting speed = new FloatSetting("Speed", "Game speed multiplier", 0.1F, 10.0F, 1.5F);

    public GameSpeed() {
        super("Game Speed", "Changes the game speed", Category.PLAYER);
        this.addSettings(this.speed);
    }

    @EventHandler
    public void onPreTick(EventPreTick event) {
        mc.timer.timerSpeed = this.speed.get();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }

    @Override
    public String getSuffix() {
        return this.speed.getDisplayValue();
    }
}

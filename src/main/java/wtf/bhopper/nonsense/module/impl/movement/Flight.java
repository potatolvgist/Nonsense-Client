package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

public class Flight extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.VANILLA);

    private final GroupSetting vanillaSpeed = new GroupSetting("Vanilla Speed", "Vanilla speed", this);
    private final FloatSetting vanillaHorizonal = new FloatSetting("Horizontal", "Horizontal speed", 0.1F, 10.0F, 1.0F);
    private final FloatSetting vanillaVertical = new FloatSetting("Vertical", "Vertical speed", 0.1F, 10.0F, 1.0F);

    private int stage = 0;
    private double speed = 0.0;
    private double lastDist = 0.0;

    public Flight() {
        super("Flight", "Allows you to fly", Category.MOVEMENT);
        this.vanillaSpeed.add(vanillaHorizonal, vanillaVertical);
        this.addSettings(this.mode, vanillaSpeed);
    }

    @Override
    public void onEnable() {
        this.stage = 0;
        this.speed = 0.0;
        this.lastDist = 0.0;
    }

    @Override
    public void onDisable() {
        if (mode.is(Mode.MINIBLOX)) {
            mc.thePlayer.motionX = Math.max(Math.min(mc.thePlayer.motionX, 0.3), -0.3);
            mc.thePlayer.motionZ = Math.max(Math.min(mc.thePlayer.motionZ, 0.3), -0.3);
        }
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        this.lastDist = MoveUtil.lastDistance();
    }

    @EventHandler
    public void onMove(EventMove event) {

        switch (mode.get()) {
            case VANILLA: {
                MoveUtil.setSpeed(event, vanillaHorizonal.get());
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, vanillaVertical.get());
                } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, -vanillaVertical.get());
                } else {
                    MoveUtil.vertical(event, 0.0);
                }
                break;
            }

            case MINIBLOX: {
                MoveUtil.setSpeed(event, 0.39);
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, 0.3);
                } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event,-0.3);
                } else {
                    MoveUtil.vertical(event, 0.0);
                }
                break;
            }

            case MINIBLOX_TEST: {

                switch (this.stage) {

                    case 0: {

                        if (MoveUtil.isMoving()) {
                            if (PlayerUtil.selfDamage(0.0625, true, true)) {
                                this.stage = 1;
                                this.speed = 1.0;
                                MoveUtil.vertical(event, 0.42);
                            }
                        }

                        break;
                    }

                    case 1: {
                        MoveUtil.setSpeed(event, speed);
                        MoveUtil.vertical(event, 0.0);
                        stage = 2;
                        break;
                    }

                    case 2: {
                        speed = lastDist - lastDist / 159.0;
                        speed = Math.max(speed, MoveUtil.baseSpeed());
                        MoveUtil.setSpeed(event, speed);
                        MoveUtil.vertical(event, 0.0);
                        break;
                    }

                }

            }
        }

    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    enum Mode {
        VANILLA,
        MINIBLOX,
        MINIBLOX_TEST
    }

}

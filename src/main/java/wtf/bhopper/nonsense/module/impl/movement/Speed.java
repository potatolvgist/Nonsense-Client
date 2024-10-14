package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;

public class Speed extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.VANILLA, value -> {
        this.speedSet.setDisplayed(value == Mode.VANILLA);
        this.bhopSpeed.setDisplayed(value == Mode.BHOP);
    });

    private final FloatSetting speedSet = new FloatSetting("Speed", "How fast to go", 1.0F, 20.0F, 5.6F);
    private final FloatSetting bhopSpeed = new FloatSetting("Bhop Speed", "Bhop speed, 1.6 will bypass NCP", 0.1F, 3.0F, 1.6F);

    private int stage = 0;
    private double speed = 0.0;
    private double distance = 0.0;
    private int lastJump = 10;

    public Speed() {
        super("Speed", "Makes you go zoom", Category.MOVEMENT);
        this.addSettings(mode, speedSet, bhopSpeed);
        this.mode.updateChange();
    }

    @Override
    public void onEnable() {
        this.stage = 0;
        this.speed = 0.0;
        this.lastJump = 10;
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        this.distance = MoveUtil.lastDistance();
    }


    // TODO: add a player move event
    @EventHandler
    public void onMove(EventMove event) {
        switch (mode.get()) {
            case VANILLA: {
                MoveUtil.setSpeed(event, speedSet.get() / 20.0);
                break;
            }

            case BHOP: {
                switch (this.stage) {
                    case 0:
                    case 1: {
                        if (this.stage == 0 && MoveUtil.isMoving()) {
                            this.stage++;
                            this.speed = 1.18 * MoveUtil.baseSpeed() - 0.01;
                        }

                        if (!MoveUtil.isMoving() || !mc.thePlayer.onGround) {
                            break;
                        }

                        MoveUtil.jump(event, 0.4);
                        this.speed *= this.bhopSpeed.get();
                        this.stage++;
                        break;
                    }

                    case 2: {
                        this.speed = this.distance - 0.76 * (this.distance - MoveUtil.baseSpeed());
                        this.stage++;
                        break;
                    }

                    case 3: {
                        if (mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)) || mc.thePlayer.isCollidedVertically && stage > 0) {
                            this.stage = 0;
                        }
                        this.speed = this.distance - (this.distance / 159.0);
                        break;
                    }

                }

                this.speed = Math.max(this.speed, MoveUtil.baseSpeed());

                MoveUtil.setSpeed(event, this.speed);

                break;
            }

            case MINIBLOX: {

                lastJump++;
                if (mc.thePlayer.onGround) {
                    lastJump = 0;
                }

                MoveUtil.setSpeed(event, 0.39);
                if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    MoveUtil.jump(event, 0.4);
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
        VANILLA,
        BHOP,
        MINIBLOX
    }

}

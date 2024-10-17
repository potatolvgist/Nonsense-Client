package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.util.minecraft.client.BlinkUtil;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

public class Flight extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.VANILLA, value -> {
        this.vanillaHorizontal.setDisplayed(value == Mode.VANILLA || value == Mode.MINIBLOX_TEST);
        this.vanillaVertical.setDisplayed(value == Mode.VANILLA || value == Mode.MINIBLOX_TEST);
    });

    private final FloatSetting vanillaHorizontal = new FloatSetting("Horizontal", "Horizontal speed", 0.1F, 10.0F, 1.0F);
    private final FloatSetting vanillaVertical = new FloatSetting("Vertical", "Vertical speed", 0.1F, 10.0F, 1.0F);

    private int stage = 0;
    private double speed = 0.0;
    private double lastDist = 0.0;

    private boolean spoof = false;

    public Flight() {
        super("Flight", "Allows you to fly", Category.MOVEMENT);
        this.addSettings(this.mode, vanillaHorizontal, vanillaVertical);
    }

    @Override
    public void onEnable() {
        this.stage = 0;
        this.speed = 0.0;
        this.lastDist = 0.0;
        this.spoof = false;
    }

    @Override
    public void onDisable() {
        if (mode.is(Mode.MINIBLOX)) {
            mc.thePlayer.motionX = Math.max(Math.min(mc.thePlayer.motionX, 0.3), -0.3);
            mc.thePlayer.motionZ = Math.max(Math.min(mc.thePlayer.motionZ, 0.3), -0.3);
            BlinkUtil.disableBlink();
        }
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        this.lastDist = MoveUtil.lastDistance();
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        if (this.mode.is(Mode.MINIBLOX)) {

            if (mc.thePlayer.ticksExisted % 10 == 0) {
                if (this.spoof = !this.spoof) {
                    BlinkUtil.enableBlink();
                } else {
                    BlinkUtil.disableBlink();
                }
            }

        }

        if (this.mode.is(Mode.MINIBLOX_TEST) && mc.thePlayer.ticksExisted % 10 == 0) {
            PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SLEEPING));
        }

    }

    @EventHandler
    public void onMove(EventMove event) {

        switch (mode.get()) {
            case VANILLA: {
                MoveUtil.setSpeed(event, vanillaHorizontal.get());
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

            case DAMAGE_BOOST: {

                switch (this.stage) {

                    case 0: {

                        if (MoveUtil.isMoving()) {
                            if (PlayerUtil.selfDamage(0.0625, true, true)) {
                                this.stage = 1;
                                this.speed = 1.25;
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

            case MINIBLOX_TEST: {
                MoveUtil.setSpeed(event, vanillaHorizontal.get());
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, vanillaVertical.get());
                } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    MoveUtil.vertical(event, -vanillaVertical.get());
                } else {
                    MoveUtil.vertical(event, 0.0);
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
        MINIBLOX,
        DAMAGE_BOOST,
        MINIBLOX_TEST
    }

}

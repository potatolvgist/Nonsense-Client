package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.bhopper.nonsense.event.impl.EventMove;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.*;
import wtf.bhopper.nonsense.module.setting.util.Description;
import wtf.bhopper.nonsense.util.minecraft.client.BlinkUtil;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.misc.Clock;

public class Flight extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.VANILLA, value -> {
        this.vanillaHorizontal.setDisplayed(value == Mode.VANILLA || value == Mode.MINIBLOX);
        this.vanillaVertical.setDisplayed(value == Mode.VANILLA || value == Mode.MINIBLOX);
        this.speedSet.setDisplayed(value == Mode.BOOST);
        this.boostGroup.setDisplayed(value == Mode.BOOST);
        this.miniSpoof.setDisplayed(value == Mode.MINIBLOX);
    });

    private final FloatSetting vanillaHorizontal = new FloatSetting("Horizontal", "Horizontal speed", 0.1F, 10.0F, 1.0F);
    private final FloatSetting vanillaVertical = new FloatSetting("Vertical", "Vertical speed", 0.1F, 10.0F, 1.0F);
    private final FloatSetting speedSet = new FloatSetting("Speed", "Speed", 0.1F, 10.0F, 1.2F);
    private final BooleanSetting miniSpoof = new BooleanSetting("Spoof", "Ground spoof", true);

    private final GroupSetting boostGroup = new GroupSetting("Boost", "Boost settings", this);
    private final BooleanSetting quickStop = new BooleanSetting("Quick Stop", "Set motion to 0 when you stop flying", true);
    private final BooleanSetting useTimer = new BooleanSetting("Timer", "Use timer to increase speed", false);
    private final FloatSetting timerFactor = new FloatSetting("Timer Factor", "Timer speed", 0.1F, 3.0F, 1.5F);
    private final IntSetting timerTime = new IntSetting("Timer Time", "How long to use timer for", 1, 3000, 500, "%dms", null);
    private final FloatSetting timerStart = new FloatSetting("Timer Start", "Timer start factor", 0.1F, 3.0F, 1.0F);
    private final EnumSetting<Damage> damage = new EnumSetting<>("Damage", "Damage boost", Damage.PACKET);

    private int stage = 0;
    private double speed = 0.0;
    private double lastDist = 0.0;

    private boolean stopTimer = false;
    private final Clock timerClock = new Clock();

    public Flight() {
        super("Flight", "Allows you to fly", Category.MOVEMENT);
        this.boostGroup.add(quickStop, useTimer, timerFactor, timerTime, timerStart, damage);
        this.addSettings(this.mode, this.boostGroup, vanillaHorizontal, vanillaVertical, speedSet);
        this.mode.updateChange();
    }

    @Override
    public void onEnable() {
        this.stage = 0;
        this.speed = 0.0;
        this.lastDist = 0.0;
    }

    @Override
    public void onDisable() {
        if (mode.is(Mode.MINIBLOX_BLINK)) {
            mc.thePlayer.motionX = Math.max(Math.min(mc.thePlayer.motionX, 0.3), -0.3);
            mc.thePlayer.motionZ = Math.max(Math.min(mc.thePlayer.motionZ, 0.3), -0.3);
            BlinkUtil.disableBlink();
        }

        if (this.mode.is(Mode.BOOST)) {
            if (this.useTimer.get()) {
                mc.timer.timerSpeed = 1.0F;
            }

            if (this.quickStop.get()) {
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
            }

        }

    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        this.lastDist = MoveUtil.lastDistance();

        if (this.mode.is(Mode.MINIBLOX) && this.miniSpoof.get()) {
            event.onGround = true;
        }

    }

    @EventHandler
    public void onTick(EventPreTick event) {

        if (this.mode.is(Mode.MINIBLOX)) {
            stage++;
            if (stage == 6) {
                PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SLEEPING));
            }
        }

        if (this.mode.is(Mode.MINIBLOX_BLINK)) {

            BlinkUtil.enableBlink();
            if (mc.thePlayer.ticksExisted % 10 == 0) {
                BlinkUtil.poll();
            }

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
                if (this.stage >= 6) {
                    MoveUtil.setSpeed(event, 0.0);
                    MoveUtil.vertical(event, 0.0);
                } else {
                    MoveUtil.setSpeed(event, vanillaHorizontal.get());
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        MoveUtil.vertical(event, vanillaVertical.get());
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        MoveUtil.vertical(event, -vanillaVertical.get());
                    } else {
                        MoveUtil.vertical(event, 0.0);
                    }
                }

                break;
            }

            case MINIBLOX_BLINK: {
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

            case BOOST: {

                switch (this.stage) {

                    case 0: {

                        if (MoveUtil.isMoving()) {

                            if (this.useTimer.get()) {
                                mc.timer.timerSpeed = this.timerStart.get();
                            }

                            boolean start;
                            switch (this.damage.get()) {
                                case PACKET:
                                    start = PlayerUtil.selfDamage(0.0625, true, true);
                                    break;

                                default:
                                    start = mc.thePlayer.onGround;
                                    break;
                            }

                            if (start) {
                                this.stage = 1;
                                this.speed = this.speedSet.get();
                                MoveUtil.vertical(event, 0.42);
                            }
                        }

                        break;
                    }

                    case 1: {

                        if (this.useTimer.get()) {
                            this.timerClock.reset();
                            this.stopTimer = false;
                            mc.timer.timerSpeed = this.timerFactor.get();
                        }

                        MoveUtil.setSpeed(event, speed);
                        MoveUtil.vertical(event, 0.0);
                        stage = 2;
                        break;
                    }

                    case 2: {

                        if (this.useTimer.get()) {

                            if (this.timerClock.hasReached(this.timerTime.get())) {
                                if (!this.stopTimer) {
                                    this.stopTimer = true;
                                    mc.timer.timerSpeed = 1.0F;
                                }
                            } else {
                                mc.timer.timerSpeed = this.timerFactor.get();
                            }
                        }

                        speed = lastDist - lastDist / 159.0;
                        speed = Math.max(speed, MoveUtil.baseSpeed());
                        MoveUtil.setSpeed(event, speed);
                        MoveUtil.vertical(event, 0.0);
                        break;
                    }

                }

                break;

            }
        }

    }

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {
        if (mode.is(Mode.MINIBLOX) && event.packet instanceof S08PacketPlayerPosLook) {
            stage = 0;
        }
    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    enum Mode {
        VANILLA,
        MINIBLOX,
        MINIBLOX_BLINK,
        @Description("Recreate the 2020 damage boost flies!") BOOST
    }

    enum Damage {
        PACKET,
        NONE
    }

}

package wtf.bhopper.nonsense.module.impl.combat;

import io.netty.util.internal.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.*;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.*;
import wtf.bhopper.nonsense.module.setting.util.Description;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "targetting mode", Mode.SINGLE, value -> this.switchDelay.setDisplayed(value == Mode.SWITCH));

    private final GroupSetting targetsGroup = new GroupSetting("Targets", "Targets", this);
    private final BooleanSetting players = new BooleanSetting("Players", "Target players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", "Target mobs", false);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Target animals", false);
    private final BooleanSetting others = new BooleanSetting("Others", "Target others", false);
    private final BooleanSetting invis = new BooleanSetting("Invisibles", "Target invisible entities", false);
    private final BooleanSetting dead = new BooleanSetting("Dead", "Target dead entities", false);
    private final BooleanSetting teams = new BooleanSetting("Ignore Teammates", "Prevents you from attacking teammates", true);

    private final GroupSetting attackSpeed = new GroupSetting("Attack Speed", "Attack speed", this);
    private final IntSetting minAps = new IntSetting("Min APS", "Minimum attacks per second", 1, 20, 9, "%d APS", value -> {
        if (this.maxAps.get() < value) {
            this.maxAps.set(value);
        }
    });
    private final IntSetting maxAps = new IntSetting("Max APS", "Maximum attacks per second", 1, 20, 12, "%d APS", value -> {
        if (this.minAps.get() > value) {
            this.minAps.set(value);
        }
    });

    private final GroupSetting rotationsGroup = new GroupSetting("Rotations", "Rotations", this);
    private final FloatSetting fov = new FloatSetting("FOV", "Fov check", 0.0F, 360.0F, 360.0F, new DecimalFormat("#0.##'\u00b0'"), null);
    private final EnumSetting<RotationMode> rotationMode = new EnumSetting<>("Mode", "Rotations mode", RotationMode.INSTANT);
    private final EnumSetting<RotationRandomization> randomization = new EnumSetting<>("Random", "Randomizes rotations to help bypass anti-cheats", RotationRandomization.NONE);

    private final GroupSetting strafeGroup = new GroupSetting("Target Strafe", "Strafe around targets", this);
    private final BooleanSetting enableStrafe = new BooleanSetting("Enable", "Enable target strafe", false);
    private final BooleanSetting onSpace = new BooleanSetting("On Jump", "Only strafe while holding jump", true);
    private final EnumSetting<StrafeMode> strafeMode = new EnumSetting<>("Mode", "Strafe mode", StrafeMode.CIRCLE);
    private final FloatSetting strafeRadius = new FloatSetting("Radius", "Strafe radius", 0.1F, 4.0F, 1.0F);

    private final FloatSetting attackRange = new FloatSetting("Attack Range", "Attacking range", 1.0F, 10.0F, 4.2F);
    private final FloatSetting swingRange = new FloatSetting("Swing Range", "Swinging range", 1.0F, 16.0F, 5.2F);
    private final FloatSetting rotateRange = new FloatSetting("Rotate Range", "Rotating range", 1.0F, 16.0F, 5.2F);
    private final EnumSetting<TargetSorting> sorting = new EnumSetting<>("Sorting", "Target sorting", TargetSorting.DISTANCE);
    private final EnumSetting<AttackMode> attackMode = new EnumSetting<>("Attack Mode", "Attack mode", AttackMode.VANILLA);
    private final EnumSetting<SwingMode> swingMode = new EnumSetting<>("Swing Mode", "Swinging mode", SwingMode.ATTACK_ONLY);
    private final BooleanSetting walls = new BooleanSetting("Walls", "Allows Kill Aura to attack through walls", true);
    private final IntSetting ticksExisted = new IntSetting("Ticks Existed", "Amount of ticks an entity has to have existed for before attacking", 0, 100, 15);
    private final BooleanSetting autoDisable = new BooleanSetting("Auto Disable", "Automatically disabled Kill Aura", true);
    private final IntSetting switchDelay = new IntSetting("Switch Delay", "Switch delay", 0, 1000, 250, "%dms", null);

    private EntityLivingBase target = null;
    private final List<EntityLivingBase> invalidTargets = new ArrayList<>();
    private final List<EntityLivingBase> targets = new ArrayList<>();
    private int targetCounter = 0;
    private final Clock switchTimer = new Clock();

    private final Clock attackTimer = new Clock();
    private int nextAttackDelay = 0;
    private boolean canAttack = false;
    private boolean blockAttack = false;

    private Rotation rotations;
    private Rotation targetRotations = new Rotation();
    private Rotation prevRotations = new Rotation();

    private int attacked = -1;

    private boolean strafing = false;
    private int strafeDirection = 1;
    private int strafeTicks = 0;

    public KillAura() {
        super("Kill Aura", "Automatically attacks nearby entities", Category.COMBAT);
        this.targetsGroup.add(players, mobs, animals, others, invis, dead, teams);
        this.attackSpeed.add(minAps, maxAps);
        this.rotationsGroup.add(fov, rotationMode, randomization);
        this.strafeGroup.add(enableStrafe, onSpace, strafeMode, strafeRadius);
        this.addSettings(mode,
                targetsGroup, attackSpeed, rotationsGroup, strafeGroup,
                attackRange, swingRange, rotateRange,
                sorting, attackMode, swingMode,
                walls, ticksExisted, autoDisable, switchDelay);
        this.mode.updateChange();
    }

    @Override
    public void onEnable() {
        this.prevRotations.yaw = mc.thePlayer.rotationYaw;
        this.prevRotations.pitch = mc.thePlayer.rotationPitch;
        this.updateAttackDelay();
        this.strafing = false;
    }

    @EventHandler
    public void onTick(EventPreTick event) {

        this.blockAttack = false;
        this.attacked = -1;

        if (mc.thePlayer.isCollidedHorizontally && strafeTicks >= 4) {
            strafeDirection = -strafeDirection;
            strafeTicks = 0;
        } else {
            ++strafeTicks;
        }

        if (mc.thePlayer.getHealth() <= 0.0F && autoDisable.get()) {
            this.toggle(false);
            Notification.send("Kill Aura", "Kill Aura was automatically disabled", NotificationType.WARNING, 3000);
            return;
        }

        this.updateTargetList();
        this.selectTarget();

        if (this.target == null) {
            return;
        }

        this.rotate();
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        if (this.target != null && this.rotations != null) {
            event.setRotations(this.rotations);
        }
    }

    @EventHandler
    public void onMouseOver(EventMouseOverUpdate event) {
        if (this.target != null && this.canAttack) {
            event.objectMouseOver = new MovingObjectPosition(this.target, this.targetRotations.hitVec);
        }
    }

    @EventHandler
    public void onClickAction(EventClickAction event) {
        if (!this.attackMode.is(AttackMode.VANILLA) || this.target == null || this.blockAttack) {
            return;
        }

        if (!this.attackTimer.hasReached(this.nextAttackDelay)) {
            return;
        }

        if (event.button == EventClickAction.Button.LEFT) {
            this.updateAttackDelay();
            this.attackTimer.reset();
            event.click = true;

            switch (this.swingMode.get()) {
                case CLIENT:
                    event.silentSwing = false;
                    break;

                case SILENT:
                    event.silentSwing = true;
                    break;

                case ATTACK_ONLY:
                    event.silentSwing = !this.canAttack;
                    break;
            }
        }
    }

    @EventHandler
    public void onPostMotion(EventPostMotion event) {
        if (!this.attackMode.is(AttackMode.POST) || this.target == null) {
            return;
        }

        if (!this.attackTimer.hasReached(this.nextAttackDelay)) {
            return;
        }

        this.updateAttackDelay();
        this.attackTimer.reset();

        boolean silentSwing = false;

        switch (this.swingMode.get()) {
            case CLIENT:
                silentSwing = false;
                break;

            case SILENT:
                silentSwing = true;
                break;

            case ATTACK_ONLY:
                silentSwing = !this.canAttack;
                break;
        }

        mc.clickMouse(silentSwing);
    }

    @EventHandler
    public void onJoin(EventJoinGame event) {
        if (autoDisable.get()) {
            this.toggle(false);
            Notification.send("Kill Aura", "Kill Aura was automatically disabled", NotificationType.WARNING, 3000);
        }
    }

    @EventHandler
    public void onSpeed(EventSpeed event) {

        if (!this.canStrafe()) {
            this.strafing = false;
            return;
        }

        this.strafing = true;

        switch (strafeMode.get()) {
            case CIRCLE: {
                Rotation rotation = RotationUtil.getRotations(this.target);

                event.yaw = rotation.yaw;
                event.strafe = strafeDirection;
                if (Math.hypot(mc.thePlayer.posX - target.posX, mc.thePlayer.posZ - target.posZ) > this.strafeRadius.get()) {
                    event.forward = 1.0;
                } else {
                    event.forward = 0.0;
                }

                break;
            }

            case BACK: {
                Rotation rotation = RotationUtil.getRotations(this.target);
                Rotation targetRot = RotationUtil.getRotations(mc.thePlayer, this.target);

                event.yaw = rotation.yaw;

                if (targetRot.yaw - target.rotationYaw > 0.0F) {
                    event.strafe = 1.0;
                } else {
                    event.strafe = -1.0;
                }

                if (Math.hypot(mc.thePlayer.posX - target.posX, mc.thePlayer.posZ - target.posZ) > this.strafeRadius.get()) {
                    event.forward = 1.0;
                } else {
                    event.forward = 0.0;
                }

                break;
            }
        }

    }

    private void rotate() {

        this.prevRotations = this.targetRotations;

        switch (this.randomization.get()) {
            case SAMPLE:
                this.targetRotations = RotationUtil.getRotationsRandomOptimizedQuick(this.target.getEntityBoundingBox(), 0.05, this.canAttack ? this.attackRange.get() : this.rotateRange.get());
                break;

            case GEOMETRY:
                this.targetRotations = RotationUtil.getRotationsRandomOptimized(this.target.getEntityBoundingBox(), this.canAttack ? this.attackRange.get() : this.rotateRange.get());
                break;

            default:
                this.targetRotations = RotationUtil.getRotationsOptimized(this.target.getEntityBoundingBox());
                break;
        }

        switch (this.rotationMode.get()) {
            default:
                this.rotations = this.targetRotations;
                break;
        }

        if (!MathUtil.rayIntersection(this.target.getEntityBoundingBox(), mc.thePlayer.getPositionEyes(1.0F), this.rotations.yaw, this.rotations.pitch, this.attackRange.get())) {
            this.canAttack = false;
        }

    }


    private void updateAttackDelay() {
        if (this.minAps.get().equals(this.maxAps.get())) {
            this.nextAttackDelay = (int)(1000.0F / this.minAps.get());
            return;
        }

        this.nextAttackDelay = (int)(1000.0F / (float)ThreadLocalRandom.current().nextInt(this.minAps.get(), this.maxAps.get() + 1));
    }

    private void updateTargetList() {
        this.invalidTargets.clear();
        this.targets.clear();
        this.invalidTargets.addAll(mc.theWorld.getEntities(EntityLivingBase.class, this::isValidTarget));
        this.targets.addAll(this.invalidTargets.stream()
                .filter(entity -> RotationUtil.rayCastRange(entity.getEntityBoundingBox()) <= attackRange.get())
                .collect(Collectors.toList()));
        this.invalidTargets.removeIf(targets::contains);
        Comparator<? super EntityLivingBase> comparator = this.getTargetComparator();
        this.targets.sort(comparator);
        this.invalidTargets.sort(comparator);
    }

    private void selectTarget() {

        switch (mode.get()) {
            case SINGLE: {

                if (this.target != null && this.targets.contains(this.target)) {
                    this.canAttack = true;
                    return;
                }

                if (!this.targets.isEmpty()) {
                    this.target = this.targets.get(0);
                    this.canAttack = true;
                } else {
                    if (!this.invalidTargets.isEmpty()) {
                        this.target = this.invalidTargets.get(0);
                    } else {
                        this.target = null;
                    }
                    this.canAttack = false;
                }
                break;
            }

            case SWITCH: {
                if (!this.targets.isEmpty()) {
                    this.target = this.targets.get(this.targetCounter % this.targets.size());
                    this.canAttack = true;
                } else {
                    if (!this.invalidTargets.isEmpty()) {
                        this.target = this.invalidTargets.get(this.targetCounter % this.invalidTargets.size());
                    } else {
                        this.target = null;
                    }
                    this.canAttack = false;
                }

                if (this.switchTimer.hasReached(this.switchDelay.get())) {
                    this.targetCounter++;
                    if (this.targetCounter == Integer.MAX_VALUE) {
                        this.targetCounter = 0;
                    }
                }

                break;
            }

            case PRIORITY: {
                if (!this.targets.isEmpty()) {
                    this.target = this.targets.get(0);
                    this.canAttack = true;
                } else {
                    if (!this.invalidTargets.isEmpty()) {
                        this.target = this.invalidTargets.get(0);
                    } else {
                        this.target = null;
                    }
                    this.canAttack = false;
                }
                break;
            }
        }
    }

    private Comparator<? super EntityLivingBase> getTargetComparator() {

        switch (this.sorting.get()) {
            case DISTANCE:
                return Comparator.<EntityLivingBase>comparingDouble(entity -> RotationUtil.rayCastRange(entity.getEntityBoundingBox())).reversed();

            case HEALTH:
                return Comparator.comparingDouble(EntityLivingBase::getHealth).reversed();
        }

        return (Comparator<EntityLivingBase>) (o1, o2) -> 0;
    }

    private boolean isValidTarget(EntityLivingBase entity) {
        float rangeCheck = Math.max(attackRange.get(), Math.max(swingRange.get(), rotateRange.get()));

        if (entity == mc.thePlayer) {
            return false;
        }

        if (Nonsense.INSTANCE.moduleManager.get(AntiBot.class).isBot(entity)) {
            return false;
        }

        if (entity.ticksExisted < this.ticksExisted.get()) {
            return false;
        }

        if (RotationUtil.rayCastRange(entity.getEntityBoundingBox()) > rangeCheck) {
            return false;
        }

        if (!walls.get()) {
            // TODO: walls stuff idk
        }

        if (entity instanceof EntityPlayer) {
            if (!players.get()) {
                return false;
            }
            if (teams.get() && PlayerUtil.isOnSameTeam((EntityPlayer)entity)) {
                return false;
            }

        } else if (entity instanceof EntityMob) {
            if (!mobs.get()) {
                return false;
            }
        } else if (entity instanceof EntityAnimal) {
            if (!animals.get()) {
                return false;
            }
        } else {
            if (!others.get()) {
                return false;
            }
        }

        if (entity.isInvisible() && !invis.get()) {
            return false;
        }

        if (entity.isDead && !dead.get()) {
            return false;
        }

        if (fov.get() < 360.0F) {
            float yawCheck = Math.abs(RotationUtil.getRotations(entity).yaw - mc.thePlayer.rotationYaw);
            if (yawCheck <= fov.get() / 2.0F) {
                return false;
            }
        }

        return true;
    }

    public void blockAttack() {
        this.blockAttack = true;
    }

    private boolean canStrafe() {
        if (!enableStrafe.get()) {
            return false;
        }

        if (this.onSpace.get() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            return false;
        }

        if (this.target == null || !MoveUtil.isMoving()) {
            return false;
        }

        return true;
    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    private enum Mode {
        @Description("Selects a target and keeps attacking it until it dies or becomes invalid") SINGLE,
        @Description("Switches between multiple targets") SWITCH,
        @Description("Attacks the best target") PRIORITY
    }

    private enum RotationMode {
        INSTANT,
        LINEAR
    }

    private enum RotationRandomization {
        GEOMETRY,
        SAMPLE,
        NONE
    }

    private enum StrafeMode {
        CIRCLE,
        BACK
    }

    private enum TargetSorting {
        DISTANCE,
        HEALTH
    }

    private enum AttackMode {
        VANILLA,
        POST
    }

    private enum SwingMode {
        CLIENT,
        @Description("Hides swings client side (you will still swing server side)") SILENT,
        @Description("Only shows client side swinging when you're attacking") ATTACK_ONLY
    }

}

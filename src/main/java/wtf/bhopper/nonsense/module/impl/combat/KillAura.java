package wtf.bhopper.nonsense.module.impl.combat;

import io.netty.util.internal.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.*;
import wtf.bhopper.nonsense.module.setting.util.Description;
import wtf.bhopper.nonsense.util.Clock;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    private static final DecimalFormat FOV_FORMAT = new DecimalFormat("#0.##'\u00b0'");

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "targetting mode", Mode.SINGLE);

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
    private final FloatSetting fov = new FloatSetting("FOV", "Fov check", 0.0F, 360.0F, 360.0F, FOV_FORMAT, null);
    private final EnumSetting<RotationMode> rotationMode = new EnumSetting<>("Mode", "Rotatiosn mode", RotationMode.INSTANT);
    private final EnumSetting<RotationRandomization> randomization = new EnumSetting<>("Random", "Randomizes rotations to help bypass anticheats", RotationRandomization.NONE);

    private final FloatSetting attackRange = new FloatSetting("Attack Range", "Attacking range", 1.0F, 7.0F, 4.2F);
    private final FloatSetting swingRange = new FloatSetting("Swing Range", "Swinging range", 1.0F, 10.0F, 5.2F);
    private final FloatSetting rotateRange = new FloatSetting("Rotate Range", "Rotating range", 1.0F, 10.0F, 5.2F);
    private final EnumSetting<TargetSorting> sorting = new EnumSetting<>("Sorting", "Target sorting", TargetSorting.DISTANCE);
    private final EnumSetting<SwingMode> swingMode = new EnumSetting<>("Swing Mode", "Swinging mode", SwingMode.ATTACK_ONLY);

    private EntityLivingBase target = null;
    private EntityLivingBase prevTarget = null;
    private final List<EntityLivingBase> invalidTargets = new ArrayList<>();
    private final List<EntityLivingBase> targets = new ArrayList<>();

    private final Clock attackTimer = new Clock();
    private int nextAttackDelay = 0;
    private boolean canAttack = false;

    private Rotation targetRotations = new Rotation();
    private Rotation prevRotations = new Rotation();

    public KillAura() {
        super("Kill Aura", "Automatically attacks nearby entities", Category.COMBAT);
        this.targetsGroup.add(players, mobs, animals, others, invis, dead, teams);
        this.attackSpeed.add(minAps, maxAps);
        this.rotationsGroup.add(fov, rotationMode, randomization);
        this.addSettings(mode, targetsGroup, attackSpeed, rotationsGroup, attackRange, swingRange, rotateRange, sorting, swingMode);
    }

    @Override
    public void onEnable() {
        this.prevRotations.yaw = mc.thePlayer.rotationYaw;
        this.prevRotations.pitch = mc.thePlayer.rotationPitch;
        this.updateAttackDelay();
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        this.updateTargetList();
        this.selectTarget();

        if (this.target == null) {
            return;
        }

        this.rotate(event);
        this.attack();

    }

    private void rotate(EventPreMotion event) {

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

        Rotation rotation;

        switch (rotationMode.get()) {
            default:
                rotation = this.targetRotations;
                break;
        }

        event.setRotations(rotation);
        this.prevRotations = rotation;
    }

    private void attack() {

        if (!this.attackTimer.hasReached(this.nextAttackDelay)) {
            return;
        }
        this.updateAttackDelay();
        this.attackTimer.reset();

        switch (this.swingMode.get()) {
            case CLIENT:
                mc.thePlayer.swingItem();
                break;

            case SILENT:
                PacketUtil.send(new C0APacketAnimation());
                break;

            case ATTACK_ONLY:
                if (this.canAttack) {
                    mc.thePlayer.swingItem();
                } else {
                    PacketUtil.send(new C0APacketAnimation());
                }
                break;
        }

        if (this.canAttack) {
            PacketUtil.send(new C02PacketUseEntity(this.target, C02PacketUseEntity.Action.ATTACK));
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
        this.prevTarget = this.target;

        switch (mode.get()) {
            case SINGLE: {
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
                // TODO: add the mode
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

        if (RotationUtil.rayCastRange(entity.getEntityBoundingBox()) > rangeCheck) {
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
        @Description("Switches between multiple targets") SWITCH
    }

    private enum RotationMode {
        INSTANT
    }

    private enum RotationRandomization {
        GEOMETRY,
        SAMPLE,
        NONE
    }

    private enum TargetSorting {
        DISTANCE,
        HEALTH
    }

    private enum SwingMode {
        CLIENT,
        @Description("Hides swings client side (you will still swing server side)") SILENT,
        @Description("Only shows client side swinging when you're attacking") ATTACK_ONLY
    }

}

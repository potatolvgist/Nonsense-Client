package wtf.bhopper.nonsense.module.impl.combat;

import com.google.common.base.Predicate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventClickAction;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AutoBlock extends Module {

    private final GroupSetting targetsGroup = new GroupSetting("Targets", "Targets", this);
    private final BooleanSetting players = new BooleanSetting("Players", "Target players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", "Target mobs", false);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Target animals", false);
    private final BooleanSetting others = new BooleanSetting("Others", "Target others", false);
    private final BooleanSetting invis = new BooleanSetting("Invisibles", "Target invisible entities", false);
    private final BooleanSetting dead = new BooleanSetting("Dead", "Target dead entities", false);
    private final BooleanSetting teams = new BooleanSetting("Ignore Teammates", "Prevents you from attacking teammates", true);

    private final FloatSetting range = new FloatSetting("Range", "Target range", 1.0F, 16.0F, 7.0F);

    public AutoBlock() {
        super("Auto Block", "Automatically blocks when there are nearby entities", Category.COMBAT);
        this.targetsGroup.add(players, mobs, animals, others, invis, dead, teams);
        this.addSettings(this.targetsGroup, this.range);
    }

    private boolean shouldBlock = false;

    @Override
    public void onEnable() {
        this.shouldBlock = false;
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        this.shouldBlock = !mc.theWorld.getEntities(EntityLivingBase.class, this::isValidTarget).isEmpty();
    }

    @EventHandler
    public void onClickAction(EventClickAction event) {
        if (event.button == EventClickAction.Button.RIGHT &&
                !mc.thePlayer.isBlocking() && this.shouldBlock &&
                this.blockItem()) {
            event.click = true;
        }

        if (event.button == EventClickAction.Button.RELEASE &&
                mc.thePlayer.isBlocking() && this.shouldBlock) {
            event.click = true;
        }
    }

    private boolean blockItem() {
        try {
            return mc.thePlayer.inventory.getCurrentItem().getItemUseAction() == EnumAction.BLOCK;
        } catch (NullPointerException exception) {
            return false;
        }
    }


    private boolean isValidTarget(EntityLivingBase entity) {

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

        if (RotationUtil.rayCastRange(entity.getPositionEyes(1.0F), mc.thePlayer.getEntityBoundingBox()) > this.range.get()) {
            return false;
        }

        return true;
    }

}

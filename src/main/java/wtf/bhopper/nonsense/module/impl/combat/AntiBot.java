package wtf.bhopper.nonsense.module.impl.combat;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.util.minecraft.world.ServerUtil;

import java.util.HashSet;
import java.util.Set;

public class AntiBot extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.TAB);

    private static final String VALID_USERNAME_REGEX = "^[a-zA-Z0-9_]{1,16}+$";

    public AntiBot() {
        super("Anti Bot", "Prevents bot targetting", Category.COMBAT);
        this.addSettings(this.mode);
    }

    private final Set<EntityPlayer> bots = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTick(EventPreTick event)  {
        switch (this.mode.get()) {

            case TAB: {
                this.bots.clear();
                for (EntityPlayer player : mc.theWorld.getEntities(EntityPlayer.class, input -> true))  {
                    if (!ServerUtil.isInTab(player)) {
                        this.bots.add(player);
                    }
                }
                break;
            }

            case HYPIXEL: {
                this.bots.clear();
                for (EntityPlayer player : mc.theWorld.getEntities(EntityPlayer.class, input -> true))  {
                    if (!ServerUtil.isInTab(player) || this.nameStartsWith(player, "[NPC] ") || !player.getName().matches(VALID_USERNAME_REGEX)) {
                        this.bots.add(player);
                    }
                }
                break;
            }

        }
    }

    public boolean isBot(Entity entity) {
        if (!this.isEnabled()) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            return this.bots.contains(entity);
        }
        return false;
    }

    public boolean isBot(EntityPlayer entityPlayer)  {
        if (!this.isEnabled()) {
            return false;
        }
        return this.bots.contains(entityPlayer);
    }

    private boolean nameStartsWith(EntityPlayer player, String prefix) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(player.getDisplayName().getUnformattedText()).startsWith(prefix);
    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    private enum Mode {
        TAB,
        HYPIXEL
    }

}

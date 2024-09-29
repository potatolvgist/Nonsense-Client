package wtf.bhopper.nonsense.module;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Module {

    public static final Minecraft mc = Minecraft.getMinecraft();

    public final String name;
    public final String displayName;
    public final String description;
    public final Category category;

    private boolean toggled = false;
    private int bind = Keyboard.KEY_NONE;
    private boolean hidden;

    private final List<Setting<?>> settings = new CopyOnWriteArrayList<>();

    public Module(String displayName, String description, Category category) {
        this.name = displayName.replace(" ", "");
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.hidden = category == Category.VISUAL; // Hide Visuals by default
    }

    public void toggle(boolean toggled) {
        if (this.toggled == toggled) {
            return;
        }

        this.toggled = toggled;

        if (this.toggled) {
            this.onEnable();
            Nonsense.INSTANCE.eventBus.subscribe(this);
        } else {
            this.onDisable();
            Nonsense.INSTANCE.eventBus.unsubscribe(this);
        }
    }

    public void toggle() {
        this.toggle(!this.toggled);
    }

    public boolean isEnabled() {
        return this.toggled;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public int getBind() {
        return this.bind;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    protected void addSettings(Setting<?>... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public void onEnable() {}
    public void onDisable() {}

    public String getSuffix() {
        return null;
    }

    public enum Category {
        COMBAT("Combat", 0xFFFF5555, EnumChatFormatting.RED),
        MOVEMENT("Movement", 0xFF55FF55, EnumChatFormatting.GREEN),
        PLAYER("Player", 0xFFAA55AA, EnumChatFormatting.DARK_PURPLE),
        EXPLOIT("Exploit", 0xFF55AAFF, EnumChatFormatting.AQUA),
        OTHER("Other", 0xFFFFAA00, EnumChatFormatting.GOLD),
        VISUAL("Visual", 0xFF0000AA, EnumChatFormatting.DARK_BLUE);

        public final String name;
        public final int color;
        public final EnumChatFormatting chatFormat;

        Category(String name, int color, EnumChatFormatting chatFormat) {
            this.name = name;
            this.color = color;
            this.chatFormat = chatFormat;
        }
    }

}

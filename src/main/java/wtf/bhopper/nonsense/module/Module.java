package wtf.bhopper.nonsense.module;

import com.google.gson.JsonObject;
import net.minecraft.util.EnumChatFormatting;
import org.lwjglx.input.Keyboard;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.misc.JsonUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Module implements MinecraftInstance {

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
            return; // Avoid triggering onEnable/onDisable when not needed
        }

        this.toggled = toggled;

        if (this.toggled) {
            Nonsense.INSTANCE.eventBus.subscribe(this);
            this.onEnable();
        } else {
            Nonsense.INSTANCE.eventBus.unsubscribe(this);
            this.onDisable();
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

    public Setting<?> getSetting(String name) {
        return this.settings.stream()
                .filter(setting -> setting.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void serialize(JsonObject object) {
        JsonObject moduleObject = new JsonObject();
        moduleObject.addProperty("toggled", this.toggled);
        moduleObject.addProperty("bind", this.bind);
        moduleObject.addProperty("hidden", this.hidden);

        JsonObject settingsObject = new JsonObject();
        for (Setting<?> setting : this.settings) {
            setting.serialize(settingsObject);
        }

        moduleObject.add("settings", settingsObject);
        object.add(this.name, moduleObject);
    }

    public void deserialize(JsonObject object) {
        JsonUtil.getSafe(object, this.name, element -> {
            JsonObject moduleObject = element.getAsJsonObject();
            JsonUtil.getSafe(moduleObject, "toggled", toggled -> this.toggle(toggled.getAsBoolean()));
            JsonUtil.getSafe(moduleObject, "bind", bind -> this.setBind(bind.getAsInt()));
            JsonUtil.getSafe(moduleObject, "hidden", hidden -> this.setHidden(hidden.getAsBoolean()));

            JsonUtil.getSafe(moduleObject, "settings", settingsElement -> {
                JsonObject settingsObject = settingsElement.getAsJsonObject();
                for (Setting<?> setting : this.settings) {
                    setting.deserialize(settingsObject);
                }
            });
        });
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

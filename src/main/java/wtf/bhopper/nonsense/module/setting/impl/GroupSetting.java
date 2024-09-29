package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroupSetting extends Setting<List<Setting<?>>> {

    private final Module owner;
    private final List<Setting<?>> settings = new CopyOnWriteArrayList<>();

    public GroupSetting(String displayName, String description, Module owner) {
        super(displayName, description);
        this.owner = owner;
    }

    public Module getOwner() {
        return this.owner;
    }

    public Setting<?> getSetting(String name) {
        for (Setting<?> setting : settings) {
            if (setting.name.equalsIgnoreCase(name)) return setting;
        }

        return null;
    }

    public <T> Setting<T> getSetting(String name, Class<T> clazz) {
        for (Setting<?> setting : settings) {
            if (setting.name.equalsIgnoreCase(name) && setting.get().getClass() == clazz) return (Setting<T>)setting;
        }

        return null;
    }

    public void add(Setting<?>... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    @Override
    public List<Setting<?>> get() {
        return this.settings;
    }

    @Override
    public void set(List<Setting<?>> value) {
        // Does nothing
    }

    @Override
    public void parseString(String str) {
        // Also does nothing
    }

    @Override
    public String getDisplayValue() {
        return "HELP!";
    }

    @Override
    public void serialize(JsonObject object) {
        JsonObject newObject = new JsonObject();
        for (Setting<?> setting : this.settings) {
            setting.serialize(newObject);
        }
        object.add(this.name, newObject);
    }

    @Override
    public void deserialize(JsonObject object) {
        try {
            JsonObject parseObject = object.get(this.name).getAsJsonObject();
            for (Setting<?> setting : settings) {
                setting.deserialize(parseObject);
            }
        } catch (NullPointerException | UnsupportedOperationException | IllegalStateException ignored) {}
    }
}

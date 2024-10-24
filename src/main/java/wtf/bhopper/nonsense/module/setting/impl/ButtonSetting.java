package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.module.setting.Setting;

public class ButtonSetting extends Setting<Void> {

    private final Runnable onClick;

    public ButtonSetting(String displayName, String description, Runnable onClick) {
        super(displayName, description);
        this.onClick = onClick;
    }

    public void execute() {
        this.onClick.run();
    }

    @Override
    public Void get() {
        return null;
    }

    @Override
    public void set(Void value) {
        // does nothing
    }

    @Override
    public void parseString(String str) {
        // does nothing
    }

    @Override
    public String getDisplayValue() {
        return this.displayName;
    }

    @Override
    public void serialize(JsonObject object) {
        // does nothing
    }

    @Override
    public void deserialize(JsonObject object) {
        // does nothing
    }
}

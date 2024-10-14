package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.util.misc.JsonUtil;

public class StringSetting extends Setting<String> {

    private String value;
    private final int maxLength;

    public StringSetting(String displayName, String description, String defaultValue) {
        this(displayName, description, defaultValue, -1);
    }

    public StringSetting(String displayName, String description, String defaultValue, int maxLength) {
        super(displayName, description);
        this.value = defaultValue;
        this.maxLength = maxLength;
    }

    @Override
    public String get() {
        return this.value;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public int length() {
        return this.value.length();
    }

    @Override
    public void set(String value) {
        this.value = value;
        if (this.maxLength != -1 && this.value.length() > this.maxLength) {
            this.value = this.value.substring(0, this.maxLength);
        }
    }

    public void append(char c) {
        this.set(this.value + c);
    }

    public void append(String str) {
        this.set(this.value + str);
    }

    public void backspace() {
        if (!this.value.isEmpty()) {
            if (this.value.length() == 1) {
                this.value = "";
            } else {
                this.value = this.value.substring(0, this.value.length() - 1);
            }
        }
    }

    @Override
    public void parseString(String str) {
        this.value = str;
    }

    @Override
    public String getDisplayValue() {
        return this.value;
    }

    @Override
    public void serialize(JsonObject object) {
        object.addProperty(this.name, this.value);
    }

    @Override
    public void deserialize(JsonObject object) {
        JsonUtil.getSafe(object, this.name, element -> this.set(element.getAsString()));
    }
}

package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NBTTagCompound;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.util.JsonUtil;

public class BooleanSetting extends Setting<Boolean> {

    private boolean value;

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description);
        this.value = defaultValue;
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void set(Boolean value) {
        this.value = value;
    }

    public void toggle() {
        value = !value;
    }

    @Override
    public void parseString(String str) {
        this.value = str.equalsIgnoreCase("true") || str.equalsIgnoreCase("1");
    }

    @Override
    public String getDisplayValue() {
        return value ? "True" : "False";
    }

    @Override
    public void serialize(JsonObject object) {
       object.addProperty(this.name, this.value);
    }

    @Override
    public void deserialize(JsonObject object) {
        JsonUtil.getSafe(object, this.name, element -> this.set(element.getAsBoolean()));
    }
}

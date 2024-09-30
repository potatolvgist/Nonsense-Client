package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NBTTagCompound;
import wtf.bhopper.nonsense.util.JsonUtil;

import java.text.DecimalFormat;

public class FloatSetting extends NumberSetting<Float> {

    private float value;
    public final float min, max;
    public DecimalFormat format;

    private final ChangedCallback<Float> changedCallback;

    public FloatSetting(String name, String description, float min, float max, float defaultValue, DecimalFormat format, ChangedCallback<Float> changedCallback) {
        super(name, description);

        if (min > max) throw new IllegalArgumentException("Minimum value must be smaller than maximum value");

        this.min = min;
        this.max = max;
        this.value = defaultValue;
        this.format = format;
        this.changedCallback = changedCallback;
    }

    public FloatSetting(String name, String description, float min, float max, float defaultValue) {
        this(name, description, min, max, defaultValue, DEFAULT_FORMAT, null);
    }

    @Override
    public Float get() {
        return value;
    }

    @Override
    public void set(Float value) {
        this.value = value;
        if (this.value < min) {
            this.value = min;
        } else if (this.value > max) {
            this.value = max;
        }
        if (this.changedCallback != null) {
            this.changedCallback.onChanged(value);
        }
    }

    @Override
    public void parseString(String str) {
        try {
            this.value = Float.parseFloat(str);
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public String getDisplayValue() {
        return format.format(value);
    }

    @Override
    public void serialize(JsonObject object) {
        object.addProperty(this.name, this.value);
    }

    @Override
    public void deserialize(JsonObject object) {
        JsonUtil.getSafe(object, this.name, element -> this.set(element.getAsFloat()));
    }

    @Override
    public float getPercent() {
        return value / (max - min);
    }

    @Override
    public void setFromPercent(float f) {
        float range = this.max - this.min;
        this.set(this.min + range * f);
    }

    @Override
    public float minF() {
        return min;
    }

    @Override
    public float maxF() {
        return max;
    }
}

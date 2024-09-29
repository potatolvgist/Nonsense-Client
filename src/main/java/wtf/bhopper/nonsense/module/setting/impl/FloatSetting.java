package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;

public class FloatSetting extends NumberSetting<Float> {

    private float value;
    public final float min, max;
    public DecimalFormat format;

    public FloatSetting(String name, String description, float min, float max, float defaultValue, DecimalFormat format) {
        super(name, description);

        if (min > max) throw new IllegalArgumentException("Minimum value must be smaller than maximum value");

        this.min = min;
        this.max = max;
        this.value = defaultValue;
        this.format = format;
    }

    public FloatSetting(String name, String description, float min, float max, float defaultValue) {
        this(name, description, min, max, defaultValue, DEFAULT_DECIMAL_FORMAT);
    }

    @Override
    public Float get() {
        return value;
    }

    @Override
    public void set(Float value) {
        this.value = value;
        if (this.value < min) this.value = min;
        else if (this.value > max) this.value = max;
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
        this.value = object.get(this.name).getAsFloat();
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

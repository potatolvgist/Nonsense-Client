package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.util.misc.JsonUtil;

public class IntSetting extends NumberSetting<Integer> {

    private int value;
    public final int min, max;
    public final String format;

    private final ChangedCallback<Integer> changedCallback;

    public IntSetting(String name, String description, int min, int max, int defaultValue, String format, ChangedCallback<Integer> changedCallback) {
        super(name, description);

        if (min > max) throw new IllegalArgumentException("Minimum value must be smaller than maximum value");

        this.min = min;
        this.max = max;
        this.value = defaultValue;
        this.format = format;
        this.changedCallback = changedCallback;
    }

    public IntSetting(String name, String description, int min, int max, int defaultValue) {
        this(name, description, min, max, defaultValue, "%d", null);
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void set(Integer value) {
        this.value = value;
        if (this.value < min) {
            this.value = min;
        } else if (this.value > max) {
            this.value = max;
        }
        if (this.changedCallback != null) {
            this.changedCallback.onChanged(this.value);
        }
    }

    @Override
    public void parseString(String str) {
        try {
            this.value = Integer.parseInt(str);
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public String getDisplayValue() {
        return String.format(format, value);
    }

    @Override
    public void serialize(JsonObject object) {
        object.addProperty(this.name, this.value);
    }

    @Override
    public void deserialize(JsonObject object) {
        JsonUtil.getSafe(object, this.name, element -> this.set(element.getAsInt()));
    }


    @Override
    public float getF() {
        return (float)this.value;
    }

    @Override
    public void setF(float f) {
        this.set(Math.round(f));
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

package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.module.setting.util.Description;
import wtf.bhopper.nonsense.module.setting.util.DisplayName;
import wtf.bhopper.nonsense.util.JsonUtil;
import wtf.bhopper.nonsense.util.GeneralUtil;

import java.util.HashMap;
import java.util.Map;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {

    private final T[] values;
    private T currentValue;

    private final ChangedCallback<T> changedCallback;

    public EnumSetting(String displayName, String description, T defaultValue) {
        super(displayName, description);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
        this.currentValue = defaultValue;
        this.changedCallback = null;
    }

    public EnumSetting(String displayName, String description, T defaultValue, ChangedCallback<T> changedCallback) {
        super(displayName, description);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
        this.currentValue = defaultValue;
        this.changedCallback = changedCallback;
    }

    public void updateChange() {
        if (this.changedCallback != null) {
            this.changedCallback.onChanged(this.currentValue);
        }
    }

    public void set(T value) {
        this.currentValue = value;
        this.updateChange();
    }

    public void cycleForwards() {
        int index = currentValue.ordinal() + 1;
        if (index >= values.length) index = 0;
        this.set(values[index]);
    }

    public void cycleBackwards() {
        int index = currentValue.ordinal() - 1;
        if (index < 0) index = values.length - 1;
        this.set(values[index]);
    }

    public boolean is(T value) {
        return this.currentValue == value;
    }

    public boolean isAny(T... values) {
        for (T value : values) {
            if (this.currentValue == value) return true;
        }

        return false;
    }

    public T get() {
        return this.currentValue;
    }

    public T[] getValues() {
        return this.values;
    }

    public Map<T, String> valueNameMap() {
        Map<T, String> map = new HashMap<>();
        for (T t : values) {
            map.put(t, toDisplay(t));
        }
        return map;
    }

    @Override
    public void parseString(String str) {
        try {
            for (T value : values) {
                if (value.name().equalsIgnoreCase(str)) {
                    currentValue = value;
                    break;
                }
            }
        } catch (NullPointerException ignored) {}
    }


    @Override
    public String getDisplayValue() {
        return toDisplay(currentValue);
    }

    public String getValueDescription() {
        return getEnumDescription(this.currentValue);
    }

    @Override
    public void serialize(JsonObject object) {
        object.addProperty(this.name, this.currentValue.name());
    }

    @Override
    public void deserialize(JsonObject object) {
        JsonUtil.getSafe(object, this.name, element -> {
            String valueStr = element.getAsString();
            for (T value : values) {
                if (value.name().equalsIgnoreCase(valueStr)) {
                    currentValue = value;
                    break;
                }
            }
        });
    }

    public static <E extends Enum<?>> String toDisplay(E e) {

        try {
            if (e.getClass().getField(e.name()).isAnnotationPresent(DisplayName.class)) {
                return e.getClass().getField(e.name()).getAnnotation(DisplayName.class).value();
            }
        } catch (NoSuchFieldException | NullPointerException ignored) {}

        return toDisplay(e.name());

    }

    public static String toDisplay(String str) {
        return GeneralUtil.capitalize(str.replace('_', ' ').toLowerCase());
    }

    public static <E extends Enum<E>> String getEnumDescription(E e) {
        try {
            if (e.getClass().getField(e.name()).isAnnotationPresent(Description.class))  {
                return e.getClass().getField(e.name()).getAnnotation(Description.class).value();
            }
        } catch (NoSuchFieldException | NullPointerException ignored) {}

        return null;
    }

}

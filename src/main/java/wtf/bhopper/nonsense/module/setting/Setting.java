package wtf.bhopper.nonsense.module.setting;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;

public abstract class Setting<T> {

    public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#0.##");

    public final String name;
    public final String displayName;
    public final String description;

    private boolean displayed = true;

    public Setting(String displayName, String description) {
        this.name = displayName.toLowerCase().replace(' ', '-');
        this.displayName = displayName;
        this.description = description;
    }

    public abstract T get();

    public abstract void set(T value);

    public abstract void parseString(String str);

    public abstract String getDisplayValue();

    public abstract void serialize(JsonObject object);
    public abstract void deserialize(JsonObject object);

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isDisplayed() {
        return this.displayed;
    }
}

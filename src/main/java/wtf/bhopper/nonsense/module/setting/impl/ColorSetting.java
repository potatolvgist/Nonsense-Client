package wtf.bhopper.nonsense.module.setting.impl;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NBTTagCompound;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.util.JsonUtil;

import java.awt.*;

public class ColorSetting extends Setting<Color> {

    private Color color;

    public ColorSetting(String displayName, String description, Color color) {
        super(displayName, description);
        this.color = color;
    }

    public ColorSetting(String displayName, String description, int color) {
        super(displayName, description);
        this.color = new Color(color);
    }

    @Override
    public Color get() {
        return this.color;
    }

    public int getRgb() {
        return color.getRGB();
    }

    public float[] getHsb() {
        return Color.RGBtoHSB(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), null);
    }

    @Override
    public void set(Color value) {
        this.color = value;
    }

    public void set(int color) {
        this.color = new Color(color);
    }

    public void set(int red, int green, int blue, int alpha) {
        this.color = new Color(red, green, blue, alpha);
    }

    public void set(float hue, float saturation, float brightness, float alpha) {
        this.color = new Color(Color.HSBtoRGB(hue, saturation, brightness) | (int)(alpha * 255.0F) << 24);
    }

    @Override
    public void parseString(String str) {
        int parsed = Integer.parseInt(str, 16); // Parse as a hex string
        this.color = new Color(parsed);
    }

    @Override
    public String getDisplayValue() {
        return Integer.toHexString(color.getRGB());
    }

    @Override
    public void serialize(JsonObject object) {
        object.addProperty(this.name, this.color.getRGB());
    }

    @Override
    public void deserialize(JsonObject object) {
        JsonUtil.getSafe(object, this.name, element -> this.set(element.getAsInt()));
    }
}

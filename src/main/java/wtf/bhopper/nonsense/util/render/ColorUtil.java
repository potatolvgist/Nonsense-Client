package wtf.bhopper.nonsense.util.render;

import java.awt.*;

public class ColorUtil {

    private static final float RISE_FACTOR = 5.0F / 12.0F;

    public static int rainbowColor(long timeMS, int count, float saturation, float brightness) {
        float hue = (float) ((timeMS - count * 200L) % 4000) / 4000.0F;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int wavyColor(int color, long timeMS, int count) {
        float factor = Math.abs((((timeMS * 2L) - count * 500L) % 4000) / 4000.0F - 0.5F) + 0.5F;
        Color awt = new Color(color);
        float[] hsb = Color.RGBtoHSB(awt.getRed(), awt.getGreen(), awt.getBlue(), null);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * factor);
    }

    public static int astolfoColor(long timeMS, int count) {
        float hue = (Math.abs((((timeMS * 2L) - count * 500L) % 8000) / 8000.0F) - 0.5F) + 0.5F;
        return Color.HSBtoRGB(hue, 0.5F, 1.0F);
    }

    public static int riseColor(long timeMS, int count) {
        float normalizedTime = (float) ((timeMS - count * 200L) % 2000) / 2000.0F;
        float hue = (Math.abs(normalizedTime - 0.5F) / 3.0F) + RISE_FACTOR;
        return Color.HSBtoRGB(hue, 1.0F, 1.0F);
    }

    public static int darken(int color) {
        return new Color(color).darker().getRGB();
    }

    public static int brighten(int color) {
        return new Color(color).brighter().getRGB();
    }

    public static int darken(int color, int amount) {
        Color awt = new Color(color);
        for (int i = 0; i < amount; i++) {
            awt = awt.darker();
        }
        return awt.getRGB();
    }

    public static int brighten(int color, int amount) {
        Color awt = new Color(color);
        for (int i = 0; i < amount; i++) {
            awt = awt.brighter();
        }
        return awt.getRGB();
    }

}

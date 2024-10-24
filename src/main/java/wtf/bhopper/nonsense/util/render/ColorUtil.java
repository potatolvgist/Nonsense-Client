package wtf.bhopper.nonsense.util.render;

import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.awt.*;

public class ColorUtil {

    public static final int BLACK = Color.BLACK.getRGB();
    public static final int RED = Color.RED.getRGB();

    private static final float RISE_FACTOR = 5.0F / 12.0F;

    public static int rainbow(long timeMS, int count, float saturation, float brightness) {
        float hue = (float) ((timeMS - count * 200L) % 4000) / 4000.0F;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int wave(int color, long timeMS, int count) {
        float factor = Math.abs((((timeMS * 2L) - count * 500L) % 8000) / 8000.0F - 0.5F) + 0.5F;
        Color awt = new Color(color);
        float[] hsb = Color.RGBtoHSB(awt.getRed(), awt.getGreen(), awt.getBlue(), null);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * factor);
    }

    public static int astolfo(long timeMS, int count) {
        float hue = Math.abs(((((timeMS * 2L) - count * 500L) % 8000) / 8000.0F) - 0.5f) + 0.5F;
        return Color.HSBtoRGB(hue, 0.5F, 1.0F);
    }

    public static int rise(long timeMS, int count) {
        float normalizedTime = (float) ((timeMS - count * 200L) % 2000) / 2000.0F;
        float hue = (Math.abs(normalizedTime - 0.5F) / 3.0F) + RISE_FACTOR;
        return Color.HSBtoRGB(hue, 1.0F, 1.0F);
    }

    public static int health(float factor) {
        return Color.HSBtoRGB(factor / 3.0F, 1.0F, 1.0F);
    }

    public static int health(float health, float maxHealth) {
        return health(health / maxHealth);
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

    public static int lerp(int left, int right, float factor) {
        int[] rgbLeft = separate(left);
        int[] rgbRight = separate(right);

        int r = MathUtil.lerp(rgbLeft[0], rgbRight[0], factor);
        int g = MathUtil.lerp(rgbLeft[1], rgbRight[1], factor);
        int b = MathUtil.lerp(rgbLeft[2], rgbRight[2], factor);
        int a = MathUtil.lerp(rgbLeft[3], rgbRight[3], factor);

        return merge(r, g, b, a);
    }

    public static Color lerp(Color left, Color right, float factor) {

        int r = MathUtil.lerp(left.getRed(), right.getRed(), factor);
        int g = MathUtil.lerp(left.getGreen(), right.getGreen(), factor);
        int b = MathUtil.lerp(left.getBlue(), right.getBlue(), factor);
        int a = MathUtil.lerp(left.getAlpha(), right.getAlpha(), factor);

        return new Color(r, g, b, a);
    }

    public static int[] separate(int color) {
        return new int[]{
                    (color >> 16) & 0xFF,
                    (color >> 8) & 0xFF,
                    color & 0xFF,
                    (color >> 24) & 0xFF
        };
    }

    public static float[] separateF(int color) {
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F,
                ((color >> 24) & 0xFF) / 255.0F
        };
    }

    public static int merge(int r, int g, int b, int a) {
        return  ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                (b & 0xFF);
    }

}

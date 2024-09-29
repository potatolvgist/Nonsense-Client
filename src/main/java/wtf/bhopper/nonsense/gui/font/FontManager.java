package wtf.bhopper.nonsense.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FontManager {

    private final TTFFontRenderer defaultFont;

    public TTFFontRenderer getFont(Fonts font, int size) {
        return fonts.getOrDefault(font.getKey(size), this.defaultFont);
    }

    private final Map<String, TTFFontRenderer> fonts = new HashMap<>();

    public FontManager() {
        defaultFont = new TTFFontRenderer(new Font("Arial", Font.PLAIN, 18));
        try {

            for (Fonts font : Fonts.values()) {
                for (int size : font.sizes) {
                    fonts.put(font.getKey(size), new TTFFontRenderer(font.load(size)));
                }
            }

        } catch (Exception ignored) {}
    }

    public static FontRenderer minecraft() {
        return Minecraft.getMinecraft().fontRendererObj;
    }

    public static FontRenderer bit() {
        return Minecraft.getMinecraft().bitFontRenderer;
    }


}

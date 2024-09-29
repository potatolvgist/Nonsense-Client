package wtf.bhopper.nonsense.gui.clickgui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.font.Fonts;
import wtf.bhopper.nonsense.gui.font.TTFFontRenderer;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.impl.visual.ClickGuiMod;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {

    public static final int WIDTH = 220;

    private static final List<ClickGuiPanel> PANELS = new ArrayList<>();

    private static ClickGuiMod module;
    private static TTFFontRenderer font;

    private static long time = 0L;

    public static String toolTip = null;

    public static void init() {
        font = Nonsense.INSTANCE.fontManager.getFont(Fonts.SEGOE, 16);
        module = Nonsense.INSTANCE.moduleManager.get(ClickGuiMod.class);
        for (Module.Category category : Module.Category.values()) {
            PANELS.add(new ClickGuiPanel(category));
        }
    }

    public static ClickGuiMod mod() {
        return module;
    }

    public static TTFFontRenderer font() {
        return font;
    }

    public static float stringWidth(String text) {
        return font.getStringWidth(text) * 2.0F;
    }

    public static float stringHeight(String text) {
        return font.getHeight(text) * 2.0F;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        time = System.currentTimeMillis();
        toolTip = null;

        int mx = Math.round((float)mouseX * mc.gameSettings.guiScale);
        int my = Math.round((float)mouseY * mc.gameSettings.guiScale);

        if (module.background.is(ClickGuiMod.Background.DARKEN)) {
            this.drawDefaultBackground();
        }

        Hud.beginDraw(scaledResolution);
        PANELS.forEach(panel -> panel.draw(mx, my, partialTicks));

        if (module.toolTips.get() && toolTip != null) {
            String[] parts = toolTip.split("\n");

            for (int i = 0; i < parts.length; i++) {
                String line = parts[i];
                Gui.drawRect(mx + 3, my - 6 + (i * ((int)font.getHeight(line) * 2 + 3)), mx + 6 + (int)font.getStringWidth(line) * 2, my - 3 + (int)font.getHeight(line) * 2 + (i * ((int)font.getHeight(line) * 2 + 3)), 0x77000000);
                drawString(line, mx + 5, my - 5 + (i * (font.getHeight(line) + 3)), 0xFFFFFFFF);
            }
        }

        Hud.endDraw();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int mx = Math.round((float)mouseX * mc.gameSettings.guiScale);
        int my = Math.round((float)mouseY * mc.gameSettings.guiScale);
        PANELS.forEach(panel -> panel.mouseClicked(mx, my, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int mx = Math.round((float)mouseX * mc.gameSettings.guiScale);
        int my = Math.round((float)mouseY * mc.gameSettings.guiScale);
        PANELS.forEach(panel -> panel.mouseReleased(mx, my, state));
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        int mx = Math.round((float)mouseX * mc.gameSettings.guiScale);
        int my = Math.round((float)mouseY * mc.gameSettings.guiScale);
        PANELS.forEach(panel -> panel.mouseClickMove(mx, my, clickedMouseButton, timeSinceLastClick));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        PANELS.forEach(panel -> panel.keyTyped(typedChar, keyCode));
    }

    public static void drawString(String text, float x, float y, int color) {
        // Strings need to be scaled up by 2 when drawn
        GlStateManager.scale(2.0F, 2.0F, 0.0F);
        font.drawStringWithShadow(text, Math.round(x / 2.0F), Math.round(y / 2.0F), color);
        GlStateManager.scale(0.5F, 0.5F, 0.0F);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public static int getColor(ClickGuiPanel panel) {
        switch (module.mode.get()) {
            case STATIC:
                return module.color.getRgb();

            case CATEGORY:
                return panel.category.color;

            case WAVY:
                return ColorUtil.wavyColor(module.color.getRgb(), ClickGui.time, panel.drawCount);

            case RAINBOW_1:
                return ColorUtil.rainbowColor(ClickGui.time, panel.drawCount, 0.5F, 1.0F);

            case RAINBOW_2:
                return ColorUtil.rainbowColor(ClickGui.time, panel.drawCount, 1.0F, 1.0F);
        }

        // if the the mode is null for some strange reason return -1 (which is just white)
        return -1;
    }

    public static boolean shouldHide(Setting<?> setting) {
        return !setting.isDisplayed() && !module.showHidden.get();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.gui.font.Fonts;
import wtf.bhopper.nonsense.gui.font.TTFFontRenderer;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationManager;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;

import java.util.ArrayList;
import java.util.List;

public class Hud {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static HudMod module;
    public static final ModuleList moduleList = new ModuleList();
    public static final Watermark watermark = new Watermark();
    public static final InfoDisplay infoDisplay = new InfoDisplay();
    public static final NotificationManager notificationManager = new NotificationManager();
    private static final List<RenderComponent> components = new ArrayList<>();

    private static FontRenderer font;
    private static TTFFontRenderer customFont;

    public static void init() {
        module = Nonsense.INSTANCE.moduleManager.get(HudMod.class);
        font = mc.fontRendererObj;
        customFont = Nonsense.INSTANCE.fontManager.getFont(Fonts.ARIAL, 18);

        moduleList.init();
    }

    public static void addComponent(RenderComponent component) {
        components.add(component);
    }

    public static HudMod mod() {
        return module;
    }

    public static boolean enabled() {
        if (!module.isEnabled()) {
            return false;
        }

        if (module.hidef3.get() && mc.gameSettings.showDebugInfo) {
            return false;
        }

        return true;
    }

    public static int color() {
        return module.color.getRgb();
    }

    public static void beginDraw(ScaledResolution sr) {
        float inverseScale = 1.0F / sr.getScaleFactor();
        GlStateManager.pushMatrix();
        GlStateManager.scale(inverseScale, inverseScale, 0.0F);
    }

    public static void endDraw() {
        GlStateManager.popMatrix();
    }

    public static void drawComponents(ScaledResolution res, float delta) {
        float inverseScale = 1.0F / res.getScaleFactor();
        GlStateManager.scale(inverseScale, inverseScale, inverseScale);
        for (RenderComponent component : components) {
            if (component.isEnabled()) {
                component.draw(res, delta);
            }
        }
        GlStateManager.scale(res.getScaleFactor(), res.getScaleFactor(), res.getScaleFactor());
    }

    public static void drawString(String text, float x, float y, int color, boolean shadow) {
        GlStateManager.scale(2.0F, 2.0F, 0.0F);
        if (module.customFont.get()) {
            if (shadow) {
                customFont.drawStringWithShadow(text, (int)Math.floor(x / 2.0F), (int)Math.floor(y / 2.0F), color);
            } else {
                customFont.drawString(text, (int)Math.floor(x / 2.0F), (int)Math.floor(y / 2.0F), color);
            }
        } else {
            font.drawString(text, (int)Math.floor(x / 2.0F), (int)Math.floor(y / 2.0F), color, shadow);
        }

        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F); // Fix a weird rendering bug that happens with the custom fonts
    }

    public static float getStringWidth(String text) {
        if (module.customFont.get()) {
            return customFont.getStringWidth(text) * 2.0F;
        } else {
            return font.getStringWidth(text) * 2.0F;
        }
    }

    public static float getStringHeight(String text) {
        if (module.customFont.get()) {
            return customFont.getHeight(text) * 2.0F;
        } else {
            return font.FONT_HEIGHT * 2.0F;
        }
    }

}

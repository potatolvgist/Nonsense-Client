package wtf.bhopper.nonsense.gui.hud.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.text.DecimalFormat;

public class Notification {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int HEIGHT = 48;
    private static final float FADE_FACTOR = 0.25F;
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#0.00");

    private final String title;
    private final String message;
    private final NotificationType type;
    private final int displayTimeMS;

    private int stage = 0;
    private float positionFactor = 0.0F;
    private final Clock timer = new Clock();

    public Notification(String title, String message, NotificationType type, int displayTimeMS) {
        this.title = "\247l" + title + "\247r \2477- %time%";
        this.message = message;
        this.type = type;
        this.displayTimeMS = displayTimeMS;
        this.timer.reset();
    }

    public int draw(float delta, ScaledResolution res, int offset) {

        int width = (int)Math.max(Hud.getStringWidth(title), Hud.getStringWidth(message)) + HEIGHT + 8;

        if (stage == 0) {
            this.positionFactor += FADE_FACTOR * delta;
            if (this.positionFactor >= 1.0F) {
                this.positionFactor = 1.0F;
                this.stage = 1;
            }
        } else if (stage == 1) {
            if (this.timer.hasReached(this.displayTimeMS)) {
                this.stage = 2;
            }
        } else if (stage == 2) {
            this.positionFactor -= FADE_FACTOR * delta;
            if (this.positionFactor <= 0.0F) {
                this.positionFactor = 0.0F;
                this.stage = 3;
            }
        } else {
            return offset;
        }

        int right = res.getScaledWidth() * res.getScaleFactor();
        int x = res.getScaledWidth() * res.getScaleFactor() - (int)(width * positionFactor);

        String displayTitle = title.replace("%time%", this.displayTimeLeft());

        GlStateManager.enableAlpha();
        Gui.drawRect(x, offset - HEIGHT, right, offset, 0xAA000000);
        GlStateManager.enableBlend();
        GlStateManager.color(this.type.red(), this.type.green(), this.type.blue());
        mc.getTextureManager().bindTexture(this.type.icon);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Gui.drawModalRectWithCustomSizedTexture(x + 8, offset - HEIGHT + 8, 0.0F, 0.0F, 32, 32, 32, 32);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Gui.drawRect(x, offset - 4, x + width, offset, ColorUtil.darken(this.type.color.getRGB(), 3));
        if (this.stage != 2) {
            Gui.drawRect(x, offset - 4, x + (int) ((float) width * this.doneFactor()), offset, this.type.color.getRGB());
        }

        Hud.drawString(displayTitle, x + HEIGHT, offset - HEIGHT + 6, -1, true);
        Hud.drawString(this.message, x + HEIGHT, offset - HEIGHT + 8 + Hud.getStringHeight(displayTitle), 0xFFAAAAAA, true);

        return offset - (int)((HEIGHT + 4) * positionFactor);
    }

    public boolean isDone() {
        return stage == 3;
    }

    private String displayTimeLeft() {
        return TIME_FORMAT.format((float)Math.max(this.displayTimeMS - this.timer.getTime(), 0) / 1000.0F);
    }

    private float doneFactor() {
        return 1.0F - Math.min((float)this.timer.getTime() / (float)this.displayTimeMS, 1.0F);
    }

    public static void send(String title, String message, NotificationType type, int displayTimeMS) {
        Hud.notificationManager.addNotification(new Notification(title, message, type, displayTimeMS));
    }

}

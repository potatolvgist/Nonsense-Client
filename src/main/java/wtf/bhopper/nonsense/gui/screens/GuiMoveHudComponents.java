package wtf.bhopper.nonsense.gui.screens;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.gui.hud.Hud;

import java.io.IOException;

public class GuiMoveHudComponents extends GuiScreen {

    private static final String NAME = "Component Editor";

    private RenderComponent dragging = null;
    private int dragX = 0;
    private int dragY = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        int mx = sr.scaleMouse(mouseX, 2);
        int my = sr.scaleMouse(mouseY, 2);

        if (this.dragging != null) {
            this.dragging.setX(mx - this.dragX);
            this.dragging.setY(my - this.dragY);
        }

        GlStateManager.pushMatrix();
        sr.scaleToFactor(4.0F);
        this.drawCenteredString(mc.fontRendererObj, NAME, this.width / sr.getScaleFactor() / 2, 2, Hud.color());
        GlStateManager.popMatrix();

        Hud.drawComponents(sr, partialTicks, mouseX, mouseY, true);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (mouseButton != 0) {
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int mx = sr.scaleMouse(mouseX, 2);
        int my = sr.scaleMouse(mouseY, 2);
        for (RenderComponent component : Hud.getComponents()) {
            if (component.mouseIntersecting(mx, my)) {
                this.dragging = component;
                this.dragX = mx - component.getX();
                this.dragY = my - component.getY();
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

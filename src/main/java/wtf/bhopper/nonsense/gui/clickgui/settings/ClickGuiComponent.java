package wtf.bhopper.nonsense.gui.clickgui.settings;

import net.minecraft.client.gui.Gui;
import wtf.bhopper.nonsense.gui.clickgui.ClickGui;
import wtf.bhopper.nonsense.gui.clickgui.ClickGuiPanel;

import java.io.IOException;

public abstract class ClickGuiComponent {

    public static final int HEIGHT = 24;

    protected final ClickGuiPanel panel;
    protected int x, y;
    protected final boolean inGroup;

    public ClickGuiComponent(ClickGuiPanel panel, boolean inGroup) {
        this.panel = panel;
        this.inGroup = inGroup;
    }

    protected void updatePosition() {
        this.x = this.panel.getX();
        this.y = this.panel.drawY;
        this.panel.drawCount++;
    }

    protected void drawBackground() {
        this.drawBackground(HEIGHT);
    }

    protected void drawBackground(int height) {
        Gui.drawRect(this.x, this.y, this.x + ClickGui.WIDTH, this.y + height + 3, 0xFF111111);
        Gui.drawRect(this.x + 3, this.y, this.x + ClickGui.WIDTH - 3, this.y + height + 3, 0xFF171717);
        if (this.inGroup) {
            Gui.drawRect(this.x + 5, this.y, this.x + ClickGui.WIDTH - 5, this.y + height + 3, 0xFF0D0D0D);
        }
    }

    protected boolean mouseIntersecting(int mouseX, int mouseY) {
        return mouseIntersecting(mouseX, mouseY, this.x, this.y, this.x + ClickGui.WIDTH, this.y + HEIGHT);
    }

    protected boolean mouseIntersecting(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        return mouseX > left && mouseX < right && mouseY > top && mouseY < bottom;
    }

    public abstract void draw(int mouseX, int mouseY, float partialTicks);
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
    public void keyTyped(char typedChar, int keyCode) {}
    public void mouseReleased(int mouseX, int mouseY, int state) {}
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}

}

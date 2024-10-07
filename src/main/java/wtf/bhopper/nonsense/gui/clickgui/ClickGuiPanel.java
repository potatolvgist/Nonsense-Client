package wtf.bhopper.nonsense.gui.clickgui;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiPanel {

    public static final int HEIGHT = 40;

    public final Module.Category category;

    private final List<ClickGuiModuleButton> moduleButtons = new ArrayList<>();

    private int x, y;

    private boolean expanded = false;
    private boolean dragging = false;
    private int dragX, dragY;

    public int drawY = 0;
    public int drawCount = 0;

    public ClickGuiPanel(Module.Category category) {
        this.category = category;
        this.x = 5 + category.ordinal() * (ClickGui.WIDTH + 5);
        this.y = 5;
        for (Module module : Nonsense.INSTANCE.moduleManager.getInCategory(category)) {
            moduleButtons.add(new ClickGuiModuleButton(this, module));
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {

        if (this.dragging) {
            this.x = mouseX - this.dragX;
            this.y = mouseY - this.dragY;
        }

        this.drawY = this.y;
        this.drawCount = 0;

        String name = EnumChatFormatting.BOLD + this.category.name;

        Gui.drawRect(this.x, this.y - 1, this.x + ClickGui.WIDTH, this.y + HEIGHT, 0xFF111111);
        ClickGui.drawString(name, this.x + 8, this.y + 8, -1);
        this.drawY += HEIGHT;
        if (expanded) {
            this.moduleButtons.forEach(button -> button.draw(mouseX, mouseY, partialTicks));
        }
        Gui.drawRect(this.x, this.drawY, this.x + ClickGui.WIDTH, this.drawY + 3, 0xFF111111);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (this.mouseIntersecting(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.dragX = mouseX - this.x;
                this.dragY = mouseY - this.y;
                this.dragging = true;
            } else if (mouseButton == 1) {
                this.expanded = !this.expanded;
            }
        } else if (this.expanded) {
            this.moduleButtons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
        if (this.expanded) {
            this.moduleButtons.forEach(button -> button.mouseReleased(mouseX, mouseY, state));
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.expanded) {
            this.moduleButtons.forEach(button -> button.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick));
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (this.expanded) {
            this.moduleButtons.forEach(button -> button.keyTyped(typedChar, keyCode));
        }
    }

    public int getX() {
        return this.x;
    }

    public void offsetY(int y) {
        this.y += y;
    }

    private boolean mouseIntersecting(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + ClickGui.WIDTH && mouseY > y && mouseY < y + HEIGHT;
    }
}

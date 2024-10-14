package wtf.bhopper.nonsense.gui.clickgui.dropdown.settings;

import net.minecraft.client.gui.Gui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownPanel;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;

public class DropdownSwitch extends DropdownComponent {

    private final BooleanSetting setting;

    public DropdownSwitch(DropdownPanel panel, BooleanSetting setting, boolean inGroup) {
        super(panel, inGroup);
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {

        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        this.updatePosition();
        this.drawBackground();

        int rectWidth = this.x + DropdownClickGui.WIDTH - (inGroup ? 9 : 5);
        int rectHeight = y + HEIGHT - 2;
        int switchBgColor = this.inGroup ? 0xFF070707 : 0xFF111111;
        int switchColor = setting.get() ? DropdownClickGui.getColor(this.panel) : 0xFF555555;
        String name = DropdownClickGui.mod().displayNames.get() ? setting.displayName : setting.name;

        Gui.drawRect(rectWidth - 2 * (HEIGHT - 4), y + 2, rectWidth, rectHeight, switchBgColor);
        Gui.drawRect(rectWidth - HEIGHT + 4, y + 2, setting.get() ? rectWidth : rectWidth - 2 * (HEIGHT - 4), rectHeight, switchColor);

        DropdownClickGui.drawString(name, this.x + (this.inGroup ? 18 : 14), this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(name), -1);
        if (this.mouseIntersecting(mouseX, mouseY)) {
            DropdownClickGui.toolTip = this.setting.description;
        }

        this.panel.drawY += HEIGHT;

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        if (mouseIntersecting(mouseX, mouseY)) {
            if (mouseButton == 0) {
                setting.set(!setting.get());
            }
        }
    }
}

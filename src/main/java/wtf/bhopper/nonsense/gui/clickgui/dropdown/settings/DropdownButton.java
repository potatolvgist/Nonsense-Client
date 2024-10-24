package wtf.bhopper.nonsense.gui.clickgui.dropdown.settings;

import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownPanel;
import wtf.bhopper.nonsense.module.setting.impl.ButtonSetting;
import wtf.bhopper.nonsense.util.render.RenderUtil;

public class DropdownButton extends DropdownComponent {

    private final ButtonSetting setting;

    public DropdownButton(DropdownPanel panel, ButtonSetting setting, boolean inGroup) {
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

        int xOffset = this.inGroup ? 9 : 5;

        RenderUtil.drawCircleRect(this.x + xOffset, this.y + 2, this.x + DropdownClickGui.WIDTH - xOffset, this.y + HEIGHT - 2, 4, this.inGroup ? 0xFF070707 : 0xFF111111);

        String name = DropdownClickGui.mod().displayNames.get() ? setting.displayName : setting.name;
        float nameX = Math.round(this.x + DropdownClickGui.WIDTH / 2.0F - DropdownClickGui.stringWidth(name) / 2.0F);
        float nameY = Math.round(this.y + HEIGHT / 2.0F - DropdownClickGui.stringHeight(name) / 2.0F);
        DropdownClickGui.drawString(name, nameX, nameY, DropdownClickGui.getColor(this.panel));

        this.panel.drawY += HEIGHT;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        if (mouseIntersecting(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.setting.execute();
            }
        }
    }
}

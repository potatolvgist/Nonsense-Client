package wtf.bhopper.nonsense.gui.clickgui.dropdown.settings;

import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownPanel;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;

public class DropdownModeSelector extends DropdownComponent {

    private final EnumSetting<?> setting;

    public DropdownModeSelector(DropdownPanel panel, EnumSetting<?> setting, boolean inGroup) {
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

        String name = DropdownClickGui.mod().displayNames.get() ? setting.displayName : setting.name;
        String value = DropdownClickGui.mod().displayNames.get() ? setting.getDisplayValue() : setting.get().name();

        DropdownClickGui.drawString(name, this.x + (this.inGroup ? 18 : 14), this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(name), -1);
        DropdownClickGui.drawString(value, this.x + DropdownClickGui.WIDTH - DropdownClickGui.font().getStringWidth(value) * 2.0F - (inGroup ? 16 : 12), this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(name), -1);

        if (this.mouseIntersecting(mouseX, mouseY)) {
            DropdownClickGui.toolTip = this.setting.description;
            String enumDesc = this.setting.getValueDescription();
            if (enumDesc != null) {
                DropdownClickGui.toolTip += "\n" + enumDesc;
            }
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
                setting.cycleForwards();
            } else if (mouseButton == 1) {
                setting.cycleBackwards();
            }
        }
    }
}

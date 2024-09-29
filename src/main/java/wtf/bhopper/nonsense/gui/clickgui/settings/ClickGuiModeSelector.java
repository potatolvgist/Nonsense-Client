package wtf.bhopper.nonsense.gui.clickgui.settings;

import wtf.bhopper.nonsense.gui.clickgui.ClickGui;
import wtf.bhopper.nonsense.gui.clickgui.ClickGuiPanel;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;

public class ClickGuiModeSelector extends ClickGuiComponent {

    private final EnumSetting<?> setting;

    public ClickGuiModeSelector(ClickGuiPanel panel, EnumSetting<?> setting, boolean inGroup) {
        super(panel, inGroup);
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {

        if (ClickGui.shouldHide(this.setting)) {
            return;
        }

        this.updatePosition();
        this.drawBackground();

        String name = ClickGui.mod().displayNames.get() ? setting.displayName : setting.name;
        String value = ClickGui.mod().displayNames.get() ? setting.getDisplayValue() : setting.get().name();

        ClickGui.drawString(name, this.x + (this.inGroup ? 18 : 14), this.y + HEIGHT / 2.0F - ClickGui.font().getHeight(name), -1);
        ClickGui.drawString(value, this.x + ClickGui.WIDTH - ClickGui.font().getStringWidth(value) * 2.0F - (inGroup ? 16 : 12), this.y + HEIGHT / 2.0F - ClickGui.font().getHeight(name), -1);

        if (this.mouseIntersecting(mouseX, mouseY)) {
            ClickGui.toolTip = this.setting.description;
            String enumDesc = this.setting.getValueDescription();
            if (enumDesc != null) {
                ClickGui.toolTip += "\n" + enumDesc;
            }
        }

        this.panel.drawY += HEIGHT;

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (ClickGui.shouldHide(this.setting)) {
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

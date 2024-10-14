package wtf.bhopper.nonsense.gui.clickgui.dropdown.settings;

import net.minecraft.client.gui.Gui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownPanel;
import wtf.bhopper.nonsense.module.setting.impl.NumberSetting;
import wtf.bhopper.nonsense.util.render.ColorUtil;

public class DropdownSlider extends DropdownComponent {

    private final NumberSetting<?> setting;
    private boolean selected = false;

    public DropdownSlider(DropdownPanel panel, NumberSetting<?> setting, boolean inGroup) {
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

        float value = this.setting.getF();
        float min = this.setting.minF();
        float max = this.setting.maxF();
        float percent = (value - min) / (max - min);
        int left = this.x + (inGroup ? 9 : 5);
        int right = this.x + DropdownClickGui.WIDTH - (inGroup ? 9 : 5);
        int width = DropdownClickGui.WIDTH - (inGroup ? 18 : 10);

        int color = DropdownClickGui.getColor(this.panel);
        String name = DropdownClickGui.mod().displayNames.get() ? this.setting.displayName : this.setting.name;
        String displayValue = this.setting.getDisplayValue();

        Gui.drawRect(left, this.y + 2, right, this.y + HEIGHT - 2, this.inGroup ? 0xFF070707 : 0xFF111111);

        Gui.drawRect(left + 2, this.y + 2, left + (int)((width - 2) * percent), this.y + HEIGHT - 2, color);
        Gui.drawRect(left + (int)((width - 2) * percent), this.y + 2, left + 2 + (int)(width * percent), this.y + HEIGHT - 2, ColorUtil.darken(color));

        DropdownClickGui.drawString(name, this.x + (inGroup ? 18 : 14), this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(name), -1);
        DropdownClickGui.drawString(displayValue, this.x + DropdownClickGui.WIDTH - DropdownClickGui.font().getStringWidth(displayValue) * 2 - (inGroup ? 16 : 12), this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(name), 0xFFAAAAAA);

        if (this.mouseIntersecting(mouseX, mouseY)) {
            DropdownClickGui.toolTip = this.setting.description;
        }

        this.panel.drawY += HEIGHT;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (DropdownClickGui.shouldHide(this.setting)) return;

        if (this.mouseIntersecting(mouseX, mouseY)) {
            this.selected = true;
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.selected = false;
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (DropdownClickGui.shouldHide(this.setting)) return;

        if (this.selected) {
            float min = this.setting.minF();
            float max = this.setting.maxF();
            float left = this.x + (inGroup ? 9 : 5);
            float width = DropdownClickGui.WIDTH - (inGroup ? 18 : 10);
            float percent = (mouseX - left) / (width - 1);
            setting.setF(min + percent * (max - min));
        }
    }
}

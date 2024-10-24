package wtf.bhopper.nonsense.gui.clickgui.dropdown.settings;

import net.minecraft.client.gui.Gui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownPanel;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.module.setting.impl.*;

import java.util.ArrayList;
import java.util.List;

public class DropdownGroup extends DropdownComponent {

    private final GroupSetting setting;

    private boolean expanded;
    private final List<DropdownComponent> components = new ArrayList<>();

    public DropdownGroup(DropdownPanel panel, GroupSetting setting, boolean inGroup) {
        super(panel, inGroup);
        this.setting = setting;
        for (Setting<?> s : this.setting.get()) {
            if (s instanceof BooleanSetting) this.components.add(new DropdownSwitch(this.panel, (BooleanSetting)s, true));
            if (s instanceof NumberSetting) this.components.add(new DropdownSlider(this.panel, (NumberSetting<?>)s, true));
            if (s instanceof EnumSetting) this.components.add(new DropdownModeSelector(this.panel, (EnumSetting<?>)s, true));
            if (s instanceof ColorSetting) this.components.add(new DropdownColorPicker(this.panel, (ColorSetting)s, true));
            if (s instanceof StringSetting) this.components.add(new DropdownTextBox(this.panel, (StringSetting)s, true));
            if (s instanceof ButtonSetting) this.components.add(new DropdownButton(this.panel, (ButtonSetting)s, true));
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (DropdownClickGui.shouldHide(this.setting)) return;

        this.updatePosition();
        this.drawBackground();

        String name = (DropdownClickGui.mod().displayNames.get() ? setting.displayName : setting.name) + "...";

        if (this.expanded) {
            Gui.drawRect(this.x + 5, y + 2 , this.x + DropdownClickGui.WIDTH - 5, y + HEIGHT, 0xFF0D0D0D);
        }

        DropdownClickGui.drawString(name, this.x + DropdownClickGui.WIDTH / 2.0F - DropdownClickGui.font().getStringWidth(name), this.y + HEIGHT / 5.0F, -1);

        this.panel.drawY += HEIGHT;

        if (this.expanded) {
            this.components.forEach(components -> components.draw(mouseX, mouseY, partialTicks));
            Gui.drawRect(this.x, this.panel.drawY+ 2, this.x + DropdownClickGui.WIDTH, this.panel.drawY + 4, 0xFF111111);
            Gui.drawRect(this.x + 8, this.panel.drawY + 2, this.x + DropdownClickGui.WIDTH - 3, this.panel.drawY + 4, 0xFF171717);
            this.panel.drawY += 2;
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (DropdownClickGui.shouldHide(this.setting)) return;

        if (this.mouseIntersecting(mouseX, mouseY)) {
            if (mouseButton == 0) {
                if (!this.components.isEmpty()) {
                    this.expanded = !this.expanded;
                } else {
                    this.expanded = false;
                }
            }
        } else if (this.expanded) {
            this.components.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (!DropdownClickGui.shouldHide(this.setting) && this.expanded) {
            this.components.forEach(button -> button.mouseReleased(mouseX, mouseY, state));
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (!DropdownClickGui.shouldHide(this.setting) && this.expanded) {
            this.components.forEach(button -> button.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick));
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (!DropdownClickGui.shouldHide(this.setting) && this.expanded) {
            this.components.forEach(button -> button.keyTyped(typedChar, keyCode));
        }
    }
}

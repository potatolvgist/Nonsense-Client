package wtf.bhopper.nonsense.gui.clickgui.dropdown;

import net.minecraft.client.gui.Gui;
import org.lwjglx.input.Keyboard;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.settings.*;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.module.setting.impl.*;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class DropdownModuleButton {

    public static final int HEIGHT = 32;

    private final DropdownPanel panel;
    private final Module module;

    private int x, y;
    private boolean expanded = false;

    private final List<DropdownComponent> components = new ArrayList<>();

    public DropdownModuleButton(DropdownPanel panel, Module module) {
        this.panel = panel;
        this.module = module;
        for (Setting<?> setting : this.module.getSettings()) {
            if (setting instanceof BooleanSetting) this.components.add(new DropdownSwitch(this.panel, (BooleanSetting)setting, false));
            if (setting instanceof NumberSetting) this.components.add(new DropdownSlider(this.panel, (NumberSetting<?>)setting, false));
            if (setting instanceof EnumSetting) this.components.add(new DropdownModeSelector(this.panel, (EnumSetting<?>)setting, false));
            if (setting instanceof ColorSetting) this.components.add(new DropdownColorPicker(this.panel, (ColorSetting)setting, false));
            if (setting instanceof GroupSetting) this.components.add(new DropdownGroup(this.panel, (GroupSetting)setting, false));
            if (setting instanceof StringSetting) this.components.add(new DropdownTextBox(this.panel, (StringSetting)setting, false));
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        this.x = this.panel.getX();
        this.y = this.panel.drawY;
        this.panel.drawCount++;

        int color = this.module.isEnabled() ? DropdownClickGui.getColor(this.panel) : 0xFF222222;
        if (this.mouseIntersecting(mouseX, mouseY)) {
            if (this.module.isEnabled()) {
                color = ColorUtil.darken(color);
            } else {
                color = ColorUtil.brighten(color);
            }
        }

        String name = DropdownClickGui.mod().displayNames.get() ? module.displayName : module.name;

        Gui.drawRect(this.x, this.y, this.x + DropdownClickGui.WIDTH, this.y + HEIGHT + 3, 0xFF111111);
        Gui.drawRect(this.x + 3, this.y, this.x + DropdownClickGui.WIDTH - 3, this.y + HEIGHT + 3, color);

        DropdownClickGui.drawString(name, this.x + 8, this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(name) - 1, this.module.isEnabled() ? -1 : 0xFF555555);
        if (this.module.getBind() != Keyboard.KEY_NONE && DropdownClickGui.mod().keyBinds.get()) {
            String key = "[" + Keyboard.getKeyName(this.module.getBind()) + "]";
            DropdownClickGui.drawString(key, this.x + DropdownClickGui.WIDTH - 6 - DropdownClickGui.font().getStringWidth(key) * 2, this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(key) - 1, this.module.isEnabled() ? 0xFFAAAAAA : 0xFF555555);
        }

        this.panel.drawY += HEIGHT;

        if (this.expanded) {
            this.components.forEach(settingButton -> settingButton.draw(mouseX, mouseY, partialTicks));
        }

        if (this.mouseIntersecting(mouseX, mouseY)) {
            DropdownClickGui.toolTip = this.module.description;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.mouseIntersecting(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.module.toggle();
            } else if (mouseButton == 1) {
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
        if (this.expanded) {
            this.components.forEach(button -> button.mouseReleased(mouseX, mouseY, state));
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.expanded) {
            this.components.forEach(button -> button.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick));
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (this.expanded) {
            this.components.forEach(button -> button.keyTyped(typedChar, keyCode));
        }
    }

    private boolean mouseIntersecting(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + DropdownClickGui.WIDTH && mouseY > y && mouseY < y + HEIGHT;
    }

}

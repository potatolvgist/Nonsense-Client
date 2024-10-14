package wtf.bhopper.nonsense.gui.clickgui.dropdown.settings;

import net.minecraft.client.gui.GuiScreen;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownPanel;
import wtf.bhopper.nonsense.module.setting.impl.StringSetting;
import wtf.bhopper.nonsense.util.misc.Clock;

public class DropdownTextBox extends DropdownComponent {

    private final StringSetting setting;

    private boolean selected = false;

    private final Clock dashClock = new Clock();
    private boolean dash = false;

    public DropdownTextBox(DropdownPanel panel, StringSetting setting, boolean inGroup) {
        super(panel, inGroup);
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {

        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        if (dashClock.hasReached(500)) {
            this.dash = !this.dash;
            this.dashClock.reset();
        }

        this.updatePosition();
        this.drawBackground(this.selected ? HEIGHT * 2 : HEIGHT);

        String name = DropdownClickGui.mod().displayNames.get() ? this.setting.displayName : this.setting.name;
        String value = this.setting.get();
        int groupFactor = this.inGroup ? 18 : 14;

        if (this.selected) {
            DropdownClickGui.drawString(name, this.x + groupFactor, this.y + HEIGHT / 2.0F - DropdownClickGui.stringHeight(name) / 2.0F, -1);
            int stringX = Math.min(this.x + groupFactor, this.x + DropdownClickGui.WIDTH - groupFactor - (int) DropdownClickGui.stringWidth(value + "_"));
            String display = "\2477" + value + (this.dash ? "\2478_" : "");
            DropdownClickGui.drawString(display, stringX, this.y + HEIGHT + HEIGHT / 6.0F - DropdownClickGui.stringHeight(display) / 2.0F, -1);
        } else {
            String fullText = name + "\2477: " + value;
            DropdownClickGui.drawString(fullText, this.x + groupFactor, this.y + HEIGHT / 2.0F - DropdownClickGui.stringHeight(fullText) / 2.0F, -1);
        }


        this.panel.drawY += this.selected ? HEIGHT * 1.5F : HEIGHT;

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (DropdownClickGui.shouldHide(this.setting)) {
            this.selected = false;
            return;
        }

        this.selected = mouseButton == 0 && this.mouseIntersecting(mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (DropdownClickGui.shouldHide(this.setting) || !this.selected) {
            return;
        }

        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.setting.append(GuiScreen.getClipboardString());
        } else if (typedChar == '\b') {
            this.setting.backspace();
        } else if (typedChar >= ' ' && typedChar <= '~') {
            this.setting.append(typedChar);
        }
    }
}

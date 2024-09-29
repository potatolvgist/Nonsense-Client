package wtf.bhopper.nonsense.gui.clickgui.settings;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import wtf.bhopper.nonsense.gui.clickgui.ClickGui;
import wtf.bhopper.nonsense.gui.clickgui.ClickGuiPanel;
import wtf.bhopper.nonsense.module.setting.impl.StringSetting;
import wtf.bhopper.nonsense.util.Clock;

public class ClickGuiTextBox extends ClickGuiComponent {

    private final StringSetting setting;

    private boolean selected = false;

    private final Clock dashClock = new Clock();
    private boolean dash = false;

    public ClickGuiTextBox(ClickGuiPanel panel, StringSetting setting, boolean inGroup) {
        super(panel, inGroup);
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {

        if (ClickGui.shouldHide(this.setting)) {
            return;
        }

        if (dashClock.hasReached(500)) {
            this.dash = !this.dash;
            this.dashClock.reset();
        }

        this.updatePosition();
        this.drawBackground(this.selected ? HEIGHT * 2 : HEIGHT);

        String name = ClickGui.mod().displayNames.get() ? this.setting.displayName : this.setting.name;
        String value = this.setting.get();
        int groupFactor = this.inGroup ? 18 : 14;

        if (this.selected) {
            ClickGui.drawString(name, this.x + groupFactor, this.y + HEIGHT / 2.0F - ClickGui.stringHeight(name) / 2.0F, -1);
            int stringX = Math.min(this.x + groupFactor, this.x + ClickGui.WIDTH - groupFactor - (int)ClickGui.stringWidth(value + "|"));
            String display = value + (this.dash ? "\2477_" : "");
            ClickGui.drawString(display, stringX, this.y + HEIGHT + HEIGHT / 2.0F - ClickGui.stringHeight(display) / 2.0F, -1);
        } else {
            String fullText = name + "\2477: " + value;
            ClickGui.drawString(fullText, this.x + groupFactor, this.y + HEIGHT / 2.0F - ClickGui.stringHeight(fullText) / 2.0F, -1);
        }


        this.panel.drawY += this.selected ? HEIGHT * 2 : HEIGHT;

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (ClickGui.shouldHide(this.setting)) {
            this.selected = false;
            return;
        }

        this.selected = mouseButton == 0 && this.mouseIntersecting(mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (ClickGui.shouldHide(this.setting) || !this.selected) {
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

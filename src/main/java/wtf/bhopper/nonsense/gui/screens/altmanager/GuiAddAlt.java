package wtf.bhopper.nonsense.gui.screens.altmanager;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjglx.input.Keyboard;

public class GuiAddAlt extends GuiScreen {

    private final GuiScreen parentScreen;

    public GuiAddAlt(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 66, "Microsoft"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 90, "Cookie"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, 114, "Browser"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, 138, "Session"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, 162, "Refresh"));

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("gui.cancel")));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1) {
            // Microsoft
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiAddCookieAlt(this.parentScreen));
        } else if (button.id == 3) {
//            this.mc.displayGuiScreen(new GuiAddBrowserAccount(this.parentScreen));
        } else if (button.id == 4) {
            // Session
        } else if (button.id == 5) {
            // Refresh
        }
    }

}

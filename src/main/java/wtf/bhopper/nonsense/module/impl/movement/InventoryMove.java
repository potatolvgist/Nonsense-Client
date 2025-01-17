package wtf.bhopper.nonsense.module.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;

public class InventoryMove extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.VANILLA, value -> this.clientSideOnly.setDisplayed(value == Mode.VANILLA));
    private final BooleanSetting clientSideOnly = new BooleanSetting("Client Side Only", "Only move in client side GUI's", false);

    private final KeyBinding[] binds = new KeyBinding[]{
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindJump
    };

    public InventoryMove() {
        super("Inventory Move", "Allows you to move while in your inventory or other GUI's", Category.MOVEMENT);
        this.addSettings(this.mode, this.clientSideOnly);
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {

        if (!this.allowMove()) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            return;
        }

        for (KeyBinding bind : this.binds) {
            bind.setPressed(Keyboard.isKeyDown(bind.getKeyCode()));
        }

    }

    public boolean allowMove() {

        if (!this.isEnabled() || mc.thePlayer == null) {
            return false;
        }

        if (mc.isSingleplayer() && mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame()) {
            return false;
        }

        if (mode.is(Mode.VANILLA) && clientSideOnly.get()) {
            if (mc.currentScreen instanceof GuiContainer && !((GuiContainer) mc.currentScreen).isClientSide()) {
                return false;
            }
        }

        return true;
    }

    private enum Mode {
        VANILLA
    }

}

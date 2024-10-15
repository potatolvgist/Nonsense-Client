package wtf.bhopper.nonsense.module.impl.player;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;

public class InventoryManager extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.BASIC);

    public InventoryManager() {
        super("Inventory Manager", "Manage your inventory", Category.PLAYER);
        this.addSettings(mode);
    }

    public enum Mode {
        BASIC,
        ADVANCED
    }

}

package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import org.lwjglx.openal.AL;
import wtf.bhopper.nonsense.event.impl.EventPreUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.util.misc.Clock;

import java.lang.reflect.Array;

public class InventoryManager extends Module {

    private static final String[] BLACKLISTED_CONTAINERS = new String[]{"mode", "delivery", "menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", //
            "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept",  //
            "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock", "anticheat", "travel", "settings", //
            "user", "preference", "compass", "cake", "wars", "buy", "upgrade", "ranged", "potions", "utility", "collectibles"};

    private final GroupSetting slotsGroup = new GroupSetting("Slots", "Slots", this);

    @SuppressWarnings("unchecked")
    private final EnumSetting<ItemSlot>[] slotSettings = (EnumSetting<ItemSlot>[]) Array.newInstance(EnumSetting.class, 9);

    private final GroupSetting autoArmorGroup = new GroupSetting("Auto Armor", "Auto armor", this);
    private final BooleanSetting enableAutoArmor = new BooleanSetting("Enable", "Enable auto armor", true);
    private final BooleanSetting armorHotbar = new BooleanSetting("Hotbar", "Put on armor in hotbar", true);
    private final BooleanSetting armorDrop = new BooleanSetting("Drop", "Drop armor", true);

    private final GroupSetting stealerGroup = new GroupSetting("Chest Stealer", "Check stealer", this);
    private final BooleanSetting enable = new BooleanSetting("Enable chest stealer", "Take items from chests", true);
    private final BooleanSetting stealerWhitelist = new BooleanSetting("Whitelist GUI", "Don't take from certain GUI's", true);

    private final EnumSetting<ActionMode> dropMode = new EnumSetting<>("Drop Mode", "Drop mode", ActionMode.ALWAYS);
    private final EnumSetting<ActionMode> swapMode = new EnumSetting<>("Swap Mode", "Swap mode", ActionMode.ALWAYS);
    private final IntSetting delay = new IntSetting("Delay", "Delay", 0, 10000, 100, "%dms", null);
    private final BooleanSetting guiCheck = new BooleanSetting("Gui Check", "Check that that inventory is open", false);

    private final Clock timer = new Clock();

    public InventoryManager() {
        super("Inventory Manager", "Manage your inventory", Category.PLAYER);

        for (int i = 0; i < 9; i++) {
            String name = "Slot " + (i + 1);
            ItemSlot value = i == 0 ? ItemSlot.SWORD : i == 1 ? ItemSlot.BLOCKS : i == 8 ? ItemSlot.GOLDEN_APPLE : ItemSlot.NONE;
            slotSettings[i] = new EnumSetting<>(name, name, value);
        }
        this.slotsGroup.add(this.slotSettings);

        this.autoArmorGroup.add(enableAutoArmor, armorHotbar, armorDrop);
        this.stealerGroup.add(this.enable, this.stealerWhitelist);

        this.addSettings(this.slotsGroup, this.autoArmorGroup, this.stealerGroup, this.dropMode, this.swapMode, this.delay, this.guiCheck);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {

        if (!this.timer.hasReached(this.delay.get())) {
            return;
        }

        if (mc.currentScreen instanceof GuiChest) {
            GuiChest gui = (GuiChest)mc.currentScreen;
            ContainerChest container = (ContainerChest)gui.inventorySlots;
            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
                ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);

            }


            return;
        }

        Container container = mc.thePlayer.inventoryContainer;

    }

    private boolean needItem(ItemStack stack) {
        return false;
    }

    public enum ActionMode {
        ALWAYS,
        INVENTORY,
        NOT_MOVING,
        NEVER
    }

    public enum ItemSlot {
        SWORD,
        BOW,
        ROD,
        PEARL,
        GOLDEN_APPLE,
        GOLDEN_HEAD,
        BLOCKS,
        PICKAXE,
        AXE,
        SHOVEL,
        NONE
    }

}

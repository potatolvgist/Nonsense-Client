package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;

    public static int serverItem = 0;

    public static int currentItem() {
        return serverItem;
    }

    public static boolean placeStackInHotbar(ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null) {
                mc.thePlayer.inventory.mainInventory[i] = stack.copy();
                PacketUtil.send(new C10PacketCreativeInventoryAction(i + 36, stack));
                return true;
            }
        }

        return false;
    }

    public static void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    public static void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
    }

    public static void swap(int slot, int hSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hSlot, 2, mc.thePlayer);
    }


}

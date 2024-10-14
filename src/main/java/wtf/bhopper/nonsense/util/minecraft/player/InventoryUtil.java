package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

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

}

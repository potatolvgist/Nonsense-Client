package wtf.bhopper.nonsense.util.minecraft.player;

import net.minecraft.client.Minecraft;

public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int serverItem = 0;

    public static int currentItem() {
        return serverItem;
    }

}

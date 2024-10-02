package wtf.bhopper.nonsense.util.minecraft.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class ServerUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isInTab(EntityPlayer player) {
        for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getId().compareTo(player.getUniqueID()) == 0)  {
                return true;
            }
        }

        return false;
    }

}

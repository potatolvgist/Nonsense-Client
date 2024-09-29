package wtf.bhopper.nonsense.util.minecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtil {

    public static void send(Packet packet) {
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
    }

    public static void sendNoEvent(Packet packet) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
    }

}

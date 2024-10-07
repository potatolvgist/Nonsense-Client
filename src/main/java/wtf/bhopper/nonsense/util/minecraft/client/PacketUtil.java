package wtf.bhopper.nonsense.util.minecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.other.Debugger;

public class PacketUtil {

    public static void send(Packet packet) {
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
    }

    public static void sendNoEvent(Packet packet) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
        Nonsense.INSTANCE.eventBus.post(new Debugger.EventPacketDebug(packet, Debugger.State.NO_EVENT, Debugger.EventPacketDebug.Direction.OUTGOING));
    }

}

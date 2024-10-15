package wtf.bhopper.nonsense.module.impl.other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class PackSpoofer extends Module {

    public PackSpoofer() {
        super("Pack Spoofer", "Spoofs server resource packs", Category.OTHER);
    }

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {
        if (event.packet instanceof S48PacketResourcePackSend) {
            event.cancel();
            S48PacketResourcePackSend packet = (S48PacketResourcePackSend)event.packet;
            PacketUtil.send(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.ACCEPTED));
            PacketUtil.send(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            Notification.send("Pack Spoofer", "Spoofed Pack: " + packet.getURL(), NotificationType.SUCCESS, 3000);
        }
    }

}

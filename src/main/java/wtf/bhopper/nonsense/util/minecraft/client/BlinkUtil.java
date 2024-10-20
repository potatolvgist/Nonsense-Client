package wtf.bhopper.nonsense.util.minecraft.client;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Packet;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventSendPacket;

import java.util.ArrayDeque;
import java.util.Queue;

public class BlinkUtil {

    private static boolean blink = false;

    private static final Queue<Packet> chokedPackets = new ArrayDeque<>();

    @EventHandler
    public static void onSendPacket(EventSendPacket event) {
        if (blink) {
            chokedPackets.add(event.packet);
            event.cancel();
        }
    }

    public static void enableBlink() {
        blink = true;
    }

    public static void disableBlink() {
        blink = false;
        poll();
    }

    public static void poll() {
        while (!chokedPackets.isEmpty()) {
            PacketUtil.sendNoEvent(chokedPackets.poll());
        }
    }

    public static void init() {
        Nonsense.INSTANCE.eventBus.subscribe(BlinkUtil.class);
    }

}

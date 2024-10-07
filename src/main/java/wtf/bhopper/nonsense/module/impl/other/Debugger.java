package wtf.bhopper.nonsense.module.impl.other;

import com.google.common.collect.EvictingQueue;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Debugger extends Module {

    private final GroupSetting packetDebuggerClient = new GroupSetting("Client Packets", "Client packet debugger", this);
    private final GroupSetting packetDebuggerServer = new GroupSetting("Server Packets", "Server packet debugger", this);

    private final Map<Class<? extends Packet>, BooleanSetting> clientPacketSettings = new HashMap<>();
    private final Map<Class<? extends Packet>, BooleanSetting> serverPacketSettings = new HashMap<>();

    private Queue<PacketInfo> packetCache = EvictingQueue.create(100);

    public Debugger() {
        super("Debugger", "Helps with debugging", Category.OTHER);

        for (int i = 0; ; i++) {
            try {
                Packet packet = EnumConnectionState.PLAY.getPacket(EnumPacketDirection.SERVERBOUND, i);
                if (packet == null) {
                    break;
                }

                Class<? extends Packet> packetClass = packet.getClass();
                BooleanSetting setting = new BooleanSetting(String.format("C%02X", i), packetClass.getSimpleName(), false);
                this.clientPacketSettings.put(packetClass, setting);
                this.packetDebuggerClient.add(setting);

            } catch (IllegalAccessException | InstantiationException ignored) {}
        }

        for (int i = 0; ; i++) {
            try {
                Packet packet = EnumConnectionState.PLAY.getPacket(EnumPacketDirection.CLIENTBOUND, i);
                if (packet == null) {
                    break;
                }

                Class<? extends Packet> packetClass = packet.getClass();
                BooleanSetting setting = new BooleanSetting(String.format("S%02X", i), packetClass.getSimpleName(), false);
                this.serverPacketSettings.put(packetClass, setting);
                this.packetDebuggerServer.add(setting);

            } catch (IllegalAccessException | InstantiationException ignored) {}
        }

        this.addSettings(packetDebuggerClient, packetDebuggerServer);

    }

    @EventHandler
    public void onPacketDebug(EventPacketDebug event) {

        try {

            if (event.direction == EventPacketDebug.Direction.OUTGOING) {

                if (this.clientPacketSettings.get(event.packet.getClass()).get()) {
                    this.printPacket(event.packet, event.state);
                }

            } else if (event.direction == EventPacketDebug.Direction.INCOMING) {

                if (this.serverPacketSettings.get(event.packet.getClass()).get()) {
                    this.printPacket(event.packet, event.state);
                }

            }
        } catch (Exception exception) {
            Nonsense.LOGGER.error("Failed to log packet", exception);
        }
    }

    public void printPacket(Packet packet, State state) {
        String mainValue = getPacketMainValue(packet);

        StringBuilder info = new StringBuilder()
                .append(ChatUtil.CHAT_PREFIX_SHORT)
                .append(state.format)
                .append(packet.getClass().getSimpleName());

        if (mainValue != null) {
            info.append(EnumChatFormatting.GRAY)
                    .append(": ")
                    .append(mainValue);
        }

        PacketInfo cache = new PacketInfo(packet, state);
        this.packetCache.add(cache);

        ChatUtil.Builder.of(info.toString())
                .setHoverEvent("View Packet Info")
                .setClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(".debug packet %d", cache.hashCode()))
                .send();

    }

    public PacketInfo cachedPacket(int hashCode) {
        return packetCache.stream()
                .filter(packet -> packet.hashCode() == hashCode)
                .findFirst()
                .orElse(null);
    }

    public String getPacketMainValue(Packet packet) {
        if (packet instanceof C02PacketUseEntity) {
            return ((C02PacketUseEntity)packet).getAction().name();
        }

        if (packet instanceof C07PacketPlayerDigging) {
            return ((C07PacketPlayerDigging)packet).getStatus().name();
        }

        if (packet instanceof C09PacketHeldItemChange) {
            return Integer.toString(((C09PacketHeldItemChange)packet).getSlotId());
        }

        if (packet instanceof C0BPacketEntityAction) {
            return ((C0BPacketEntityAction)packet).getAction().name();
        }

        if (packet instanceof C0EPacketClickWindow) {
            return Integer.toString(((C0EPacketClickWindow) packet).getSlotId());
        }

        return null;
    }

    public enum State {
        NORMAL(EnumChatFormatting.GREEN),
        NO_EVENT(EnumChatFormatting.YELLOW),
        CANCELED(EnumChatFormatting.RED);

        public final EnumChatFormatting format;

        State(EnumChatFormatting format) {
            this.format = format;
        }

        public String getString() {
            return String.format("%s%s", format, EnumSetting.toDisplay(this));
        }
    }

    public static class PacketInfo {

        public String clazz;
        public State state;
        public FieldInfo[] fields;

        public PacketInfo(Packet packet, State state) {
            this.clazz = packet.getClass().getSimpleName();
            this.state = state;
            Field[] declaredFields = packet.getClass().getDeclaredFields();
            this.fields = new FieldInfo[declaredFields.length];
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                field.setAccessible(true);
                try {
                    this.fields[i] = new FieldInfo(field.getType().getSimpleName(), field.getName(), field.get(packet).toString());
                } catch (IllegalAccessException | IllegalArgumentException exception) {
                    this.fields[i] = new FieldInfo(field.getType().getSimpleName(), field.getName(), "\247cError");
                }
            }
        }

        public void print() {
            ChatUtil.print("%s--- Packet Info ---", EnumChatFormatting.AQUA);
            ChatUtil.print("\247bClass\2478: \2477%s", this.clazz);
            ChatUtil.print("\247bState\2478: \2477%s", state.getString());

            if (this.fields.length == 0) {
                ChatUtil.print("\247bFields\2478: \2477<NONE>");
            } else {
                ChatUtil.print("\247bFields\2478:");
                for (FieldInfo field : this.fields) {
                    ChatUtil.print("  \2478[\2473%s\2478] \247b%s\2478: \2477%s", field.type, field.name, field.value);
                }
            }
        }

        public static class FieldInfo {
            String type;
            String name;
            String value;

            public FieldInfo(String type, String name, String value) {
                this.type = type;
                this.name = name;
                this.value = value;
            }
        }
    }

    public static class EventPacketDebug {
        public final Packet packet;
        public final State state;
        public final Direction direction;

        public EventPacketDebug(Packet packet, State state, Direction direction) {
            this.packet = packet;
            this.state = state;
            this.direction = direction;
        }

        public enum Direction {
            INCOMING,
            OUTGOING
        }
    }



}

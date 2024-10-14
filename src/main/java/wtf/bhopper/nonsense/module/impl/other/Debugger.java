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
import wtf.bhopper.nonsense.event.impl.EventPreClick;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.gui.components.RenderTable;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Debugger extends Module {

    private final GroupSetting packetDebuggerClient = new GroupSetting("Client Packets", "Client packet debugger", this);
    private final GroupSetting packetDebuggerServer = new GroupSetting("Server Packets", "Server packet debugger", this);

    private final BooleanSetting tableTestSet = new BooleanSetting("Table Test", "Table test", false);

    private final Map<Class<? extends Packet>, BooleanSetting> clientPacketSettings = new HashMap<>();
    private final Map<Class<? extends Packet>, BooleanSetting> serverPacketSettings = new HashMap<>();

    private final Queue<PacketInfo> packetCache = EvictingQueue.create(100);

    private final RenderTable<TableTest> tableTest = new RenderTable<>(TableTest.class, (o1, o2) -> 0, "A", 10, 10);

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

        Hud.addComponent(tableTest);

        this.addSettings(packetDebuggerClient, packetDebuggerServer, tableTestSet);

    }

    @EventHandler
    public void onTick(EventPreTick event) {
        this.tableTest.setEnabled(this.tableTestSet.get());
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

        if (packet instanceof C08PacketPlayerBlockPlacement) {
            return String.valueOf(((C08PacketPlayerBlockPlacement)packet).getStack());
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
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    this.fields[i] = new FieldInfo(field.getType().getSimpleName(), field.getName(), String.valueOf(field.get(packet)));
                } catch (IllegalAccessException | IllegalArgumentException exception) {
                    this.fields[i] = new FieldInfo(field.getType().getSimpleName(), field.getName(), "\247cError");
                }
            }
        }

        public void print() {
            ChatUtil.debugTitle("Packet Info");
            ChatUtil.debugItem("Class", this.clazz);
            ChatUtil.debugItem("State", state.getString());

            if (this.fields.length > 0) {
                String[] printFields = new String[this.fields.length];
                for (int i = 0; i < this.fields.length; i++) {
                    if (this.fields[i] == null) {
                        continue;
                    }
                    printFields[i] = String.format("  \2478[\2474%s\2478] \247c%s\2478: \2477%s", this.fields[i].type, this.fields[i].name, this.fields[i].value);
                }
                ChatUtil.debugList("Fields", printFields);
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

    public static class TableTest {
        @RenderTable.TableColumn("Test 1") int x = 0;
        @RenderTable.TableColumn("Test 69") int y = 69;
    }



}

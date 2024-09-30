package wtf.bhopper.nonsense.util.minecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class ChatUtil {

    public static final String CHAT_PREFIX = "\2478\247l[\247c\247lNonsense\2478\247l] \247r\2477";
    public static final String CHAT_PREFIX_SHORT = "\247f> \2477";

    public static void raw(String message) {
        raw(new ChatComponentText(message));
    }

    public static void raw(IChatComponent message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    }

    public static void print(String message, Object... args) {
        raw(CHAT_PREFIX_SHORT + String.format(message, args));
    }

    public static void info(String message, Object... args) {
        raw(CHAT_PREFIX + String.format(message, args));
    }

    public static void error(String message, Object... args) {
        raw(CHAT_PREFIX + "\247c" + String.format(message, args));
    }

    public static void style(ChatStyle style, String message, Object... args) {
        ChatComponentText component = new ChatComponentText(CHAT_PREFIX_SHORT + String.format(message, args));
        component.setChatStyle(style);
        raw(component);
    }

    public static void send(String message, Object... args) {
        PacketUtil.send(new C01PacketChatMessage(String.format(message, args)));
    }

}

package wtf.bhopper.nonsense.util.minecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import tv.twitch.chat.ChatBadgeData;

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
        Builder.of("%s\247r%s", CHAT_PREFIX, String.format(message, args))
                .setColor(EnumChatFormatting.RED)
                .send();
    }

    public static void style(ChatStyle style, String message, Object... args) {
        ChatComponentText component = new ChatComponentText(CHAT_PREFIX_SHORT + String.format(message, args));
        component.setChatStyle(style);
        raw(component);
    }

    public static void send(String message, Object... args) {
        PacketUtil.send(new C01PacketChatMessage(String.format(message, args)));
    }

    public static void debugTitle(String title) {
        print("\247c\247l--- %s ---", title);
    }

    public static void debugItem(String name, Object value) {
        Builder.of("%s\247c%s\2478: \2477%s", CHAT_PREFIX_SHORT, name, String.valueOf(value))
                .setHoverEvent("Click to copy!")
                .setClickEvent(ClickEvent.Action.RUN_COMMAND, ".copy " + value)
                .send();
    }

    public static void debugList(String name, String... items) {
        ChatUtil.print("\247c%s\2478:", name);
        for (String item : items) {
            if (item == null) {
                continue;
            }
            ChatUtil.print("  \2477%s", item);
        }
    }


    /**
     * Utility class to easily build IChatComponents (Text).
     * @author Semx11
     * <https://gist.github.com/Semx11/e3c1a8df4d8667a6c30a6d01505418c5>
     */
    public static class Builder {

        private final IChatComponent parent;

        private final String text;
        private final ChatStyle style;

        private Builder(String text) {
            this(text, null, Inheritance.SHALLOW);
        }

        private Builder(String text, IChatComponent parent, Inheritance inheritance) {
            this.parent = parent;
            this.text = text;

            switch (inheritance) {
                case DEEP:
                    this.style = parent != null ? parent.getChatStyle() : new ChatStyle();
                    break;
                default:
                case SHALLOW:
                    this.style = new ChatStyle();
                    break;
                case NONE:
                    this.style = new ChatStyle().setColor(null).setBold(false).setItalic(false)
                            .setStrikethrough(false).setUnderlined(false).setObfuscated(false)
                            .setChatClickEvent(null).setChatHoverEvent(null).setInsertion(null);
                    break;
            }
        }

        public static Builder of(String text) {
            return new Builder(text);
        }

        public static Builder of(String text, Object... args) {
            return new Builder(String.format(text, args));
        }

        public Builder setColor(EnumChatFormatting color) {
            style.setColor(color);
            return this;
        }

        public Builder setBold(boolean bold) {
            style.setBold(bold);
            return this;
        }

        public Builder setItalic(boolean italic) {
            style.setItalic(italic);
            return this;
        }

        public Builder setStrikethrough(boolean strikethrough) {
            style.setStrikethrough(strikethrough);
            return this;
        }

        public Builder setUnderlined(boolean underlined) {
            style.setUnderlined(underlined);
            return this;
        }

        public Builder setObfuscated(boolean obfuscated) {
            style.setObfuscated(obfuscated);
            return this;
        }

        public Builder setClickEvent(ClickEvent.Action action, String value) {
            style.setChatClickEvent(new ClickEvent(action, value));
            return this;
        }

        public Builder setHoverEvent(String value) {
            return this.setHoverEvent(new ChatComponentText(value));
        }

        public Builder setHoverEvent(IChatComponent value) {
            return this.setHoverEvent(HoverEvent.Action.SHOW_TEXT, value);
        }

        public Builder setHoverEvent(HoverEvent.Action action, IChatComponent value) {
            style.setChatHoverEvent(new HoverEvent(action, value));
            return this;
        }

        public Builder setCustomClickEvent(CustomClickEvent event) {
            style.setCustomClickEvent(event);
            return this;
        }

        public Builder setCustomClickEvent(Runnable runnable) {
            style.setCustomClickEvent(new CustomClickEvent(runnable));
            return this;
        }

        public Builder setInsertion(String insertion) {
            style.setInsertion(insertion);
            return this;
        }

        public Builder append(String text) {
            return this.append(text, Inheritance.SHALLOW);
        }

        public Builder append(String text, Inheritance inheritance) {
            return new Builder(text, this.build(), inheritance);
        }

        public IChatComponent build() {
            IChatComponent thisComponent = new ChatComponentText(text).setChatStyle(style);
            return parent != null ? parent.appendSibling(thisComponent) : thisComponent;
        }

        public void send() {
            ChatUtil.raw(build());
        }

        public enum Inheritance {
            DEEP, SHALLOW, NONE
        }

    }

    public static class CustomClickEvent {

        public final Runnable runnable;

        public CustomClickEvent(Runnable runnable) {
            this.runnable = runnable;
        }

    }

}

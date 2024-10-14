package wtf.bhopper.nonsense.module.impl.other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.resources.IResource;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.event.impl.EventSendPacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.module.setting.util.Description;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class ChatFilter extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.BYPASS, value -> this.bypassFrequency.setDisplayed(value == Mode.BYPASS));

    private final BooleanSetting wordCheck = new BooleanSetting("Word Check", "Check for specific words for filtering", false);

    private final IntSetting bypassFrequency = new IntSetting("Frequency", "Bypass frequency", 1, 5, 1);

    private final Map<Character, Character> replacements = new HashMap<>();

    private final List<String> singleWords = new ArrayList<>();
    private final List<String> multiWords = new ArrayList<>();

    private final List<String> commands = Arrays.asList("message", "msg", "whisper", "w", "tell", "reply", "r");
    private final List<String> hypixelCommands = Arrays.asList(
            "allchat", "achat", "ac",
            "partychat", "pchat", "pc",
            "guildchat", "gchat", "gc",
            "officerchat", "ochat", "oc",
            "coopchat", "cchat", "cc",
            "shout"
    );

    public ChatFilter() {
        super("Chat Filter", "Allows you to bypass chat filters", Category.OTHER);

        this.addSettings(mode, wordCheck, bypassFrequency);

        this.loadWords();
        this.addReplacements();
    }

    @EventHandler
    public void onSendPacket(EventSendPacket event) {
        if (event.packet instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage)event.packet;
            String message = packet.getMessage();
            String prefix = "";

            if (message.startsWith("/")) {
                String[] parts = message.split("\\s+");

                String cmd = parts[0].substring(1);

                if (!this.commands.contains(cmd)) {
                    return;
                }

                message = message.substring(cmd.length() + 2);
                prefix = "/" + cmd + " ";
            }

            if (wordCheck.get()) {
                for (String word : this.singleWords) {
                    if (message.contains(word)) {
                        message = message.replace(word, this.filterWord(word));
                    }
                }
                for (String word : this.multiWords) {
                    if (message.contains(word)) {
                        message = message.replace(word, this.filterWord(word));
                    }
                }
            } else {
                message = filterEverything(message);
            }

            event.packet = new C01PacketChatMessage(prefix + message);

        }
    }

    public String filterEverything(String message) {
        switch (mode.get()) {
            case BYPASS: {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < message.length(); i++) {
                    builder.append(message.charAt(i));
                    if (i % bypassFrequency.get() == 0)  {
                        builder.append('\u05FD');
                    }
                }
                return builder.toString();
            }

            case ACCENT: {
                String result = message;
                for (Character character : this.replacements.keySet()) {
                    result = result.replace(character, this.replacements.get(character));
                }
                result = result.replace('.', '\u2025');
                return result;
            }
        }

        return message;
    }

    public String filterWord(String message) {
        switch (mode.get()) {
            case BYPASS: {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < message.length(); i++) {
                    builder.append(message.charAt(i));
                    if (i % bypassFrequency.get() == 0)  {
                        builder.append('\u05FD');
                    }
                }
                return builder.toString();
            }

            case ACCENT: {
                String result = message;
                for (Character character : this.replacements.keySet()) {
                    result = result.replace(character, this.replacements.get(character));
                }
                return result;
            }

            case MINIBLOX: {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < message.length(); i++) {
                    builder.append(message.charAt(i));
                    if (i % bypassFrequency.get() == 0)  {
                        builder.append('\u200E');
                    }
                }
                return builder.toString();
            }
        }

        return message;
    }

    public String filterLink(String message) {
        switch (mode.get()) {
            case BYPASS: {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < message.length(); i++) {
                    builder.append(message.charAt(i));
                    if (i % bypassFrequency.get() == 0)  {
                        builder.append('\u05FD');
                    }
                }
                return builder.toString();
            }

            case ACCENT: {
                return message.replace('.', '\u2025');
            }
        }

        return message;
    }

    private void loadWords() {
        try {
            IResource resource = mc.getResourceManager().getResource(new ResourceLocation("nonsense/chatfilter.txt"));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line.contains(" ")) {
                        this.multiWords.add(line);
                    } else {
                        this.singleWords.add(line);
                    }
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void addReplacements() {
        this.replacements.clear();
        this.replacements.put('A', '\u00e5');
        this.replacements.put('E', '\u00e8');
        this.replacements.put('I', '\u00a1');
        this.replacements.put('O', '\u00f2');
        this.replacements.put('U', '\u00f9');
        this.replacements.put('Y', '\u00fd');
        this.replacements.put('a', '\u00e5');
        this.replacements.put('e', '\u00e8');
        this.replacements.put('i', '\u00a1');
        this.replacements.put('o', '\u00f2');
        this.replacements.put('u', '\u00f9');
        this.replacements.put('y', '\u00fd');
    }

    @Override
    public String getSuffix() {
        return this.mode.getDisplayValue();
    }

    private enum Mode {
        BYPASS,
        @Description("Works on Hypixel") ACCENT,
        MINIBLOX
    }

}

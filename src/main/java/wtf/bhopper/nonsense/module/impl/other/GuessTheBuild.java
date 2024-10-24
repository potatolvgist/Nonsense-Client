package wtf.bhopper.nonsense.module.impl.other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResource;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.event.impl.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.event.impl.EventRender2D;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GuessTheBuild extends Module {

    private static final String[] RANK_PREFIX = {
            "Rookie",
            "Untrained",
            "Amateur",
            "Apprentice",
            "Experienced",
            "Seasoned",
            "Trained",
            "Skilled",
            "Talented",
            "Professional",
            "Expert",
            "Master",
            "#1 Builder",
            "#2 Builder",
            "#3 Builder",
            "#4 Builder",
            "#5 Builder",
            "#6 Builder",
            "#7 Builder",
            "#8 Builder",
            "#9 Builder",
            "#10 Builder"
    };

    private final BooleanSetting autoGuess = new BooleanSetting("Auto Guess", "automatically guesses when there's only 1 theme left", true);

    private final GuessTheBuildRender render = new GuessTheBuildRender();

    private final List<String> themes = new ArrayList<>();
    private final List<String> possibleWords = new ArrayList<>();
    private final List<String> impossibleWords = new ArrayList<>();

    private boolean guessed = false;
    private String lastGuess = "";

    public GuessTheBuild() {
        super("Guess The Build", "Auto guess the build solver", Category.OTHER);
        this.addSettings(this.autoGuess);
        this.loadThemes();
    }

    @Override
    public void onDisable() {
        this.render.setEnabled(false);
    }

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {
        if (event.packet instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat)event.packet;

            String message = EnumChatFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getUnformattedText());


            if (packet.getType() == 2) {
                if (message.startsWith("The theme is ")) {
                    String word = message.replace("The theme is ", "");
                    if (!lastGuess.equalsIgnoreCase(word)) {
                        lastGuess = word;
                        try {
                            possibleWords.clear();
                            for (String value : this.themes) {
                                String guessWord = value.toLowerCase();
                                if (word.length() != guessWord.length()) continue;

                                char[] guessChars = guessWord.toCharArray();
                                char[] wordChars = word.toLowerCase().replace("_", ".").toCharArray();
                                boolean goodWord = true;
                                for (int i = 0; i < guessChars.length; i++) {
                                    if (wordChars[i] == '.') continue;
                                    if (guessChars[i] != wordChars[i]) {
                                        goodWord = false;
                                        break;
                                    }
                                }
                                if (goodWord) possibleWords.add(value);
                            }
                            possibleWords.removeIf(e -> this.possibleWords.contains(e.toLowerCase()));
                            if (possibleWords.size() == 1 && !this.guessed && this.autoGuess.get()) {
                                ChatUtil.send("%s", possibleWords.get(0));
                                Notification.send("Guess The Build", "Theme has been identified as \247a" + possibleWords.get(0), NotificationType.SUCCESS, 3000);
                            }
                        } catch (Exception ignored) {
                        }
                    }

                }
            } else {

                // Checking is you've guessed the theme or it's your turn
                if (message.equals(mc.thePlayer.getName() + " correctly guessed the theme!") || message.equals("You can't send messages when it's your turn to build!")) {
                    guessed = true;
                }

                // New round
                if (message.startsWith("Round: ")) {
                    guessed = false;
                    possibleWords.clear();
                    impossibleWords.clear();
                }

                // System guessed words
                if (message.contains(": ") && !message.startsWith("Round: ") && !message.startsWith("The theme was: ") && !message.startsWith("Builder: ") && !message.startsWith("Party > ") && !message.startsWith("Guild > ") && !message.startsWith("Officer > ") && !message.startsWith("To ") && !message.startsWith("From ")) {
                    this.addImpossible(message.split(": ")[1].toLowerCase());
                }

                // Player guessed words
                for (String prefix : RANK_PREFIX) {
                    if (message.startsWith(prefix)) {
                        this.addImpossible(message.split(": ")[1].toLowerCase());
                        break;
                    }
                }

            }

        }
    }

    private void addImpossible(String word) {
        impossibleWords.add(word);
        possibleWords.removeIf(str -> impossibleWords.contains(str.toLowerCase()));
        if (possibleWords.size() == 1 && !guessed && this.autoGuess.get()) {
            ChatUtil.send("%s", possibleWords.get(0));
        }
    }

    @EventHandler
    public void onRender2D(EventRender2D event) {
        this.render.setEnabled(true);
    }

    @EventHandler
    public void onJoin(EventJoinGame event) {
        this.possibleWords.clear();
        this.impossibleWords.clear();
        this.guessed = false;
    }

    private void loadThemes() {
        try {
            IResource resource = mc.getResourceManager().getResource(new ResourceLocation("nonsense/gtb.txt"));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    this.themes.add(line);
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public class GuessTheBuildRender extends RenderComponent {

        public GuessTheBuildRender() {
            super("GTB", 2, 2, mc.fontRendererObj.getStringWidth("____________________"), mc.fontRendererObj.FONT_HEIGHT + 2);
        }

        @Override
        public void draw(ScaledResolution res, float delta, int mouseX, int mouseY, boolean bypass) {
            FontRenderer font = mc.fontRendererObj;
            int count = 1;
            this.drawBackground(0x80000000);
            this.setHeight(3 + (possibleWords.size() + 1) * font.FONT_HEIGHT);

            this.drawString("Possible Themes \2478(\2477" + GuessTheBuild.this.possibleWords.size() + "\2478)", 2, 2, Hud.color());

            int index = mouseY / mc.fontRendererObj.FONT_HEIGHT;

            try {
                for (String word : GuessTheBuild.this.possibleWords) {
                    this.drawString(count + ": ", 2, 2 + count * font.FONT_HEIGHT, Hud.color());
                    this.drawString(word, 2 + font.getStringWidth("00: "), 2 + count * font.FONT_HEIGHT,  index == count ? Hud.color() : -1);
                    count++;
                }
            } catch (Exception ignored) {}

        }

        @Override
        public void onClick(int x, int y, int button) {
            if (this.mouseIntersecting(x, y)) {
                int index = y / mc.fontRendererObj.FONT_HEIGHT - 1;
                try {
                    ChatUtil.send(GuessTheBuild.this.possibleWords.get(index));
                } catch (IndexOutOfBoundsException ignored) {}
            }
        }
    }

}

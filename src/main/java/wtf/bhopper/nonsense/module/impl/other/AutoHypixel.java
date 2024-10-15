package wtf.bhopper.nonsense.module.impl.other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import wtf.bhopper.nonsense.event.impl.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.module.setting.impl.StringSetting;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;
import wtf.bhopper.nonsense.util.minecraft.world.HypixelUtil;
import wtf.bhopper.nonsense.util.misc.Clock;

import java.util.regex.Pattern;

public class AutoHypixel extends Module {

    private final GroupSetting autoGgGroup = new GroupSetting("Auto GG", "Auto GG", this);
    private final BooleanSetting autoGgEnabled = new BooleanSetting("Enabled", "Enables auto GG", true);
    private final StringSetting autoGgMessage = new StringSetting("Message", "Auto gg message", "gg");

    private final BooleanSetting cleanChat = new BooleanSetting("Clean Chat", "Removes unnecessary messages", true);
    private final BooleanSetting lobbyJoin = new BooleanSetting("Lobby Join Chat", "Removes lobby join messages", false);
    private final BooleanSetting autoTip = new BooleanSetting("Auto Tip", "Auto tip", true);

    private boolean joined = false;
    private final Clock autoGgTimer = new Clock();

    public AutoHypixel() {
        super("Auto Hypixel", "Helpful mods for Hypixel", Category.OTHER);
        autoGgGroup.add(autoGgEnabled, autoGgMessage);
        this.addSettings(autoGgGroup, cleanChat, lobbyJoin, autoTip);
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        if (this.joined) {
            if (this.autoTip.get()) {
                ChatUtil.send("/tipall");
            }
            this.joined = false;
        }
    }

    @EventHandler
    public void onJoin(EventJoinGame event) {
        this.joined = true;
    }

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {
        if (event.packet instanceof S02PacketChat) {
            this.onChatMessage(event, (S02PacketChat)event.packet);
        }
    }

    private void onChatMessage(EventReceivePacket event, S02PacketChat packet) {

        IChatComponent component = packet.getChatComponent();
        String formatted = component.getFormattedText();
        String unformatted = component.getUnformattedText();

        if (this.autoGgEnabled.get()) {
            for (Pattern regex : HypixelUtil.AUTO_GG_REGEX) {
                if (regex.matcher(unformatted).matches()) {
                    if (autoGgTimer.hasReached(10000) && !autoGgMessage.get().isEmpty()) {
                        ChatUtil.send(autoGgMessage.get());
                        autoGgTimer.reset();
                    }
                    break;
                }
            }
        }

        if (this.cleanChat.get()) {
            if (unformatted.startsWith("+") && unformatted.endsWith(" Karma!")) {
                event.cancel();
            }

            if (unformatted.equals("Rate this map by clicking: [5] [4] [3] [2] [1]")) {
                event.cancel();
            }

            if (unformatted.startsWith("Buy ") && unformatted.endsWith(" at https://store.hypixel.net")) {
                event.cancel();
            }

            if (unformatted.equals("You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!")) {
                event.cancel();
            }
        }

        if (this.lobbyJoin.get() && (formatted.contains("\2476join the lobby!") || formatted.contains("\2476spooked into the lobby!"))) {
            event.cancel();
        }



    }

}

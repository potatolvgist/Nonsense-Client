package wtf.bhopper.nonsense.module.impl.other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.server.S02PacketChat;
import wtf.bhopper.nonsense.event.impl.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.module.setting.impl.StringSetting;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;
import wtf.bhopper.nonsense.util.misc.Clock;

public class PartySpammer extends Module {

    private final StringSetting player = new StringSetting("Player", "Player to spam", "chortler");
    private final IntSetting delay = new IntSetting("Delay", "Delay between invites", 1, 1000, 200, "%dms", null);
    private final BooleanSetting cleanChat = new BooleanSetting("Clean Chat", "Hides the party messages", true);

    private final Clock timer = new Clock();
    private boolean inParty = false;

    public PartySpammer() {
        super("Party Spammer", "Spams a player with party invites on Hypixel", Category.OTHER);
        this.addSettings(player, delay, cleanChat);
    }

    @Override
    public void onEnable() {
        timer.reset();
        ChatUtil.send("/p invite %s", player.get());
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        if (timer.hasReached(delay.get())) {
            if (this.inParty) {
                ChatUtil.send("/p leave");
            } else {
                ChatUtil.send("/p invite %s", player.get());
            }
            inParty = !inParty;
            timer.reset();
        }
    }

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {
        if (event.packet instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat)event.packet;

            String text = packet.getChatComponent().getUnformattedText();

            if (text.equals("You cannot invite that player since they have ignored you.")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because the player ignored you... LOL", NotificationType.INFO, 4000);
            }

            if (text.equals("You cannot invite that player.")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because the player has party invites off :(", NotificationType.INFO, 4000);
            }

            if (text.equals("You cannot invite that player since they're not online.")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because the player is offline", NotificationType.INFO, 4000);
            }

            if (text.equals("Couldn't find a player with that name!")) {
                toggle(false);
                Notification.send("Party Spammer", "Disabled party spammer because player does not exist", NotificationType.INFO, 4000);
            }

            if (cleanChat.get()) {
                if (
                        text.equals("-----------------------------------------------------") ||
                                text.endsWith(" to the party! They have 60 seconds to accept.") ||
                                text.equals("You left the party.") ||
                                text.equals("The party was disbanded because all invites expired and the party was empty.") ||
                                text.equals("You are not in a party.") ||
                                text.equals("Woah slow down, you're doing that too fast!") ||
                                text.endsWith(" has already been invited to the party.") ||
                                text.startsWith("You have joined ")
                ) {
                    event.cancel();
                }

                if (text.endsWith(" joined the party.")) {
                    event.cancel();
                    ChatUtil.raw("\2479\247m-----------------------------------------------------");
                    ChatUtil.raw(packet.getChatComponent().getFormattedText());
                    ChatUtil.raw("\2479\247m-----------------------------------------------------");
                }
            }

        }
    }

    @EventHandler
    public void onJoin(EventJoinGame event) {
        this.toggle(false);
        Notification.send("Party Spammer", "Party spammer was automatically disabled", NotificationType.INFO, 3000);
    }

}

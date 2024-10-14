package wtf.bhopper.nonsense.module.impl.visual;

import meteordevelopment.orbit.EventHandler;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;

public class DiscordRPCMod extends Module {

    private static final String APPLICATION_ID = "1294195120781922305";

    public DiscordRPCMod() {
        super("Discord RPC", "Discord Rich Presence", Category.VISUAL);

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(user -> Nonsense.LOGGER.info("Logged in as {}:{}", user.username, user.userId))
                .build();

        DiscordRPC.discordInitialize(APPLICATION_ID, handlers, true);
        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));

    }

    @Override
    public void onDisable() {
        DiscordRPC.discordClearPresence();
    }

    @EventHandler
    public void onTick(EventPreTick event) {

        DiscordRichPresence presence = new DiscordRichPresence
                .Builder(String.format("%d/%d modules enabled", Nonsense.INSTANCE.moduleManager.amountEnabled(), Nonsense.INSTANCE.moduleManager.size()))
                .setStartTimestamps(Nonsense.INSTANCE.startTime)
                .build();

        DiscordRPC.discordUpdatePresence(presence);

    }

}

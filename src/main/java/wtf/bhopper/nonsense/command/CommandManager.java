package wtf.bhopper.nonsense.command;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.reflections.Reflections;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventSendChat;
import wtf.bhopper.nonsense.event.impl.EventSendPacket;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

public class CommandManager extends LinkedHashMap<Class<? extends Command>, Command> {

    public static final String PREFIX = ".";

    public CommandManager() {
        super();
        Nonsense.INSTANCE.eventBus.subscribe(this);
    }

    public void addCommands() {
        new Reflections("wtf.bhopper.nonsense.command.impl")
                .getSubTypesOf(Command.class)
                .stream()
                .sorted(Comparator.comparing(Class::getSimpleName))
                .forEach(command -> {
                    try {
                        Nonsense.LOGGER.info("Add Command: {}", command.getSimpleName());
                        this.put(command, command.newInstance());
                    } catch (InstantiationException | IllegalAccessException exception) {
                        throw new RuntimeException(exception);
                    }

                });
    }

    @SuppressWarnings("unchecked")
    public <T extends Command> T get(Class<T> clazz) {
        return (T) this.getOrDefault(clazz, null);
    }

    public Command get(String name) {
        return this.values()
                .stream()
                .filter(command -> command.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Command find(String name) {
        return this.values()
                .stream()
                .filter(command -> command.nameMatches(name))
                .findFirst()
                .orElse(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleChat(EventSendChat event) {

        if (!event.message.startsWith(PREFIX)) {
            return;
        }

        event.setCancelled(true);
        String[] args = event.message.split("\\s+");
        String commandName = args[0].substring(PREFIX.length());

        Command command = this.find(commandName);

        if (command == null) {
            ChatUtil.error("'%s' is not a command", commandName);
            return;
        }

        try {
            command.onCommand(args, event.message);
        } catch (Exception exception) {
            ChatUtil.error("Error while running command '%s': %s", command.name, exception.getMessage());
            Nonsense.LOGGER.error("Error while running command: " + command.name, exception);
        }

    }

}

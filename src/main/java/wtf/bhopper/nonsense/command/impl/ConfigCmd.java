package wtf.bhopper.nonsense.command.impl;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.config.Config;
import wtf.bhopper.nonsense.util.NonsenseException;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class ConfigCmd extends Command {

    public ConfigCmd() {
        super("Config", "Manage configs", ".config <create/delete/save/load> <name> | .config list", "c", "settings", "s");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {

        if (args.length < 2) {
            ChatUtil.error("Invalid arguments: %s", syntax);
            return;
        }

        String action = args[1];

        if (action.equalsIgnoreCase("create")) {
            if (args.length < 3) {
                ChatUtil.error("Invalid arguments: %s", syntax);
                return;
            }

            try {
                Nonsense.INSTANCE.configManager.createConfig(args[2]);
                ChatUtil.info("Created config: %s", args[2].toLowerCase());
            } catch (NonsenseException exception) {
                ChatUtil.error("Failed to create config: %s", exception.getMessage());
                Nonsense.LOGGER.error(exception);
            }
        } else if (action.equalsIgnoreCase("delete")) {
            if (args.length < 3) {
                ChatUtil.error("Invalid arguments: %s", syntax);
                return;
            }

            try {
                Nonsense.INSTANCE.configManager.deleteConfig(args[2]);
                ChatUtil.info("Deleted config: %s", args[2].toLowerCase());
            } catch (NonsenseException exception) {
                ChatUtil.error("Failed to delete config: %s", exception.getMessage());
                Nonsense.LOGGER.error(exception);
            }

        } else if (action.equalsIgnoreCase("save")) {
            if (args.length < 3) {
                ChatUtil.error("Invalid arguments: %s", syntax);
                return;
            }

            try {
                Nonsense.INSTANCE.configManager.saveConfig(args[2]);
                ChatUtil.info("Saved config: %s", args[2].toLowerCase());
            } catch (NonsenseException exception) {
                ChatUtil.error("Failed to save config: %s", exception.getMessage());
                Nonsense.LOGGER.error(exception);
            }
        } else if (action.equalsIgnoreCase("load")) {
            if (args.length < 3) {
                ChatUtil.error("Invalid arguments: %s", syntax);
                return;
            }

            try {
                Nonsense.INSTANCE.configManager.loadConfig(args[2]);
                ChatUtil.info("Loaded config: %s", args[2].toLowerCase());
            } catch (NonsenseException exception) {
                ChatUtil.error("Failed to load config: %s", exception.getMessage());
                Nonsense.LOGGER.error(exception);
            }
        } else if (action.equalsIgnoreCase("list")) {
            ChatUtil.print("\247c\247l--- Configs ---");
            int count = 1;
            for (Config config : Nonsense.INSTANCE.configManager.getConfigs()) {
                ChatStyle style = new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".config load " + config.getName()))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to load config: " + config.getName())));
                ChatUtil.style(style,"\247f%d. \2477%s", count, config.getName());
                ++count;
            }
        }

    }
}

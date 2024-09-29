package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class HelpCmd extends Command {

    public HelpCmd() {
        super("Help", "Displays help", ".help", "?");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {
        for (Command command : Nonsense.INSTANCE.commandManager.values()) {
            ChatUtil.print("\247c\247l%s \247r\247f%s \2477%s", command.name, command.description, command.syntax);
        }
    }
}

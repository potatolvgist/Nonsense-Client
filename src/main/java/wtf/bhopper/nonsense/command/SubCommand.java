package wtf.bhopper.nonsense.command;

import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

public class SubCommand implements MinecraftInstance {

    public final String name;
    public final String description;
    public final CommandRun commandRun;

    public SubCommand(String name, String description, CommandRun commandRun) {
        this.name = name;
        this.description = description;
        this.commandRun = commandRun;
    }

    public interface CommandRun {
        void execute(String[] args, String rawCommand) throws Exception;
    }

}

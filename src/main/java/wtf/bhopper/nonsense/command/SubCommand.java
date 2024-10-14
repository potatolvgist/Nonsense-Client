package wtf.bhopper.nonsense.command;

public class SubCommand {

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

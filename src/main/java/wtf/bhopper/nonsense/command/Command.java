package wtf.bhopper.nonsense.command;

import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public abstract class Command implements MinecraftInstance {

    public final String name;
    public final String description;
    public final String syntax;
    public final String[] aliases;

    public Command(String name, String description, String syntax, String... aliases) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.aliases = aliases;
    }

    public boolean nameMatches(String name) {
        if (this.name.equalsIgnoreCase(name)) return true;

        for (String alias : this.aliases) {
            if (alias.equalsIgnoreCase(name)) return true;
        }

        return false;
    }

    protected void invalidArgs() {
        ChatUtil.error("Invalid arguments: %s", this.syntax);
    }

    public abstract void onCommand(String[] args, String rawCommand) throws Exception;

}

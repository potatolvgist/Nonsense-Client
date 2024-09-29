package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class ToggleCmd extends Command {

    public ToggleCmd() {
        super("Toggle", "Toggles a module", ".toggle <module>", "t");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) {

        if (args.length < 2) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        Module module = Nonsense.INSTANCE.moduleManager.get(args[1]);
        if (module == null) {
            ChatUtil.error("'%s' is not a module", args[1]);
            return;
        }

        module.toggle();
        ChatUtil.info("%s %s", module.isEnabled() ? "Enabled" : "Disabled", module.name);

    }
}

package wtf.bhopper.nonsense.command.impl;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class HideCmd extends Command {

    public HideCmd() {
        super("Hide", "Hide or show modules on the module list", ".hide <module> [state]");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {

        if (args.length < 2) {
            this.invalidArgs();
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            for (Module module : Nonsense.INSTANCE.moduleManager.values()) {
                module.setHidden(true);
            }
            ChatUtil.info("Hidden all modules");
            return;
        }

        if (args[1].equalsIgnoreCase("none")) {
            for (Module module : Nonsense.INSTANCE.moduleManager.values()) {
                module.setHidden(false);
            }
            ChatUtil.info("Displaying all modules");
            return;
        }

        Module module = Nonsense.INSTANCE.moduleManager.get(args[1]);

        if (module == null) {
            ChatUtil.error("'%s' is not a module", args[1]);
            return;
        }

        module.setHidden(args[2] != null ? (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("1")) : !module.isHidden());
        ChatUtil.info("'%s' is now %s", module.name, module.isHidden() ? "hidden" : "displayed");

    }
}

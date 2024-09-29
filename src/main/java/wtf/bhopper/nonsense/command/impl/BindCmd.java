package wtf.bhopper.nonsense.command.impl;

import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class BindCmd extends Command {

    public BindCmd() {
        super("Bind", "Set a keybind", ".bind <module> <key> | .bind del <module> | .bind clear", "b");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) {
        if (args.length < 2) {
            ChatUtil.error("Missing arguments: %s", this.syntax);
            return;
        }

        if (args[1].equalsIgnoreCase("clear")) {
             for (Module module : Nonsense.INSTANCE.moduleManager.values()) {
                 module.setBind(Keyboard.KEY_NONE);
             }
             ChatUtil.info("Reset key-binds");
        } else if (args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("rm")) {
            if (args.length < 3) {
                ChatUtil.error("Missing arguments: %s", this.syntax);
                return;
            }
            Module module = Nonsense.INSTANCE.moduleManager.get(args[2]);

            if (module == null) {
                ChatUtil.error("'%s' is not a module", args[2]);
                return;
            }

            module.setBind(Keyboard.KEY_NONE);
            ChatUtil.info("Removed key-bind for: %s", module.name);

        } else {
            if (args.length < 3) {
                ChatUtil.error("Missing arguments: %s", this.syntax);
                return;
            }

            Module module = Nonsense.INSTANCE.moduleManager.get(args[1]);

            if (module == null) {
                ChatUtil.error("'%s' is not a module", args[1]);
                return;
            }

            module.setBind(Keyboard.getKeyIndex(args[2].toUpperCase()));
            ChatUtil.info("%s was bound to: %s", module.name, Keyboard.getKeyName(module.getBind()));
        }


    }
}
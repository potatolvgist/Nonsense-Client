package wtf.bhopper.nonsense.command.impl;

import net.minecraft.command.CommandBase;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class SetCmd extends Command {

    public SetCmd() {
        super("Set", "Changes settings", ".set <module> <setting> <value>");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {

        if (args.length < 4) {
            ChatUtil.error("Invalid arguments: %s", syntax);
            return;
        }

        Module module = Nonsense.INSTANCE.moduleManager.get(args[1]);
        if (module == null) {
            ChatUtil.error("'%s' is not a module dummy", args[1]);
            return;
        }

        Setting<?> setting = module.getSetting(args[2]);
        if (setting == null) {
            ChatUtil.error("'%s' is not a setting in '%s' you buffoon", args[2], module.name);
            return;
        }

        if (setting instanceof GroupSetting) {
            if (args.length < 5) {
                ChatUtil.error("Invalid arguments: %s", syntax);
                return;
            }

            Setting<?> setting2 = ((GroupSetting) setting).getSetting(args[3]);
            String value = CommandBase.getChatComponentFromNthArg(mc.thePlayer, args, 4).getUnformattedText();
            setting2.parseString(value);
            ChatUtil.info("'%s' was set to '%s'", setting2.name, setting2.getDisplayValue());

        } else {
            String value = CommandBase.getChatComponentFromNthArg(mc.thePlayer, args, 3).getUnformattedText();
            setting.parseString(value);

            ChatUtil.info("'%s' was set to '%s'", setting.name, setting.getDisplayValue());
        }

    }
}

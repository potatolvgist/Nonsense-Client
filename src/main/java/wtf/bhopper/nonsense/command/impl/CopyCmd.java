package wtf.bhopper.nonsense.command.impl;

import net.minecraft.client.gui.GuiScreen;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class CopyCmd extends Command {

    public CopyCmd() {
        super("Copy", "Copies something to the clipboard (used in copy chat click events)", ".copy <text>");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            return;
        }

        String text = rawCommand.substring(args[0].length() + 1);
        GuiScreen.setClipboardString(text);
        ChatUtil.print("\247cCopied: \2477%s", text);
    }
}

package wtf.bhopper.nonsense.command.impl;

import net.minecraft.command.CommandBase;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class VClipCmd extends Command {

    public VClipCmd() {
        super("VClip", "Offsets your Y position", ".vclip <height>");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {
        if (args.length < 2) {
            ChatUtil.error("Invalid arguments: %s", syntax);
            return;
        }

        double offset = CommandBase.parseDouble(args[1]);
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ);
        ChatUtil.info("Teleported %s %s blocks", offset >= 0.0 ? "up" : "down", offset, String.valueOf(offset));
    }
}

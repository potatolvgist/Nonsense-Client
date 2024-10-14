package wtf.bhopper.nonsense.command.impl;

import net.minecraft.client.multiplayer.ServerData;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

public class ServerInfo extends Command {

    public ServerInfo() {
        super("ServerInfo", "Prints information on the current server", ".serverinfo");
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {

        if (mc.isSingleplayer()) {
            ChatUtil.error("Bro... you're in single-player");
            return;
        }

        ServerData serverData = mc.getCurrentServerData();

        if (serverData == null) {
            ChatUtil.error("Server data is null! (how tf???)");
            return;
        }

        ChatUtil.debugTitle("Server Info");
        ChatUtil.debugItem("IP", serverData.serverIP);
        ChatUtil.debugItem("Version", serverData.version);
        ChatUtil.debugItem("Brand", serverData.gameVersion);


    }
}

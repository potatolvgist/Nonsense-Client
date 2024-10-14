package wtf.bhopper.nonsense.command.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.command.Command;
import wtf.bhopper.nonsense.command.SubCommand;
import wtf.bhopper.nonsense.module.impl.other.Debugger;
import wtf.bhopper.nonsense.util.misc.JsonUtil;
import wtf.bhopper.nonsense.util.minecraft.client.ChatUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class DebugCmd extends Command {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public DebugCmd() {
        super("Debug", "Helps with debugging", ".debug <args>");

        this.subCommands.add(new SubCommand("help", "Prints help", (args, rawCommand) -> {
            ChatUtil.print("\247c--- Debug Command ---");
            for (SubCommand subCommand : this.subCommands) {
                ChatUtil.print("\247c\247l%s\2478: \2477%s", subCommand.name, subCommand.description);
            }
        }));

        this.subCommands.add(new SubCommand("iteminfo", "Prints information on your current held item", (args, rawCommand) -> {
            if (mc.thePlayer.getHeldItem() == null) {
                ChatUtil.error("No held item");
                return;
            }

            ItemStack stack = mc.thePlayer.getHeldItem();

            ChatUtil.debugTitle("Item Info");
            ChatUtil.debugItem("Item", stack.getItem().getUnlocalizedName());
            ChatUtil.debugItem("Amount", stack.stackSize);
            ChatUtil.debugItem("Metadata", String.format("%d | 0x%X", stack.getMetadata(), stack.getMetadata()));
            if (stack.hasDisplayName()) {
                ChatUtil.debugItem("Display Name", stack.getDisplayName());
            }
            if (stack.hasTagCompound()) {
                ChatUtil.debugItem("NBT", stack.getTagCompound().toString());
            }
        }));

        this.subCommands.add(new SubCommand("nbtdump", "Dumps the NBT data of your current held item to a file", (args, rawCommand) -> {
            if (mc.thePlayer.getHeldItem() == null) {
                ChatUtil.error("No held item");
                return;
            }

            ItemStack stack = mc.thePlayer.getHeldItem();

            if (!stack.hasTagCompound()) {
                ChatUtil.error("Item does not have any NBT data");
                return;
            }

            boolean json = args.length >= 3 && args[2].equalsIgnoreCase("json");

            File dir = Nonsense.INSTANCE.dataDir.toPath().resolve("nbtdump").toFile();
            dir.mkdirs();

            if (json) {
                File file = dir.toPath().resolve(String.format("dump_%08x.json", stack.hashCode())).toFile();

                FileWriter writer = new FileWriter(file);
                writer.write(JsonUtil.nbtToJson(stack.getTagCompound()));
                writer.close();

                ChatUtil.info("Dumped NBT data to file: %s", file.getPath());

            } else {
                File file = dir.toPath().resolve(String.format("dump_%08x.nbt", stack.hashCode())).toFile();

                CompressedStreamTools.write(stack.getTagCompound(), file);
                ChatUtil.info("Dumped NBT data to file: %s", file.getPath());
            }
        }));

        this.subCommands.add(new SubCommand("packet", "Prints information on a cached packet", (args, rawCommand) -> {
            if (args.length < 3) {
                ChatUtil.error("Invalid arguments");
                return;
            }
            try {
                int hash = Integer.parseInt(args[2]);
                Debugger.PacketInfo packet = Nonsense.module(Debugger.class).cachedPacket(hash);
                if (packet == null) {
                    ChatUtil.error("That packet was not found");
                    return;
                }
                packet.print();

            } catch (NumberFormatException exception) {
                ChatUtil.error("'%s' is not a number", args[2]);
            }
        }));
    }

    @Override
    public void onCommand(String[] args, String rawCommand) throws Exception {

        if (args.length < 2) {
            ChatUtil.error("Invalid arguments");
            return;
        }

        String sub = args[1];

        SubCommand subCommand = this.subCommands.stream()
                .filter(subCommand1 -> subCommand1.name.equalsIgnoreCase(sub))
                .findFirst()
                .orElse(null);

        if (subCommand == null) {
            ChatUtil.error("Invalid arguments");
        } else {
            subCommand.commandRun.execute(args, rawCommand);
        }

    }
}

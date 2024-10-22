package wtf.bhopper.nonsense.util.minecraft.client;

import io.netty.buffer.Unpooled;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.other.Debugger;

public class PacketUtil {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static void send(Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendNoEvent(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
        Nonsense.INSTANCE.eventBus.post(new Debugger.EventPacketDebug(packet, Debugger.State.NO_EVENT, Debugger.EventPacketDebug.Direction.OUTGOING));
    }

    public static PacketBuffer newBuffer() {
        return new PacketBuffer(Unpooled.buffer());
    }

    // Simulates a right click server side
    public static void rightClickPackets() {

        if (mc.objectMouseOver == null) {
            return;
        }

        ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
        boolean flag = true;

        switch (mc.objectMouseOver.typeOfHit) {
            case ENTITY:
                if (mc.playerController.isPlayerRightClickingOnEntity(mc.thePlayer, mc.objectMouseOver.entityHit, mc.objectMouseOver)) {
                    flag = false;
                } else if (mc.playerController.interactWithEntitySendPacket(mc.thePlayer, mc.objectMouseOver.entityHit)) {
                    flag = false;
                }
                break;

            case BLOCK:
                BlockPos blockpos = mc.objectMouseOver.getBlockPos();

                if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)) {
                        send(new C0APacketAnimation());
                        flag = false;
                    }

                    if (itemstack == null) {
                        return;
                    }

                }
                break;
        }

        if (flag) {
            ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

            if (itemstack1 != null) {
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1);
            }
        }

    }


}

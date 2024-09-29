package wtf.bhopper.nonsense.util.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class BlockUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

}

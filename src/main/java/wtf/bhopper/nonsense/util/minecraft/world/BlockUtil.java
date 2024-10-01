package wtf.bhopper.nonsense.util.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class BlockUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static boolean isAir(BlockPos blockPos) {
        return getBlock(blockPos).getMaterial() == Material.air;
    }


}

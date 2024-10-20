package wtf.bhopper.nonsense.util.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

public class BlockUtil implements MinecraftInstance {

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static boolean isAir(BlockPos blockPos) {
        return getBlock(blockPos).getMaterial() == Material.air;
    }

    public static Block getRelativeBlock(double x, double y, double z) {
        return getBlock(new BlockPos(mc.thePlayer).add(x, y, z));
    }

    public static boolean isSolid(BlockPos blockPos) {
        return getBlock(blockPos)
                .getMaterial()
                .blocksMovement();
    }

    public static boolean safeToWalkOn(BlockPos blockPos) {
        if (!isSolid(blockPos)) {
            return false;
        }

        Block block = getBlock(blockPos);
        return !(block instanceof BlockFence) && !(block instanceof BlockWall);
    }

}

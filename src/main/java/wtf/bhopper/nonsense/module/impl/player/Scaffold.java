package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.EventPreUpdate;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;
import wtf.bhopper.nonsense.util.minecraft.player.Rotation;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {

    private static final List<Block> BAD_BLOCKS = Arrays.asList(
            Blocks.air,
            Blocks.sand,
            Blocks.gravel,
            Blocks.hopper,
            Blocks.dropper,
            Blocks.dispenser,
            Blocks.sapling,
            Blocks.web,
            Blocks.crafting_table,
            Blocks.furnace,
            Blocks.jukebox
    );

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.NORMAL);

    private final GroupSetting rotationGroup = new GroupSetting("Rotations", "Rotations", this);
    private final EnumSetting<RotationsMode> rotationsMode = new EnumSetting<>("Mode", "Rotations mode", RotationsMode.INSTANT);
    private final EnumSetting<RotationsHitVec> rotationsHitVec = new EnumSetting<>("Hit Vector", "Block placement vector", RotationsHitVec.CENTRE);

    private final BooleanSetting sprint = new BooleanSetting("Sprint", "Allows you to sprint while scaffolding", true);
    private final BooleanSetting swing = new BooleanSetting("Swing", "Swings client side", true);

    private BlockData blockData = null;
    private Vec3 hitVec = null;
    private Rotation rotations = null;

    public Scaffold() {
        super("Scaffold", "Auto bridge", Category.PLAYER);
        this.rotationGroup.add(rotationsMode, rotationsHitVec);
        this.addSettings(mode, rotationGroup, sprint, swing);
    }

    @Override
    public void onEnable() {
        this.blockData = null;
        this.hitVec = null;
        this.rotations = null;
    }

    @EventHandler
    public void onPreUpdate(EventPreUpdate event) {

        if (!sprint.get()) {
            mc.thePlayer.setSprinting(false);
        }

        if (!this.selectBlocks()) {
            return;
        }

        this.blockData = this.getBlockData();

        if (this.blockData != null) {
            this.placeBlock();
        }
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {

        switch (rotationsMode.get()) {
            case INSTANT:
                if (this.hitVec != null) {
                    this.rotations = RotationUtil.getRotations(this.hitVec);
                    this.hitVec = null;
                }
                break;

            case PLACE:
                if (this.hitVec != null) {
                    this.rotations = RotationUtil.getRotations(this.hitVec);
                    this.hitVec = null;
                } else {
                    this.rotations = null;
                }
                break;
        }

        if (this.rotations != null) {
            event.setRotations(this.rotations);
        }

    }

    private void placeBlock() {

        BlockPos pos = blockData.blockPos;
        EnumFacing face = blockData.facing;

        switch (rotationsHitVec.get()) {
            case CLOSEST:
                this.hitVec = RotationUtil.getHitVecOptimized(pos, face);
                break;

            default:
                this.hitVec = RotationUtil.getHitVec(pos, face);
                break;
        }

        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, face, this.hitVec)) {
            if (swing.get()) {
                mc.thePlayer.swingItem();
            } else {
                PacketUtil.send(new C0APacketAnimation());
            }
        }

    }

    private BlockData getBlockData() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        BlockPos playerPos = new BlockPos(x, y, z).down();

        if (BlockUtil.isAir(playerPos)) {
            for (EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP) {
                    continue;
                }
                BlockPos offset = playerPos.offset(face);
                if (!BlockUtil.isAir(offset)) {
                    return new BlockData(offset, face.getOpposite());
                }
            }

            for (EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP) {
                    continue;
                }
                BlockPos offset = playerPos.offset(face);
                if (BlockUtil.isAir(offset)) {
                    for (EnumFacing face2 : EnumFacing.values()) {
                        if (face2 == EnumFacing.UP) {
                            continue;
                        }
                        BlockPos offset2 = offset.offset(face2);
                        if (!BlockUtil.isAir(offset2)) {
                            return new BlockData(offset2, face2.getOpposite());
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean selectBlocks() {
        int slot = this.getBlockSlot();
        if (slot != -1) {
            mc.thePlayer.inventory.currentItem = slot;
            return true;
        }

        return false;
    }

    private int getBlockSlot() {
        int highestStack = -1;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack == null || !isValid(stack)) continue;
            if (stack.stackSize > 0) {
                if (stack.stackSize > highestStack) {
                    highestStack = stack.stackSize;
                    slot = i;
                }
            }
        }
        return slot;
    }

    private boolean isValid(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock) {
            ItemBlock block = (ItemBlock)itemStack.getItem();
            return !BAD_BLOCKS.contains(block.getBlock()) && block.getBlock().isNormalCube() && block.getBlock().isCollidable();
        }
        return false;
    }

    private static class BlockData {
        public final BlockPos blockPos;
        public final EnumFacing facing;

        public BlockData(BlockPos blockPos, EnumFacing facing) {
            this.blockPos = blockPos;
            this.facing = facing;
        }
    }

    private enum Mode {
        NORMAL
    }

    private enum RotationsMode {
        INSTANT,
        PLACE
    }

    private enum RotationsHitVec {
        CENTRE,
        CLOSEST
    }

}
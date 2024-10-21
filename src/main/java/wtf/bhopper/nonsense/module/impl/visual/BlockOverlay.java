package wtf.bhopper.nonsense.module.impl.visual;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventRender3D;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.ColorSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.util.misc.Clock;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlockOverlay extends Module {

    private static final float BED_HEIGHT = 0.5625F;

    public final BlockOverlaySetting mouseOver = new BlockOverlaySetting("Mouse over", true, 0xFF0000, true, true, this);
    private final BlockOverlaySetting chests = new BlockOverlaySetting("Chest", true, 0xFFAA00, this);
    private final BlockOverlaySetting trappedChest = new BlockOverlaySetting("Trapped Chest", true, 0xFF5555, this);
    private final BlockOverlaySetting enderChests = new BlockOverlaySetting("Ender Chest", true, 0xAA00AA, this);
    private final BlockOverlaySetting hopper = new BlockOverlaySetting("Hopper", false, 0x777777, this);
    private final BlockOverlaySetting dispenser = new BlockOverlaySetting("Dispenser", false, 0x00FFFF, this);
    private final BlockOverlaySetting dropper = new BlockOverlaySetting("Dropper", false, 0x55FF55, this);
    private final BlockOverlaySetting bedSet = new BlockOverlaySetting("Bed", true, 0x55AAFF, this);
    private final IntSetting bedRange = new IntSetting("Search Range", "Search range for non tile blocks", 2, 50, 10);

    private final Clock searchTimer = new Clock();
    private final List<AxisAlignedBB> beds = new ArrayList<>();

    public BlockOverlay() {
        super("Block Overlay", "Modify block overlay", Category.VISUAL);
        this.bedSet.add(this.bedRange);
        this.addSettings(this.mouseOver, this.chests, this.enderChests, this.hopper, this.dispenser, this.dropper, this.bedSet);
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {

        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            Block type = tileEntity.getBlockType();

            if (type == Blocks.chest) {
                if (this.chests.isEnabled()) {
                    this.chests.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.trapped_chest) {
                if (this.trappedChest.isEnabled()) {
                    this.trappedChest.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.ender_chest) {
                if (this.enderChests.isEnabled()) {
                    this.enderChests.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.hopper) {
                if (this.hopper.isEnabled()) {
                    this.hopper.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.dispenser) {
                if (this.dispenser.isEnabled()) {
                    this.dispenser.draw(tileEntity.getPos());
                }

            } else if (type == Blocks.dropper) {
                if (this.dropper.isEnabled()) {
                    this.dropper.draw(tileEntity.getPos());
                }

            }


        }

        if (this.bedSet.isEnabled()) {
            for (AxisAlignedBB box : this.beds) {
                bedSet.draw(box);
            }
        }

        if (this.mouseOver.isEnabled()) {
            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                this.mouseOver.draw(mc.objectMouseOver.getBlockPos());
            }
        }

    }

    @EventHandler
    public void onTick(EventPreTick event) {

        if (this.bedSet.isEnabled()) {
            if (searchTimer.hasReached(1000)) { // Only search every 1000ms (or 1 second)
                this.beds.clear();
                for (int x = -bedRange.get(); x <= bedRange.get(); x++) {
                    for (int y = -bedRange.get(); y <= bedRange.get(); y++) {
                        for (int z = -bedRange.get(); z <= bedRange.get(); z++) {

                            BlockPos pos = mc.thePlayer.getPosition().add(x, y, z);
                            IBlockState foot = mc.theWorld.getBlockState(pos);
                            if (foot.getBlock() == Blocks.bed && foot.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
                                EnumFacing direction = foot.getValue(BlockDirectional.FACING);
                                BlockPos other = pos.offset(direction);
                                IBlockState head = mc.theWorld.getBlockState(other);

                                double footX = pos.getX();
                                double footY = pos.getY();
                                double footZ = pos.getZ();

                                if (head.getBlock() == Blocks.bed && head.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {

                                    double headX = other.getX();
                                    double headZ = other.getZ();

                                    AxisAlignedBB box;
                                    if (footX != headX) {
                                        if (footX > headX) {
                                            box = new AxisAlignedBB(footX - 1.0, footY, footZ, footX + 1.0, footY + BED_HEIGHT, footZ + 1.0);
                                        } else {
                                            box = new AxisAlignedBB(footX, footY, footZ, footX + 2.0, footY + BED_HEIGHT, footZ + 1.0);
                                        }
                                    } else if (footZ > headZ) {
                                        box = new AxisAlignedBB(footX, footY, footZ - 1.0, footX + 1.0, footY + BED_HEIGHT, footZ + 1.0);
                                    } else {
                                        box = new AxisAlignedBB(footX, footY, footZ, footX + 1.0, footY + BED_HEIGHT, footZ + 2.0);
                                    }
                                    this.beds.add(box);
                                } else {
                                    this.beds.add(new AxisAlignedBB(footX, footY, footZ, footX + 1.0, footY + BED_HEIGHT, footZ + 1.0));
                                }
                            }

                        }
                    }
                }

                this.searchTimer.reset();
            }
        } else {
            this.beds.clear();
        }

    }

    public static class BlockOverlaySetting extends GroupSetting {

        private final BooleanSetting enable;
        private final ColorSetting color;
        private final BooleanSetting box;
        private final BooleanSetting outline;

        public BlockOverlaySetting(String displayName, boolean enabled, int color, boolean box, boolean outline, Module owner) {
            super(displayName, displayName, owner);
            this.enable = new BooleanSetting("Enable", "Enable " + displayName, enabled);
            this.color = new ColorSetting("Color", displayName + " color", color);
            this.box = new BooleanSetting("Box", displayName + " box", box);
            this.outline = new BooleanSetting("Outline", displayName + " outline", outline);
            this.add(this.enable, this.color, this.box, this.outline);
        }

        public BlockOverlaySetting(String displayName, boolean enabled, int color, Module owner) {
            this(displayName, enabled, color, true, false, owner);
        }

        public boolean isEnabled() {
            return this.enable.get();
        }

        public void draw(BlockPos blockPos) {
            RenderUtil.drawBlockBox(blockPos, this.color.get(), this.outline.get(), this.box.get(), 1.0F, false);
        }

        public void draw(AxisAlignedBB box) {
            this.draw(box, this.color.get());
        }

        public void draw(AxisAlignedBB box, Color color) {
            RenderUtil.drawAxisAlignedBB(RenderUtil.toRender(box), color, this.outline.get(), this.box.get(), 1.0F);
        }

    }

}

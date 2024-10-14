package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.impl.EventItemSelect;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;

public class FastMine extends Module {

    private final FloatSetting multiplier = new FloatSetting("Multiplier", "Multiplies mining speed", 1.0F, 5.0F, 1.3F);
    private final EnumSetting<AutoTool> autoTool = new EnumSetting<>("Auto Tool", "Selects the best tool for the job", AutoTool.SILENT);

    public FastMine() {
        super("Fast Mine", "Allows you to mine blocks faster", Category.PLAYER);
        this.addSettings(multiplier, autoTool);
    }

    @EventHandler
    public void onItemSelect(EventItemSelect event) {

        if (this.autoTool.is(AutoTool.NONE) || mc.objectMouseOver == null || mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }

        MovingObjectPosition objectMouseOver = mc.objectMouseOver;

        if (objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || !mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }

        Block block = BlockUtil.getBlock(objectMouseOver.getBlockPos());

        float bestSpeed = 1.0F;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);

            if (item == null) {
                continue;
            }

            if (!item.canHarvestBlock(block)) {
                continue;
            }

            float speed = item.getStrVsBlock(block);

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
            event.slot = bestSlot;
            event.silent = this.autoTool.is(AutoTool.SILENT);
        }
    }

    public float getBreakRequirement() {

        if (!this.isEnabled()) {
            return 1.0F;
        }

        return 1.0F / this.multiplier.get();
    }

    private enum AutoTool {
        CLIENT,
        SILENT,
        NONE
    }

}

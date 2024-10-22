package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.item.ItemStack;

public interface ItemScoreCalculator {
    float getScore(ItemStack stack);
}

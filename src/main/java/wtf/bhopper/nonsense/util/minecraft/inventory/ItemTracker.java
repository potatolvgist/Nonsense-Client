package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemTracker {

    private final ItemTypeChecker itemTypeChecker;
    private final ItemScoreCalculator itemScoreCalculator;
    private final ItemAmountChecker itemAmountChecker;

    private final List<ItemSlot> checkedItems = new ArrayList<>();

    public ItemTracker(ItemTypeChecker itemTypeChecker, ItemScoreCalculator itemScoreCalculator, ItemAmountChecker itemAmountChecker) {
        this.itemTypeChecker = itemTypeChecker;
        this.itemScoreCalculator = itemScoreCalculator;
        this.itemAmountChecker = itemAmountChecker;
    }

    public boolean isItemType(ItemStack itemStack) {
        return this.itemTypeChecker.check(itemStack);
    }

    public float getItemScore(ItemStack itemStack) {
        try {
            return this.itemScoreCalculator.getScore(itemStack);
        } catch (ClassCastException | IllegalArgumentException exception) {
            return -1.0F;
        }
    }

    public int getAmountToKeep() {
        return this.itemAmountChecker.getAmount();
    }

    public void addItem(ItemSlot itemSlot) {
        for (int i = 0; i < this.checkedItems.size(); i++) {
            ItemSlot checkedItem = this.checkedItems.get(i);
            if (this.getItemScore(itemSlot.getItemStack()) <= this.getItemScore(checkedItem.getItemStack())) {
                checkedItems.add(i, itemSlot);
                return;
            }
        }

        this.checkedItems.add(itemSlot);
    }

    public int getAmountOfItems() {
        return this.checkedItems.size();
    }

    public ItemSlot getFirst() {
        return this.checkedItems.get(0);
    }

    public ItemSlot getLast() {
        return this.checkedItems.get(this.checkedItems.size() - 1);
    }

    public ItemSlot removeFirst() {
        return this.checkedItems.remove(0);
    }

    public ItemSlot removeLast() {
        return this.checkedItems.remove(this.checkedItems.size() - 1);
    }

    public List<ItemSlot> getItems() {
        return this.checkedItems;
    }

    public void clear() {
        this.checkedItems.clear();
    }

}

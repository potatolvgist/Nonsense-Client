package wtf.bhopper.nonsense.util.minecraft.inventory;

import net.minecraft.item.ItemStack;

public class ItemSlot {

    // I don't have records yet ;-;

    private final ItemStack itemStack;
    private int slot;

    public ItemSlot(ItemStack itemStack, int slot) {
        this.itemStack = itemStack.copy();
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

}

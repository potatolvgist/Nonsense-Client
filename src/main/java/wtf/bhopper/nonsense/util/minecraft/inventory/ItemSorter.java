package wtf.bhopper.nonsense.util.minecraft.inventory;

public class ItemSorter {

    private final ItemTracker itemTracker;
    private final TargetSlotMethod targetSlotMethod;

    public ItemSorter(ItemTracker itemTracker, TargetSlotMethod targetSlotMethod) {
        this.itemTracker = itemTracker;
        this.targetSlotMethod = targetSlotMethod;
    }

    public ItemTracker getItemTracker() {
        return this.itemTracker;
    }

    public int getTargetSlot(int slot, int currentSlot) {
        return this.targetSlotMethod.getTargetSlot(slot, currentSlot);
    }

    public interface TargetSlotMethod {
        int getTargetSlot(int slot, int currentSlot);
    }

}

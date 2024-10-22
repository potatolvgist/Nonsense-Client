package wtf.bhopper.nonsense.util.minecraft.inventory;

public class ItemSwapper {

    private final ItemTracker itemTracker;
    private final TargetSlotMethod targetSlotMethod;

    public ItemSwapper(ItemTracker itemTracker, TargetSlotMethod targetSlotMethod) {
        this.itemTracker = itemTracker;
        this.targetSlotMethod = targetSlotMethod;
    }

    public ItemTracker getItemTracker() {
        return this.itemTracker;
    }

    public int getTargetSlot() {
        return this.targetSlotMethod.getTargetSlot() + InventoryUtil.HOTBAR_BEGIN - 1;
    }

    public interface TargetSlotMethod {
        int getTargetSlot();
    }

}

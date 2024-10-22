package wtf.bhopper.nonsense.util.minecraft.inventory;

public class InventoryAction {

    private final Action action;
    private final int delay;

    public InventoryAction(Action action, int delay) {
        this.action = action;
        this.delay = delay;
    }

    public void execute() {
        this.action.execute();
    }

    public int getDelay() {
        return this.delay;
    }

    public interface Action {
        void execute();
    }

}

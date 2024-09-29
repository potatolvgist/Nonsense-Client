package wtf.bhopper.nonsense.event;

import meteordevelopment.orbit.ICancellable;

public abstract class Cancellable implements ICancellable {

    private boolean cancelled;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

}

package wtf.bhopper.nonsense.event;

import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.ICancellable;
import wtf.bhopper.nonsense.Nonsense;

public class NonsenseEventBus extends EventBus {

    @Override
    public <T> T post(T event) {
        try {
            return super.post(event);
        } catch (NullPointerException exception) {
            Nonsense.LOGGER.error("NullPointerException in event", exception);
            return null;
        }
    }

    @Override
    public <T extends ICancellable> T post(T event) {
        try {
            return super.post(event);
        } catch (NullPointerException exception) {
            Nonsense.LOGGER.error("NullPointerException in event", exception);
            return null;
        }
    }
}

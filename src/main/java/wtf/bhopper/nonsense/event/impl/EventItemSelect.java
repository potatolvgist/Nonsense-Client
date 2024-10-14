package wtf.bhopper.nonsense.event.impl;

public class EventItemSelect {

    public boolean silent;
    public int slot;

    public EventItemSelect(int slot) {
        this.slot = slot;
        this.silent = false;
    }

}

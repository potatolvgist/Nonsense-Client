package wtf.bhopper.nonsense.event.impl;

public class EventClickAction {

    public Button button;
    public boolean click;
    public boolean usingItem;
    public boolean silentSwing;

    public EventClickAction(Button button, boolean click, boolean usingItem) {
        this.button = button;
        this.click = click;
        this.usingItem = usingItem;
        this.silentSwing = false;
    }

    public enum Button {
        LEFT,
        RIGHT,
        RELEASE
    }

}

package wtf.bhopper.nonsense.event.impl;

import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventPreClick extends Cancellable {

    public final Button button;
    public final MovingObjectPosition mouseOver;

    public EventPreClick(Button button, MovingObjectPosition mouseOver) {
        this.button = button;
        this.mouseOver = mouseOver;
    }

    public enum Button {
        LEFT,
        RIGHT
    }

}

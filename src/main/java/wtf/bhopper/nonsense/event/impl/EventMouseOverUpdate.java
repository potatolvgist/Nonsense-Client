package wtf.bhopper.nonsense.event.impl;

import net.minecraft.util.MovingObjectPosition;

public class EventMouseOverUpdate {

    public MovingObjectPosition objectMouseOver;

    public EventMouseOverUpdate(MovingObjectPosition objectMouseOver) {
        this.objectMouseOver = objectMouseOver;
    }

}

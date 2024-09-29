package wtf.bhopper.nonsense.event.impl;

import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D {

    public final float delta;
    public final ScaledResolution resolution;

    public EventRender2D(float delta, ScaledResolution resolution) {
        this.delta = delta;
        this.resolution = resolution;
    }

}

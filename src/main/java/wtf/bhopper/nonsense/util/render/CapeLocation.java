package wtf.bhopper.nonsense.util.render;

import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class CapeLocation {

    public final ResourceLocation cape;
    public final ResourceLocation overlay;
    public final Color overlayColor;

    public CapeLocation(ResourceLocation cape, ResourceLocation overlay, Color overlayColor) {
        this.cape = cape;
        this.overlay = overlay;
        this.overlayColor = overlayColor;
    }

}

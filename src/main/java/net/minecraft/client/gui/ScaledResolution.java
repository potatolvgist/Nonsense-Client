package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ScaledResolution
{
    private final double scaledWidthD;
    private final double scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public ScaledResolution(Minecraft mc)
    {
        this.scaledWidth = mc.displayWidth;
        this.scaledHeight = mc.displayHeight;
        this.scaleFactor = 1;
        boolean unicode = mc.isUnicode();
        int guiScale = mc.gameSettings.guiScale;

        if (guiScale == 0)
        {
            guiScale = 1000;
        }

        while (this.scaleFactor < guiScale && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240)
        {
            ++this.scaleFactor;
        }

        if (unicode && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
        {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    public int getScaledWidth()
    {
        return this.scaledWidth;
    }

    public int getScaledHeight()
    {
        return this.scaledHeight;
    }

    public double getScaledWidth_double()
    {
        return this.scaledWidthD;
    }

    public double getScaledHeight_double()
    {
        return this.scaledHeightD;
    }

    public int getScaleFactor()
    {
        return this.scaleFactor;
    }

    public void scaleToFactor(float factor) {
        float scale = (1.0F / this.scaleFactor) * factor;
        GlStateManager.scale(scale, scale, scale);
    }

    public int scaleMouse(int mouse, int factor) {
        return (mouse * this.scaleFactor) / factor;
    }
}

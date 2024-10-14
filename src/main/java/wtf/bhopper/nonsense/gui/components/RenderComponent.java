package wtf.bhopper.nonsense.gui.components;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.gui.font.TTFFontRenderer;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;

public abstract class RenderComponent {

    private final String name;
    private final IntSetting x;
    private final IntSetting y;

    private boolean enabled;
    private int width;
    private int height;

    public RenderComponent(String name) {
        this(name, 0, 0, 0, 0);
    }

    public RenderComponent(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = new IntSetting(name + " X Position", "X", Integer.MIN_VALUE, Integer.MAX_VALUE, x);
        this.y = new IntSetting(name + " Y Position", "Y", Integer.MIN_VALUE, Integer.MAX_VALUE, y);
        this.x.setDisplayed(false);
        this.y.setDisplayed(false);
        this.width = width;
        this.height = height;
    }

    public abstract void draw(ScaledResolution res, float delta);

    public void drawString(TTFFontRenderer font, String text, int x, int y, int color) {
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        font.drawStringWithShadow(text, this.getX() + (float)(x / 2), this.getY() + (float)(y / 2), color);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
    }

    public boolean mouseIntersecting(int mouseX, int mouseY) {
        return mouseX >= x.get() && mouseX <= x.get() + width && mouseY >= y.get() && mouseY <= y.get() + height;
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(this.getX() + x * 2, this.getY() + y * 2, this.getX() + (x + width) * 2, this.getY() + (y + height) * 2, color);
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getX() {
        return this.x.get();
    }

    public int getY() {
        return this.y.get();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setX(int x) {
        this.x.set(x);
    }

    public void setY(int y) {
       this.y.set(y);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}

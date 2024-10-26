package wtf.bhopper.nonsense.gui.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.font.TTFFontRenderer;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;

import java.awt.*;

public abstract class RenderComponent implements MinecraftInstance {

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
        Hud.addComponent(this);
    }

    public abstract void draw(ScaledResolution res, float delta, int mouseX, int mouseY, boolean bypass);

    public void onClick(int x, int y, int button) {}

    public void handleMouseClick(int x, int y, int button) {
        this.onClick(x - this.getX(), y - this.getY(), button);
    }

    public void drawString(FontRenderer font, String text, int x, int y, int color) {
        font.drawStringWithShadow(text, this.getX() + x, this.getY() + y, color);
    }

    public void drawString(String text, int x, int y, int color) {
        mc.fontRendererObj.drawStringWithShadow(text, this.getX() + x, this.getY() + y, color);
    }

    public boolean mouseIntersecting(int mouseX, int mouseY) {
        return mouseX >= x.get() && mouseX <= x.get() + width && mouseY >= y.get() && mouseY <= y.get() + height;
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(this.getX() + x, this.getY() + y, this.getX() + x + width, this.getY() + y + height, color);
    }

    public void drawBackground() {
        this.drawBackground(0x80000000);
    }

    public void drawBackground(int color) {
        Gui.drawRect(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
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

    public void drawOutline() {
        int x = this.getX();
        int y = this.getY();
        Gui.drawRect(x - 1, y - 1, x + this.width + 1, y, 0xAAAAAAAA);
        Gui.drawRect(x - 1, y + this.height, x + this.width + 1, y + this.height + 1, 0xAAAAAAAA);
        Gui.drawRect(x - 1, y, x, y + this.height, 0xAAAAAAAA);
        Gui.drawRect(x + this.width, y, x + this.width + 1, y + this.height, 0xAAAAAAAA);
    }

}

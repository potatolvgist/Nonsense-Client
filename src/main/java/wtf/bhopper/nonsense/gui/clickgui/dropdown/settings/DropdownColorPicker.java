package wtf.bhopper.nonsense.gui.clickgui.dropdown.settings;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownPanel;
import wtf.bhopper.nonsense.module.setting.impl.ColorSetting;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.awt.*;

public class DropdownColorPicker extends DropdownComponent {

    public static final int COLOR_PICKER_SIZE = 150;
    public static final int COLOR_PICKER_HEIGHT = HEIGHT + COLOR_PICKER_SIZE + 10;

    private final ColorSetting setting;
    private boolean expanded = false;

    private boolean hueSelected = false;
    private boolean sbSelected = false;

    public DropdownColorPicker(DropdownPanel panel, ColorSetting setting, boolean inGroup) {
        super(panel, inGroup);
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        this.updatePosition();
        this.drawBackground(this.expanded ? COLOR_PICKER_HEIGHT : HEIGHT);

        String name = DropdownClickGui.mod().displayNames.get() ? this.setting.displayName : this.setting.name;

        Gui.drawRect(this.x + DropdownClickGui.WIDTH - 9 - HEIGHT, this.y + 3 , this.x + DropdownClickGui.WIDTH - 15, y + HEIGHT - 3, this.setting.getRgb());
        DropdownClickGui.drawString(name, this.x + (inGroup ? 18 : 14), this.y + HEIGHT / 2.0F - DropdownClickGui.font().getHeight(name), -1);

        if (this.expanded) {

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer renderer = tessellator.getWorldRenderer();

            int hueX = this.x + DropdownClickGui.WIDTH - 25;
            int sbLeft = this.x + 15;
            int top = this.y + HEIGHT + 5;

            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();

            // Hue bar
            GL11.glLineWidth(20.0F);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (int y = 0; y < COLOR_PICKER_SIZE; y++) {
                Color color = Color.getHSBColor((float)y / (float)COLOR_PICKER_SIZE, 1.0F, 1.0F);
                renderer.pos(hueX, top + y, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 0xFF).endVertex();
            }
            tessellator.draw();
            GL11.glLineWidth(1.0F);

            // Saturation & Brightness picker
            float hue = this.setting.getHsb()[0];
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            for (int y = 0; y < COLOR_PICKER_SIZE; y++) {
                float factor = 1.0F - (float)y / (float)COLOR_PICKER_SIZE;
                Color leftColor = Color.getHSBColor(hue, 0.0F, factor);
                Color rightColor = Color.getHSBColor(hue, 1.0F, factor);
                renderer.pos(sbLeft, top + y, 0.0).color(leftColor.getRed(), leftColor.getGreen(), leftColor.getBlue(), 0xFF).endVertex();
                renderer.pos(sbLeft + COLOR_PICKER_SIZE, top + y, 0.0).color(rightColor.getRed(), rightColor.getGreen(), rightColor.getBlue(), 0xFF).endVertex();
            }
            tessellator.draw();

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            float[] hsb = this.setting.getHsb();

            float sbX = sbLeft + (float)COLOR_PICKER_SIZE * hsb[1];
            float sbY = top + (float)COLOR_PICKER_SIZE * (1.0F - hsb[2]);
            RenderUtil.drawCircle(sbX, sbY, 2, 0xFF000000);

            float hueY = top + (float)COLOR_PICKER_SIZE * hsb[0];
            Gui.drawRect(hueX - 5, (int)hueY, hueX + 5, (int)hueY + 1, 0xFF000000);
        } else {
            this.hueSelected = false;
            this.sbSelected = false;
        }

        this.panel.drawY += this.expanded ? COLOR_PICKER_HEIGHT : HEIGHT;

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        if (this.mouseIntersecting(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.expanded = !this.expanded;
                if (!this.expanded) {
                    this.hueSelected = false;
                    this.sbSelected = false;
                }
            }
        } else if (mouseButton == 0) {
            int hueLeft = this.x + DropdownClickGui.WIDTH - 15;
            int sbLeft = this.x + 15;
            int top = this.y + HEIGHT + 5;

            if (this.mouseIntersecting(mouseX, mouseY, hueLeft, top, hueLeft + 20, top + COLOR_PICKER_SIZE)) {
                this.hueSelected = true;
            } else if (this.mouseIntersecting(mouseX, mouseY, sbLeft, top, sbLeft + COLOR_PICKER_SIZE, top + COLOR_PICKER_SIZE)) {
                this.sbSelected = true;
            }

        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        this.hueSelected = false;
        this.sbSelected = false;
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (DropdownClickGui.shouldHide(this.setting)) {
            return;
        }

        if (this.expanded) {
            if (this.hueSelected) {
                int top = this.y + HEIGHT + 5;
                float hue = MathHelper.clamp_float((float) (mouseY - top) / (float) COLOR_PICKER_SIZE, 0.0F, 1.0F);
                float[] hsb = this.setting.getHsb();
                this.setting.set(hue, hsb[1], hsb[2], 1.0F);

            } else if (this.sbSelected) {
                int sbLeft = this.x + 15;
                int top = this.y + HEIGHT + 5;
                float saturation = MathHelper.clamp_float((float) (mouseX - sbLeft) / (float) COLOR_PICKER_SIZE, 0.0F, 1.0F);
                float brightness = MathHelper.clamp_float(1.0F - ((float) (mouseY - top) / (float) COLOR_PICKER_SIZE), 0.0F, 1.0F);
                float[] hsb = this.setting.getHsb();
                this.setting.set(hsb[0], saturation, brightness, 1.0F);
            }
        }

    }
}

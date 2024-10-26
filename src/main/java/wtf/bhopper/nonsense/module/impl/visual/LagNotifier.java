package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.components.RenderComponent;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;

import java.text.DecimalFormat;

public class LagNotifier extends Module {

    private final IntSetting scale = new IntSetting("Scale", "scale", 1, 3, 2);

    private final Render render = new Render();

    public LagNotifier() {
        super("Lag Notifier", "Notifies you about lag", Category.VISUAL);
        this.addSettings(scale);
    }

    @Override
    public void onEnable() {
        this.render.setEnabled(true);
    }

    @Override
    public void onDisable() {
        this.render.setEnabled(false);
    }

    public class Render extends RenderComponent {

        private final DecimalFormat format = new DecimalFormat("#0.0");

        public Render() {
            super("Lag Notifier", 100, 100, mc.fontRendererObj.getStringWidth("Lag Detected: 00.0") * 2, mc.fontRendererObj.FONT_HEIGHT * 2);
        }

        @Override
        public void draw(ScaledResolution res, float delta, int mouseX, int mouseY, boolean bypass) {

            long timeSinceLastTick = Nonsense.INSTANCE.tickRate.timeSinceLastTickMS();

            if (timeSinceLastTick > 1000 || bypass) {

                int color;
                if (timeSinceLastTick > 10000) {
                    color = 0xFFFF0000;
                } else if (timeSinceLastTick > 3000) {
                    color = 0xFFFF5500;
                } else if (timeSinceLastTick > 1000) {
                    color = 0xFFFFFF00;
                } else {
                    color = 0xFF00FF00;
                }

                String text = "Lag Detected: " + format.format(timeSinceLastTick / 1000.0);

                this.setWidth(mc.fontRendererObj.getStringWidth(text) * scale.get());
                this.setHeight(mc.fontRendererObj.FONT_HEIGHT * scale.get());

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale.get(), scale.get(), scale.get());
                mc.fontRendererObj.drawStringWithShadow(text, this.getX() / (float)scale.get(), this.getY() / (float)scale.get(), color);
                GlStateManager.popMatrix();

            }

        }
    }

}

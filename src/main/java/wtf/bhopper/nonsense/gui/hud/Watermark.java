package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.ScaledResolution;
import wtf.bhopper.nonsense.util.render.ColorUtil;

public class Watermark {

    public void draw(ScaledResolution res) {

        if (!Hud.mod().isEnabled() || !Hud.mod().watermarkEnable.get()) {
            return;
        }

        String rawText = Hud.mod().watermarkText.get();

        if (rawText.isEmpty()) {
            return;
        }

        Hud.beginDraw(res);

        String text;
        int color;

        switch (Hud.mod().watermarkColorMode.get()) {
            case COLOR:
                text = "\247l" + rawText.charAt(0) + "\247r\247f" + rawText.substring(1);
                color = Hud.mod().watermarkColor.getRgb();
                break;

            case SOLID:
                text = rawText;
                color = Hud.mod().watermarkColor.getRgb();
                break;

            case RAINBOW:
                text = "\247l" + rawText.charAt(0) + "\247r\247f" + rawText.substring(1);
                color = ColorUtil.rainbow(System.currentTimeMillis(), 0, 0.5F, 1.0F);
                break;

            case RAINBOW_2:
                text = "\247l" + rawText.charAt(0) + "\247r\247f" + rawText.substring(1);
                color = ColorUtil.rainbow(System.currentTimeMillis(), 0, 1.0F, 1.0F);
                break;

            default:
                text = rawText;
                color = -1;
                break;
        }

        Hud.drawString(text, 4.0F, 4.0F, color, true);

        Hud.endDraw();

    }

}

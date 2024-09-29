package wtf.bhopper.nonsense.module.impl.visual;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;

public class NoRender extends Module {

    private final BooleanSetting hurtCam = new BooleanSetting("Hurt Camera", "Prevents your screen from shaking when you take damage", true);
    private final BooleanSetting weather = new BooleanSetting("Weather", "Disables rain and thunder", false);

    public NoRender() {
        super("No Render", "Prevents specific things from rendering", Category.VISUAL);
        this.addSettings(hurtCam, weather);
    }

    @EventHandler
    public void onPreTick(EventPreTick event) {
        if (weather.get()) {
            mc.theWorld.getWorldInfo().setRaining(false);
            mc.theWorld.setThunderStrength(0.0F);
            mc.theWorld.setRainStrength(0.0F);
        }
    }

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {
        if (this.weather.get() && event.packet instanceof S2BPacketChangeGameState) {
            S2BPacketChangeGameState packet = (S2BPacketChangeGameState)event.packet;
            int state = packet.getGameState();
            if (state == 1 || state == 2 || state == 7 || state == 8) {
                event.cancel();
            }
        }
    }

    public boolean hurtCamera() {
        return this.isEnabled() && this.hurtCam.get();
    }

}

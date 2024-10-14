package wtf.bhopper.nonsense.module.impl.visual;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.util.Description;
import wtf.bhopper.nonsense.module.setting.util.DisplayName;
import wtf.bhopper.nonsense.util.render.CapeLocation;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.awt.*;

public class Capes extends Module {

    public final EnumSetting<Cape> cape = new EnumSetting<>("Cape", "There's too many of them...", Cape.NONSENSE);

    // Frame counter for animated capes
    private int frameCounter = 0;

    public Capes() {
        super("Capes", "Client side capes", Category.VISUAL);
        this.addSettings(cape);
        this.toggle(true);
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        ICape cape = this.cape.get().cape;
        if (cape instanceof AnimatedCape) {
            if (mc.thePlayer.ticksExisted % ((AnimatedCape) cape).tickDelay == 0) {
                frameCounter++;
            }
        }
    }

    public enum Cape {
        ASTOLFO,
        CROSSSINE,
        DAYS,
        DIABLO,
        DORTWARE,
        EXHIBITION(new OverlayCape("exhibition", () -> new Color(ColorUtil.rainbow(System.currentTimeMillis(), 0, 0.5F, 1.0F)))),
        FUTURE,
        MINECON_2011,
        MINECON_2012,
        MINECON_2013,
        MINECON_2015,
        MINECON_2016,
        MONSOON,
        MOON,
        NONSENSE,
        NOVOLINE,
        @DisplayName("OptiFine") OPTIFINE,
        REACTOR,
        RISE_5(new AnimatedCape("rise_5", 14, 3)),
        RISE_6,
        SKIDWARE,
        @Description("Ok who tf would use this though?") TEMPLATE,
        TENACITY;

        public final ICape cape;

        Cape() {
            this.cape = new StaticCape(this.name().toLowerCase());
        }

        Cape(ICape cape) {
            this.cape = cape;
        }

        public CapeLocation getResource() {
            return this.cape.getResource();
        }

        public enum Type {
            STATIC,
            OVERLAY,
            ANIMATED
        }

    }

    public static class StaticCape implements ICape {

        private final ResourceLocation location;

        public StaticCape(String name) {
            this.location = new ResourceLocation(String.format("nonsense/capes/%s.png", name));
        }

        @Override
        public CapeLocation getResource() {
            return new CapeLocation(location, null, Color.WHITE);
        }
    }

    public static class OverlayCape implements ICape {

        private final ResourceLocation location;
        private final ResourceLocation overlay;
        private final OverlayColor color;

        public OverlayCape(String name, OverlayColor color) {
            this.location = new ResourceLocation(String.format("nonsense/capes/%s.png", name));
            this.overlay = new ResourceLocation(String.format("nonsense/capes/%s_overlay.png", name));
            this.color = color;
        }

        @Override
        public CapeLocation getResource() {
            return new CapeLocation(location, overlay, color.getColor());
        }

        public interface OverlayColor {
            Color getColor();
        }
    }

    public static class AnimatedCape implements ICape {

        private final ResourceLocation[] locations;
        private final int frames;
        private final int tickDelay;

        public AnimatedCape(String name, int frames, int tickDelay) {
            this.locations = new ResourceLocation[frames];
            this.frames = frames;
            this.tickDelay = tickDelay;
            for (int i = 0; i < frames; i++) {
                locations[i] = new ResourceLocation(String.format("nonsense/capes/%s/%d.jpg", name, i + 1));
            }
        }


        @Override
        public CapeLocation getResource() {
            int frameCounter = Nonsense.module(Capes.class).frameCounter;
            return new CapeLocation(locations[frameCounter % frames], null, Color.WHITE);
        }
    }

    public interface ICape {
        CapeLocation getResource();
    }

}

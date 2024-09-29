package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.render.ColorUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleList {

    public static final float ANIMATION_FACTOR = 0.1F;

    private final List<Slot> slots = new ArrayList<>();

    private int right = 0;
    private float offsetY = 0.0F;

    public void init() {
        Nonsense.INSTANCE.moduleManager.values().forEach(module -> slots.add(new Slot(module)));
    }

    public void draw(float delta, ScaledResolution sr) {

        if (!Hud.enabled() || !Hud.mod().moduleListEnabled.get()) {
            return;
        }

        Hud.beginDraw(sr);

        this.right = sr.getScaledWidth() * sr.getScaleFactor();

        this.updateSlots(delta);

        this.offsetY = Hud.mod().moduleListSpacing.get() / 2.0F;
        for (Slot slot : slots) {
            if (slot.shouldDisplay()) {
                slot.draw();
            }
        }

        Hud.endDraw();

    }

    public void updateSlots(float delta) {
        this.slots.forEach(slot -> slot.updateText(delta));
        switch (Hud.mod().moduleListSorting.get()) {
            case LENGTH:
                this.slots.sort(Comparator.comparingDouble(Slot::getTextWidth).reversed());
                break;

            case ABC:
                this.slots.sort(Comparator.comparing(Slot::getText));
                break;
        }
        long timeMS = System.currentTimeMillis();
        int count = 0;
        for (Slot slot : this.slots) {
            if (slot.shouldDisplay()) {
                slot.updateColor(timeMS, count);
                ++count;
            }
        }
    }

    public class Slot {
        private final Module module;

        private boolean shouldDisplay;
        private String text;
        private float textWidth;
        private int color;

        private float animateFactor = 0.0F;

        public Slot(Module module) {
            this.module = module;
            this.updateText(1.0F);
        }

        public void updateText(float delta) {

            if (this.module.isHidden()) {
                this.shouldDisplay = false;
                this.text = "";
                this.textWidth = 0.0F;
                return;
            }

            if (Hud.mod().moduleListAnimated.get()) {

                if (this.module.isEnabled()) {
                    if (this.animateFactor != 1.0F) {
                        this.animateFactor = Math.min(this.animateFactor + ANIMATION_FACTOR * delta, 1.0F);
                    }
                } else {
                    if (this.animateFactor != 0.0F) {
                        this.animateFactor = Math.max(this.animateFactor - ANIMATION_FACTOR * delta, 0.0F);
                    }
                }

                this.shouldDisplay = this.animateFactor > 0.0F;
            } else {
                this.shouldDisplay = this.module.isEnabled();
                this.animateFactor = 1.0F;
            }

            if (!this.shouldDisplay) {
                this.text = "";
                this.textWidth = 0.0F;
                return;
            }

            this.text = Hud.mod().moduleListDisplayNames.get() ? module.displayName : module.name;
            String suffix = module.getSuffix();

            if (suffix != null) {
                switch (Hud.mod().moduleListSuffixes.get()) {
                    case NORMAL:
                        this.text += " \2477" + suffix;
                        break;

                    case HYPHEN:
                        this.text += " \2478- \2477" + suffix;
                        break;

                    case BRACKET:
                        this.text += " \2478(\2477" + suffix + "\2478)";
                        break;

                    case SQUARE_BRACKET:
                        this.text += " \2478[\2477" + suffix + "\2478]";
                        break;
                }
            }

            if (Hud.mod().moduleListLowerCase.get()) {
                this.text = this.text.toLowerCase();
            }

            this.textWidth = Hud.getStringWidth(this.text);
        }

        private void updateColor(long timeMS, int count) {

            switch (Hud.mod().moduleListColorMode.get()) {
                case STATIC:
                    this.color = Hud.mod().moduleListColor.getRgb();
                    break;

                case WAVY:
                    this.color = ColorUtil.wavyColor(Hud.mod().moduleListColor.getRgb(), timeMS, count);
                    break;

                case RAINBOW:
                    this.color = ColorUtil.rainbowColor(timeMS, count, 0.5F, 1.0F);
                    break;

                case RAINBOW_2:
                    this.color = ColorUtil.rainbowColor(timeMS, count, 1.0F, 1.0F);
                    break;

                case CATEGORY:
                    this.color = this.module.category.color;
                    break;

                case ASTOLFO:
                    this.color = ColorUtil.astolfoColor(timeMS, count);
                    break;

                case RISE:
                    this.color = ColorUtil.riseColor(timeMS, count);
                    break;

                case RANDOM:
                    this.color = this.module.hashCode() | 0xFF000000;
                    break;

                case TRANS:
                    switch (count % 3) {
                        case 0:
                            this.color = 0xFF5BCEFA;
                            break;

                        case 1:
                            this.color = 0xFFF5A9B8;
                            break;

                        case 2:
                            this.color = 0xFFFFFFFF;
                            break;
                    }
                    break;

                default:
                    this.color = -1;
                    break;
            }
        }

        public void draw() {
            int spacing = Hud.mod().moduleListSpacing.get();
            int height = (int)Hud.getStringHeight("I") + spacing;
            int width = (int)Hud.getStringWidth(this.text);
            if (Hud.mod().moduleListBackground.get() > 0) {
                Gui.drawRect((int) (right - (width + 4.0F) * this.animateFactor), (int) ModuleList.this.offsetY - spacing / 2, right, (int) (ModuleList.this.offsetY + height - spacing / 2), Hud.mod().moduleListBackground.get() << 24);
            }
            Hud.drawString(this.text, right - (width + 1.0F) * this.animateFactor, ModuleList.this.offsetY, this.color, true);
            ModuleList.this.offsetY += height * animateFactor;
        }

        public boolean shouldDisplay() {
            return this.shouldDisplay;
        }

        public String getText() {
            return this.text;
        }

        public float getTextWidth() {
            return this.textWidth;
        }

        public int getColor() {
            return this.color;
        }

    }

}

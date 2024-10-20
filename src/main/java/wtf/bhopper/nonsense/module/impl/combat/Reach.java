package wtf.bhopper.nonsense.module.impl.combat;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;

import java.util.concurrent.ThreadLocalRandom;

public class Reach extends Module {

    private final FloatSetting min = new FloatSetting("Min Reach", "Minimum attacks per second", 3.0F, 6.0F, 3.1F, Setting.DEFAULT_FORMAT, value -> {
        if (this.max.get() < value) {
            this.max.set(value);
        }
    });

    private final FloatSetting max = new FloatSetting("Max Reach", "Maximum attacks per second", 3.0F, 6.0F, 3.2F, Setting.DEFAULT_FORMAT, value -> {
        if (this.min.get() > value) {
            this.min.set(value);
        }
    });

    public Reach() {
        super("Reach", "Increases your interaction range", Category.COMBAT);
        this.addSettings(this.min, this.max);
    }

    public double getReach() {
        if (!this.isEnabled()) {
            return 3.0;
        }

        if (min.get().equals(max.get())) {
            return min.get();
        }

        return ThreadLocalRandom.current().nextDouble(min.get(), max.get());
    }

    @Override
    public String getSuffix() {
        if (min.get().equals(max.get())) {
            return min.getDisplayValue();
        }

        return min.getDisplayValue() + "-" + max.getDisplayValue();
    }
}

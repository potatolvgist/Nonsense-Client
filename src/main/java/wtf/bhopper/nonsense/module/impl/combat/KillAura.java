package wtf.bhopper.nonsense.module.impl.combat;

import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.util.Description;

public class KillAura extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.SINGLE);

    public KillAura() {
        super("Kill Aura", "Automatically attacks nearby entities", Category.COMBAT);
        this.addSettings(mode);
    }

    private enum Mode {
        @Description("Selects a target and keeps attacking it until it dies or becomes invalid") SINGLE,
        @Description("Switches between multiple targets") SWITCH
    }

}

package wtf.bhopper.nonsense.module;

import wtf.bhopper.nonsense.module.setting.Setting;

public abstract class ModuleMode<M extends Module> {

    protected final M owner;
    private final EnableCheck enableCheck;

    public ModuleMode(M owner, EnableCheck enableCheck) {
        this.owner = owner;
        this.enableCheck = enableCheck;
    }

    public void onEnable() {}
    public void onDisable() {}

    public boolean checkEnabled() {
        return !enableCheck.isEnabled();
    }

    protected void addSettings(Setting<?>... settings) {
        this.owner.addSettings(settings);
    }

    public interface EnableCheck {
        boolean isEnabled();
    }

}

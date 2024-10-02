package wtf.bhopper.nonsense.module.impl.other;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;

public class Panic extends Module {

    private final BooleanSetting disableVisuals = new BooleanSetting("Disable Visuals", "Disables visuals too", false);

    public Panic() {
        super("Panic", "Disables all modules", Category.OTHER);
        this.addSettings(this.disableVisuals);
    }

    @Override
    public void onEnable() {
        this.toggle(false);
        for (Module module : Nonsense.INSTANCE.moduleManager.values()) {
            if (module.category != Category.VISUAL || this.disableVisuals.get()) {
                module.toggle(false);
            }
        }

        Notification.send("Panic", "Disabled all modules", NotificationType.WARNING, 5000);
    }
}

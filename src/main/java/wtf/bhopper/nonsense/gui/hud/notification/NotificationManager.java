package wtf.bhopper.nonsense.gui.hud.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public void addNotification(Notification notification) {
        if (!Hud.enabled() || !Hud.mod().notificationEnabled.get())  {
            return;
        }

        this.notifications.add(0, notification);

        try {
            if (!Hud.mod().notificationSound.is(HudMod.NotificationSound.NONE)) {
                mc.getSoundHandler().playSound(Hud.mod().notificationSound.get().createSoundRecord());
            }
        } catch (Exception ignored) {}
    }

    public void draw(float delta, ScaledResolution res) {
        if (!Hud.enabled() || !Hud.mod().notificationEnabled.get())  {
            return;
        }

        this.notifications.removeIf(Notification::isDone);

        Hud.beginDraw(res);
        int offset = res.getScaledHeight() * res.getScaleFactor() - 72;
        for (Notification notification : this.notifications) {
            offset = notification.draw(delta, res, offset);
        }
        Hud.endDraw();
    }

}

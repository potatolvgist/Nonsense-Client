package wtf.bhopper.nonsense.module.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;

import java.util.Comparator;

public class AutoCopsAndCrims extends Module {

    public AutoCopsAndCrims() {
        super("Auto Cops and Crims", "Cops and crims aim bot", Category.COMBAT);
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        if (mc.gameSettings.keyBindUse.isKeyDown()) {

            EntityPlayer target = mc.theWorld.getEntities(EntityPlayer.class, input -> !PlayerUtil.isOnSameTeam(input) && mc.thePlayer.canEntityBeSeen(input) && !Nonsense.module(AntiBot.class).isBot(input))
                    .stream()
                    .min(Comparator.comparingDouble(o -> Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - RotationUtil.getRotations(o).yaw)))
                    .orElse(null);

            if (target == null) {
                return;
            }

            event.setRotations(RotationUtil.getRotations(target));

        }
    }

}

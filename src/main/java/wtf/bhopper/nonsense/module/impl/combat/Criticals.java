package wtf.bhopper.nonsense.module.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreClick;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventSendPacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class Criticals extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.PACKET);
    private final IntSetting delay = new IntSetting("Delay", "delay between crits in ticks", 0, 20, 15);
    private final BooleanSetting safe = new BooleanSetting("Safe", "Doesn't crit if target is hurt", false);

    private int ticks = 0;
    private boolean attacked = true;
    private int stage = 0;

    public Criticals() {
        super("Criticals", "Makes you do critical hits", Category.COMBAT);
        this.addSettings(this.mode, this.delay, this.safe);
        this.mode.updateChange();
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        ticks--;
    }

    @EventHandler
    public void onClick(EventPreClick event) {

        if (event.mouseOver != null && event.mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {

            Entity entity = event.mouseOver.entityHit;

            if (!(entity instanceof EntityLivingBase)) {
                return;
            }

            if (Nonsense.module(AntiBot.class).isBot(entity)) {
                return;
            }

            if (ticks > 0) {
                return;
            }

            if (safe.get() && entity.hurtResistantTime > 0) {
                return;
            }

            switch (mode.get()) {
                case PACKET:
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    break;
                    
                    
                case LOW:
                    attacked = true;
                    break;
            }

        }
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {

        switch (mode.get()) {
            case LOW:
                if (mc.thePlayer.onGround && attacked) {

                    switch (stage) {
                        case 0:
                            event.y += 5.0E-4;
                            break;

                        case 1:
                            event.y += 1.0E-4;
                            attacked = false;
                            break;
                    }

                } else {
                    attacked = false;
                    stage = 0;
                }
                break;
        }

    }

    @Override
    public String getSuffix() {
        return mode.getDisplayValue() + " " + (safe.get() ? "Safe" : delay.getDisplayValue());
    }

    private enum Mode {
        PACKET,
        LOW
    }

}

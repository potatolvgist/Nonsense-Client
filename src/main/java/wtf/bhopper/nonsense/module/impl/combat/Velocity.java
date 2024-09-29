package wtf.bhopper.nonsense.module.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.module.setting.impl.FloatSetting;
import wtf.bhopper.nonsense.module.setting.impl.IntSetting;

import java.text.DecimalFormat;

public class Velocity extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "Mode", Mode.PACKET, new EnumSetting.ChangedCallback<Mode>() {
        @Override
        public void onChanged(Mode value) {
            horizontal.setDisplayed(value == Mode.PACKET);
            vertical.setDisplayed(value == Mode.PACKET);
            airTicks.setDisplayed(value == Mode.AIR);
        }
    });

    private final FloatSetting horizontal = new FloatSetting("Horizontal", "Horizontal velocity", 0.0F, 100.0F, 0.0F, new DecimalFormat("#0.##'%'"));
    private final FloatSetting vertical = new FloatSetting("Vertical", "Vertical velocity", 0.0F, 100.0F, 0.0F, new DecimalFormat("#0.##'%'"));
    private final IntSetting airTicks = new IntSetting("Ticks", "Air ticks", 1, 20, 5);

    private int ticks = 0;
    private boolean cancel = false;

    public Velocity() {
        super("Velocity", "Modifies your knockback", Category.COMBAT);
        this.addSettings(mode, horizontal, vertical, airTicks);
        this.mode.updateChange();
    }

    @EventHandler
    public void onPreTick(EventPreTick event) {
        if (!mc.thePlayer.onGround) {
            ++ticks;
        } else {
            ticks = 0;
        }

        if (ticks >= airTicks.get()) {
            cancel = true;
        } else if (ticks == 0) {
            cancel = false;
        }
    }

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {

        if (event.packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.packet;

            if (packet.getEntityID() != mc.thePlayer.getEntityId()) {
                return;
            }

            switch (mode.get()) {
                case PACKET:
                    event.cancel();

                    if (horizontal.get() != 0.0F) {
                        double h = horizontal.get() / 100.0;
                        double mx = (double)packet.getMotionX() / 8000.0;
                        double mz = (double)packet.getMotionZ() / 8000.0;
                        mc.thePlayer.motionX = mx * h;
                        mc.thePlayer.motionZ = mz * h;
                    }

                    if (vertical.get() != 0.0F) {
                        double v = vertical.get() / 100.0;
                        double my = (double)packet.getMotionY() / 8000.0;
                        mc.thePlayer.motionY = my * v;
                    }

                    break;

                case AIR:
                    event.cancel();
                    if (!cancel)  {
                        mc.thePlayer.motionY = (double)packet.getMotionY() / 8000.0;
                    }
                    break;
            }

        }

    }

    @Override
    public String getSuffix() {
        if (mode.is(Mode.PACKET)) {
            return horizontal.getDisplayValue() + " " + vertical.getDisplayValue();
        }

        return mode.getDisplayValue();
    }

    enum Mode {
        PACKET,
        AIR
    }

}

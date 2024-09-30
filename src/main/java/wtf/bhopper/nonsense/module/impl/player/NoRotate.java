package wtf.bhopper.nonsense.module.impl.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.bhopper.nonsense.event.impl.EventPreMotion;
import wtf.bhopper.nonsense.event.impl.EventReceivePacket;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.EnumSetting;
import wtf.bhopper.nonsense.util.minecraft.client.PacketUtil;

public class NoRotate extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", "mode", Mode.VANILLA);

    public NoRotate() {
        super("No Rotate", "Prevents the server from updating your rotations", Category.PLAYER);
        this.addSettings(this.mode);
    }

    private boolean edit = false;
    private float yaw = 0.0F;
    private float pitch = 0.0F;

    @EventHandler
    public void onReceivePacket(EventReceivePacket event) {
        if (event.packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.packet;
            event.packet = new S08PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, packet.func_179834_f());

            switch (mode.get()) {
                case EDIT:
                    this.edit = true;
                    this.yaw = packet.getYaw();
                    this.pitch = packet.getPitch();
                    break;

                case PACKET:
                    PacketUtil.send(new C03PacketPlayer.C05PacketPlayerLook(packet.getYaw(), packet.getPitch(), mc.thePlayer.onGround));
                    break;
            }
        }
    }

    @EventHandler
    public void onPreMotion(EventPreMotion event) {
        if (this.edit) {
            event.yaw = this.yaw;
            event.pitch = this.pitch;
            this.edit = false;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getDisplayValue();
    }

    enum Mode {
        VANILLA,
        EDIT,
        PACKET
    }

}

package wtf.bhopper.nonsense.module.impl.visual;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.event.impl.EventRender3D;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

import static org.lwjgl.opengl.GL11.*;

public class Trajectories extends Module {

    public Trajectories() {
        super("Trajectories", "Shows where a projectile will land", Category.VISUAL);
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {

        PlayerUtil.PredictionResult prediction = PlayerUtil.predictProjectilePath(event.delta);
        if (prediction == null) {
            return;
        }

        double x = prediction.pos.xCoord - mc.getRenderManager().viewerPosX;
        double y = prediction.pos.yCoord - mc.getRenderManager().viewerPosY;
        double z = prediction.pos.zCoord - mc.getRenderManager().viewerPosZ;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        GlStateManager.color(1.0F, 1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
        renderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        for (Vec3 point : prediction.path) {
            renderer.pos(x + point.xCoord, y + point.yCoord, z + point.zCoord);
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

    }

}

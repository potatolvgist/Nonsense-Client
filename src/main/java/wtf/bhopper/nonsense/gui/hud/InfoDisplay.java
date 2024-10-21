package wtf.bhopper.nonsense.gui.hud;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.impl.visual.HudMod;
import wtf.bhopper.nonsense.util.minecraft.MinecraftInstance;
import wtf.bhopper.nonsense.util.minecraft.player.MoveUtil;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;
import wtf.bhopper.nonsense.util.minecraft.player.RotationUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InfoDisplay implements MinecraftInstance {

    public void draw(ScaledResolution sr) {

        // Don't display if not in game or the chat is open
        if (!Hud.enabled() || !PlayerUtil.canUpdate() || mc.currentScreen instanceof GuiChat) {
            return;
        }

        Hud.beginDraw(sr);
        this.drawLeft(sr);
        this.drawRight(sr);
        Hud.endDraw();
    }

    public void drawArmor(ScaledResolution sr) {
        if (!Hud.enabled() || !PlayerUtil.canUpdate()) {
            return;
        }

        int left = sr.getScaledWidth() / 2 + 85;

        int offset = 16;
        for (ItemStack stack : mc.thePlayer.inventory.armorInventory) {
            if (stack != null) {
                int x = left - offset;
                int y = sr.getScaledHeight() - (mc.thePlayer.capabilities.isCreativeMode ? 40 : mc.thePlayer.isInsideOfMaterial(Material.water) ? 68 : 56);
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.bitFontRenderer, stack, x, y, null);
                offset += 18;
            }
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    }

    public void drawLeft(ScaledResolution sr) {

        int y = 0;
        int height = sr.getScaledHeight() * sr.getScaleFactor();

        if (!Hud.mod().infoCoordinates.is(HudMod.Coordinates.NONE)) {
            String text = String.format("XYZ\247f: %,.1f\2477, \247f%,.1f\2477, \247f%,.1f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

            if (Hud.mod().infoCoordinates.is(HudMod.Coordinates.DIMENSIONS) && mc.thePlayer.dimension != 2) {
                double factor = mc.thePlayer.dimension == 1 ? 8.0 : 0.125;
                double posX = mc.thePlayer.posX * factor;
                double posZ = mc.thePlayer.posZ * factor;
                text += String.format(" \2477[\247f%d\2477, \247f%d\2477]", (int)Math.floor(posX), (int)Math.floor(posZ));
            }
            float textHeight = Hud.getStringHeight(text);
            Hud.drawString(text, 2, height - y - textHeight - 2, Hud.color(), true);
            y += textHeight + 2;
        }

        if (Hud.mod().infoAngles.get()) {
            float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
            float pitch = mc.thePlayer.rotationPitch;

            String direction;

            switch (mc.thePlayer.getHorizontalFacing()) {
                case NORTH:
                    direction = "\2477[\247fZ-\2477] \247fNorth";
                    break;

                case SOUTH:
                    direction = "\2477[\247fZ+\2477] \247fSouth";
                    break;

                case EAST:
                    direction = "\2477[\247fX+\2477] \247fEast";
                    break;

                case WEST:
                    direction = "\2477[\247fX-\2477] \247fWest";
                    break;

                default:
                    direction = "\2477[\247f?\2477] \247f???";
                    break;
            }

            String text = String.format("Angles\247f: \2477(\247f%.1f \2477/ \247f%.1f\2477) \247f%s", yaw, pitch, direction);

            float textHeight = Hud.getStringHeight(text);
            Hud.drawString(text, 2, height - y - textHeight - 2, Hud.color(), true);
            y += textHeight + 2;
        }

        if (!Hud.mod().infoSpeed.is(HudMod.Speed.NONE)) {
            double speed = MoveUtil.getSpeed() * mc.timer.timerSpeed;
            String suffix = "";
            switch (Hud.mod().infoSpeed.get()) {
                case MPS:
                    speed *= 20.0;
                    suffix = " m/s";
                    break;

                case KMPH:
                    speed *= 72.0; // You can convert m/s to km/h by multiplying it by 3.6, we also need to multiply by 20 for the ticks
                    suffix = " km/h";
                    break;

                case MPH:
                    speed *= 44.74; // You can convert m/s to mph by multiplying it by 2.237, we also need to multiply by 20 for the ticks
                    suffix = " mph";
                    break;
            }
            String text = String.format("Speed\247f: %.2f%s", speed, suffix);
            float textHeight = Hud.getStringHeight(text);
            Hud.drawString(text, 2, height - y - textHeight - 2, Hud.color(), true);
            y += textHeight + 2;
        }

        if (Hud.mod().infoTps.get()) {
            float tps = Nonsense.INSTANCE.tickRate.getTickRate();
            String text = String.format("TPS\247f:\247f %.2f", tps);
            float textHeight = Hud.getStringHeight(text);
            Hud.drawString(text, 2, height - y - textHeight - 2, Hud.color(), true);
            y += textHeight + 2;
        }

        if (Hud.mod().infoFps.get()) {
            String text = String.format("FPS\247f: %d", Minecraft.getDebugFPS());
            float textHeight = Hud.getStringHeight(text);
            Hud.drawString(text, 2, height - y - textHeight - 2, Hud.color(), true);
//            y += textHeight + 2;
        }

    }

    public void drawRight(ScaledResolution sr) {

        List<PotionString> strings = new ArrayList<>();

        if (Hud.mod().infoPotions.get()) {
            for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
                String name = "WTF DID I DO WRONG???";
                int color = 0xFFAAAAAA;
                try {
                    name = I18n.format(Potion.potionTypes[effect.getPotionID()].getName());
                    color = Potion.potionTypes[effect.getPotionID()].getLiquidColor() | 0xFF000000;
                } catch (ArrayIndexOutOfBoundsException ignored) {}
                String text = String.format("%s \2477%d - %s", name, effect.getAmplifier() + 1, Potion.getDurationString(effect));
                strings.add(new PotionString(text, color));
            }

            strings.sort(Comparator.<PotionString>comparingDouble(str -> Hud.getStringWidth(str.text)).reversed());
        }

        if (Hud.mod().infoVersion.get()) {
            strings.add(0, new PotionString(Nonsense.NAME + " - \247f" + Nonsense.VERSION, 0xFFAAAAAA));
        }

        int right = sr.getScaledWidth() * sr.getScaleFactor();
        int y = sr.getScaledHeight() * sr.getScaleFactor();

        for (PotionString str : strings) {
            Hud.drawString(str.text, right - Hud.getStringWidth(str.text) - 1, y -= Hud.getStringHeight(str.text) + 1, str.color, true);
        }

    }

    private static class PotionString {
        public final String text;
        public final int color;

        public PotionString(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }


}

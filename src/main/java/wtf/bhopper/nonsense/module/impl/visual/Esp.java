package wtf.bhopper.nonsense.module.impl.visual;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.event.impl.EventPreTick;
import wtf.bhopper.nonsense.event.impl.EventRender2D;
import wtf.bhopper.nonsense.event.impl.EventRenderNameTag;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.impl.combat.AntiBot;
import wtf.bhopper.nonsense.module.setting.impl.BooleanSetting;
import wtf.bhopper.nonsense.module.setting.impl.ColorSetting;
import wtf.bhopper.nonsense.module.setting.impl.GroupSetting;
import wtf.bhopper.nonsense.util.misc.MathUtil;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;

public class Esp extends Module {

    private final GroupSetting targetsGroup = new GroupSetting("Targets", "Targets", this);
    private final BooleanSetting players = new BooleanSetting("Players", "Display players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", "Display mobs", false);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Display animals", false);
    private final BooleanSetting others = new BooleanSetting("Others", "Display others", false);
    private final BooleanSetting invis = new BooleanSetting("Invisibles", "Display invisible entities", true);

    private final GroupSetting boxGroup = new GroupSetting("Box", "Boxes", this);
    private final BooleanSetting boxEnable = new BooleanSetting("Enable", "Enable boxes", true);
    private final BooleanSetting boxCorners = new BooleanSetting("Corners", "Only render corners", false);
    private final BooleanSetting boxOutline = new BooleanSetting("Outline", "Box outline", true);
    private final ColorSetting boxColor = new ColorSetting("Color", "color", -1);

    private final GroupSetting nameGroup = new GroupSetting("Names", "Names", this);
    private final BooleanSetting nameEnable = new BooleanSetting("Enable", "Enable names", true);
    private final BooleanSetting displayNames = new BooleanSetting("Display Names", "Render display names", true);
    private final BooleanSetting nameHealth = new BooleanSetting("Health", "Display health in names", true);
    private final BooleanSetting nameBackground = new BooleanSetting("Background", "Display background", true);

    private final GroupSetting barGroup = new GroupSetting("Health Bar", "Health bars", this);
    private final BooleanSetting barEnable = new BooleanSetting("Enable", "Enable health bars", true);

    private final FontRenderer font = mc.bitFontRenderer;

    private final List<RenderEntity> renderEntities = new ArrayList<>();

    public Esp() {
        super("ESP", "Extrasensory perception", Category.VISUAL);
        this.targetsGroup.add(players, mobs, animals, others, invis);
        this.boxGroup.add(boxEnable, boxCorners, boxOutline, boxColor);
        this.nameGroup.add(nameEnable, displayNames, nameHealth, nameBackground);
        this.barGroup.add(barEnable);
        this.addSettings(targetsGroup, boxGroup, nameGroup, barGroup);
    }

    @EventHandler
    public void onTick(EventPreTick event) {
        this.renderEntities.clear();
        this.renderEntities.addAll(mc.theWorld.getEntities(EntityLivingBase.class, this::isValidEntity)
                .stream()
                .collect(
                        ArrayList::new,
                        (renderEntities, entityLivingBase) -> renderEntities.add(new RenderEntity(entityLivingBase)),
                        (BiConsumer<Collection<RenderEntity>, Collection<RenderEntity>>) Collection::addAll
                )
        );
        this.renderEntities.sort(Comparator.comparingDouble((ToDoubleFunction<RenderEntity>) value -> mc.thePlayer.getDistanceToEntity(value.entity)).reversed());

    }

    @EventHandler
    public void onRender2D(EventRender2D event) {

        RenderManager renderManager = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        double scaling = 1.0 / (double)event.resolution.getScaleFactor();

        GlStateManager.pushMatrix();
        GlStateManager.scale(scaling, scaling, scaling);

        for (RenderEntity renderEntity : this.renderEntities) {
            renderEntity.draw(event, renderManager, entityRenderer);
        }

        GlStateManager.popMatrix();

    }

    @EventHandler
    public void onRenderNameTag(EventRenderNameTag event) {
        for (RenderEntity renderEntity : this.renderEntities) {
            if (renderEntity.entity == event.entity) {
                event.cancel();
                break;
            }
        }
    }

    private boolean isValidEntity(EntityLivingBase entity) {

        if (entity == null || entity == mc.thePlayer) {
            return false;
        }

        if (Nonsense.INSTANCE.moduleManager.get(AntiBot.class).isBot(entity)) {
            return false;
        }

        if (entity.isInvisible() && !invis.get()) {
            return false;
        }

        if (entity instanceof EntityPlayer) {
            return players.get();

        } else if (entity instanceof EntityMob) {
            return mobs.get();

        } else if (entity instanceof EntityAnimal) {
            return animals.get();

        }

        return others.get();
    }

    private class RenderEntity {

        private final EntityLivingBase entity;

        private final String name;
        private final float health;
        private final float healthFactor;
        private final float absorbFactor;
        private final List<ItemStack> items = new ArrayList<>();

        private double startX = 0.0;
        private double startY = 0.0;
        private double endX = 0.0;
        private double endY = 0.0;

        public RenderEntity(EntityLivingBase entity) {
            this.entity = entity;

            this.name = displayNames.get() ? entity.getDisplayName().getFormattedText() : entity.getName();
            this.health = entity.getHealth();
            this.healthFactor = this.health / entity.getMaxHealth();
            this.absorbFactor = Math.min(entity.getAbsorptionAmount() / entity.getMaxHealth(), 1.0F);

            this.items.clear();
            this.items.add(entity.getHeldItem());
            for (int i = 0; i < 4; i++) {
                this.items.add(entity.getCurrentArmor(i));
            }
        }

        public void draw(EventRender2D event, RenderManager render, EntityRenderer entityRenderer) {
            if (!RenderUtil.isInViewFrustum(entity)) {
                return;
            }

            double x = MathUtil.lerp(entity.lastTickPosX, entity.posX, event.delta);
            double y = MathUtil.lerp(entity.lastTickPosY, entity.posY, event.delta);
            double z = MathUtil.lerp(entity.lastTickPosZ, entity.posZ, event.delta);

            double width = entity.width / 1.5;
            double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
            AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);

            List<Vector3d> vectors = Arrays.asList(
                    new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                    new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                    new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                    new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                    new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                    new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                    new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                    new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
            );

            entityRenderer.setupCameraTransform(event.delta, 0);

            Vector4d position = null;

            for (Vector3d vector : vectors) {
                Vector3d projected = RenderUtil.project2D(event.resolution.getScaleFactor(),
                        vector.x - render.viewerPosX,
                        vector.y - render.viewerPosY,
                        vector.z - render.viewerPosZ);

                if (projected != null && projected.z >= 0.0 && projected.z < 1.0) {
                    if (position == null) {
                        position = new Vector4d(projected.x, projected.y, projected.z, 0.0);
                    }

                    position.x = Math.min(projected.x, position.x);
                    position.y = Math.min(projected.y, position.y);
                    position.z = Math.max(projected.x, position.z);
                    position.w = Math.max(projected.y, position.w);
                }

            }

            if (position == null) {
                return;
            }

            entityRenderer.setupOverlayRendering();
            this.startX = position.x;
            this.startY = position.y;
            this.endX = position.z;
            this.endY = position.w;

            this.drawBox();
            this.drawHealthBar();
            this.drawName();



        }

        private void drawBox() {

            if (!boxEnable.get()) {
                return;
            }

            int color = boxColor.getRgb();

            if (boxCorners.get()) {

                if (boxOutline.get()) {
                    Gui.drawRect(startX + 0.5, startY, startX - 1.0, startY + (endY - startY) / 4.0 + 0.5, ColorUtil.BLACK);
                    Gui.drawRect(startX - 1.0, endY, startX + 0.5, endY - (endY - startY) / 4.0 - 0.5, ColorUtil.BLACK);
                    Gui.drawRect(startX - 1.0, startY - 0.5, startX + (endX - startX) / 3.0 + 0.5, startY + 1.0, ColorUtil.BLACK);
                    Gui.drawRect(endX - (endX - startX) / 3.0 - 0.5, startY - 0.5, endX + 0.5, startY + 1.0, ColorUtil.BLACK);
                    Gui.drawRect(endX - 1.0, startY, endX + 0.5, startY + (endY - startY) / 4.0 + 0.5, ColorUtil.BLACK);
                    Gui.drawRect(endX - 1.0, endY, endX + 0.5, endY - (endY - startY) / 4.0 - 0.5, ColorUtil.BLACK);
                    Gui.drawRect(startX - 1.0, endY - 1.0, startX + (endX - startX) / 3.0 + 0.5, endY + 0.5, ColorUtil.BLACK);
                    Gui.drawRect(endX - (endX - startX) / 3.0 - 0.5, endY - 1.0, endX + 0.5, endY + 0.5, ColorUtil.BLACK);
                }

                Gui.drawRect(startX, startY, startX - 0.5, startY + (endY - startY) / 4.0, color);
                Gui.drawRect(startX, endY, startX - 0.5, endY - (endY - startY) / 4.0, color);
                Gui.drawRect(startX - 0.5, startY, startX + (endX - startX) / 3.0, startY + 0.5, color);
                Gui.drawRect(endX - (endX - startX) / 3.0, startY, endX, startY + 0.5, color);
                Gui.drawRect(endX - 0.5, startY, endX, startY + (endY - startY) / 4.0, color);
                Gui.drawRect(endX - 0.5, endY, endX, endY - (endY - startY) / 4.0, color);
                Gui.drawRect(startX, endY - 0.5, startX + (endX - startX) / 3.0, endY, color);
                Gui.drawRect(endX - (endX - startX) / 3.0, endY - 0.5, endX - 0.5, endY, color);


            } else {

                if (boxOutline.get()) {
                    Gui.drawRect(startX - 1, startY, startX + 0.5, endY + 0.5, ColorUtil.BLACK);
                    Gui.drawRect(startX - 1.0, startY - 0.5, endX + 0.5, startY + 0.5 + 0.5, ColorUtil.BLACK);
                    Gui.drawRect(endX - 0.5 - 0.5, startY, endX + 0.5, endY + 0.5, ColorUtil.BLACK);
                    Gui.drawRect(startX - 1.0, endY - 0.5 - 0.5, endX + 0.5, endY + 0.5, ColorUtil.BLACK);
                }

                Gui.drawRect(startX - 0.5, startY, startX + 0.5 - 0.5, endY, color);
                Gui.drawRect(startX, endY - 0.5, endX, endY, color);
                Gui.drawRect(startX - 0.5, startY, endX, startY + 0.5, color);
                Gui.drawRect(endX - 0.5, startY, endX, endY, color);

            }

        }

        private void drawHealthBar() {

            if (!barEnable.get()) {
                return;
            }

            Gui.drawRect(startX - 3.5, startY - 0.5, startX - 1.5, endY + 0.5, 0x78000000);

            if (healthFactor > 0.0F) {
                int healthColor = ColorUtil.health(healthFactor);
                Gui.drawRect(startX - 3.0, endY, startX - 2.0, endY - (endY - startY) * healthFactor, healthColor);
                if (absorbFactor > 0.0F) {
                    Gui.drawRect(startX - 3.0, endY, startX - 2.0, endY - (endY - startY) * absorbFactor, Potion.absorption.getLiquidColor() | 0xFF000000);
                }
            }
        }

        private void drawName() {

            if (!nameEnable.get()) {
                return;
            }

            int color = -1;

            String display = this.name;

            if (nameHealth.get()) {
                String health = new DecimalFormat("#0.#").format(this.health) + "\247c\u2764";
                display += " \2477[\247f" + health + "\2477]";
            }

            double width = (endX - startX) / 2.0;
            double textWidth = font.getStringWidth(display) * 0.5;
            double tagX = (startX + width - textWidth / 2.0) * 2.0;
            double tagY = startY * 2.0 - 10.0F;


            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 0.5);
            if (nameBackground.get()) {
                Gui.drawRect(tagX - 2.0, tagY - 3.0, tagX + textWidth * 2.0 + 2.0, tagY + 8.0, 0x8C000000);
                font.drawStringWithShadow(display, Math.round(tagX), Math.round(tagY), color);
            }
            GlStateManager.popMatrix();

        }




    }

}

package wtf.bhopper.nonsense.gui.screens;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.glu.Project;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.font.Fonts;
import wtf.bhopper.nonsense.gui.font.TTFFontRenderer;
import wtf.bhopper.nonsense.gui.screens.altmanager.GuiAltManager;
import wtf.bhopper.nonsense.util.render.ColorUtil;
import wtf.bhopper.nonsense.util.render.RenderUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class NonsenseMainMenu extends GuiScreen {

    public static GuiScreen get() {
        return new NonsenseMainMenu();
    }

    private static final ResourceLocation[] PANORAMA = new ResourceLocation[]{
            new ResourceLocation("textures/gui/title/background/panorama_0.png"),
            new ResourceLocation("textures/gui/title/background/panorama_1.png"),
            new ResourceLocation("textures/gui/title/background/panorama_2.png"),
            new ResourceLocation("textures/gui/title/background/panorama_3.png"),
            new ResourceLocation("textures/gui/title/background/panorama_4.png"),
            new ResourceLocation("textures/gui/title/background/panorama_5.png")
    };

    private static final String[] SPLASHES = {
            "Also try Adjust",
            ":)",
            "${jndi:ldap://127.0.0.1:69420/skid_stuff_lol}",
            "#Ratted by nonsense client 2024",
            "we do a little trolling (just a little)",
            "mc.getNetHandler().addToSendQueue(new C01PacketChatMessage(\"Hypixel is dumb\"));",
            "Are you cheating on Minecraft servers again?",
            "wtf how nuker",
            "Fuck bridging, I can fly motherfucker!",
            "yozef lost their winstreak: 1973 -> 0 (playing threes)",
            "no refunds (you can't refund something that's free)",
            "blowsy is incompetent",
            "listen to asmr when sniping you'll win everytime :3",
            "As you might expect, the ones who work hard succeed more often.",
            "In order to grow, one must not be afraid of challenges but rather view them as opportunities for self-improvement.",
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            "mommy's silly zilly boy :3",
            "This sentence has thirty-three letters.",
            "SKIDZ",
            "ok boomer",
            "Tear can't code",
            "Sigma rule no3: consent is for pussies",
            "Opal client fell off",
            "How much wood could Alan Wood suck if Alan Wood could suck wood?",
            "People die of drinking and smoking, but nobody has ever died of gambling",
            "If she leaves you on seen, put it all on green"
    };

    private final TTFFontRenderer font;
    private final TTFFontRenderer titleFont;
    private final TTFFontRenderer buttonFont;
    private final TTFFontRenderer changeLogFont;

    private ScaledResolution res;

    private ResourceLocation backgroundTexture;

    private String splashText;

    private int panoramaTimer;

    private GuiChangelog.VersionInfo latestVersionInfo;

    private final Button[] buttons = new Button[]{
            new Button("Singleplayer", () -> this.mc.displayGuiScreen(new GuiSelectWorld(this))),
            new Button("Multiplayer", () -> this.mc.displayGuiScreen(new GuiMultiplayer(this))),
            new Button("Alt Manager", () -> this.mc.displayGuiScreen(new GuiAltManager(this)))
    };

    private final IconButton[] iconButtons = new IconButton[]{
            new IconButton("nonsense/icon/close.png", () -> mc.shutdown()),
            new IconButton("nonsense/icon/options.png", () -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings))),
            new IconButton("nonsense/icon/language.png", () -> mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager())))
    };

    public NonsenseMainMenu() {
        this.selectNewSplashText();
        GuiChangelog.ChangeLog changeLog = GuiChangelog.loadChangeLog();
        if (changeLog != null) {
            this.latestVersionInfo = changeLog.changelog[0];
        }
        this.font = Nonsense.INSTANCE.fontManager.getFont(Fonts.ARIAL, 20);
        this.titleFont = Nonsense.INSTANCE.fontManager.getFont(Fonts.ARIAL, 72);
        this.buttonFont = Nonsense.INSTANCE.fontManager.getFont(Fonts.ARIAL, 22);
        this.changeLogFont = Nonsense.INSTANCE.fontManager.getFont(Fonts.ARIAL, 12);
    }

    @Override
    public void initGui() {
        this.res = new ScaledResolution(this.mc);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", new DynamicTexture(256, 256));

        for (int i = 0; i < this.buttons.length; i++) {
            this.buttons[i].x = this.getWidth() / 2 - 100;
            this.buttons[i].y = this.getHeight() / 2 + 60 + i * 50;
        }

        for (int i = 0; i < this.iconButtons.length; i++) {
            this.iconButtons[i].x = this.getWidth() - (i + 1) * 72;
            this.iconButtons[i].y = 8;
        }

    }

    @Override
    public void updateScreen() {
        ++this.panoramaTimer;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Button button : this.buttons) {
            if (button.mouseIntersecting(mouseX * res.getScaleFactor(), mouseY * res.getScaleFactor()) && mouseButton == 0) {
                this.playPressSound(mc.getSoundHandler());
                button.action.run();
            }
        }

        for (IconButton button : this.iconButtons) {
            if (button.mouseIntersecting(mouseX * res.getScaleFactor(), mouseY * res.getScaleFactor()) && mouseButton == 0) {
                this.playPressSound(mc.getSoundHandler());
                button.action.run();
            }
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // Skybox
        GlStateManager.disableAlpha();
        this.renderSkybox(partialTicks);
        GlStateManager.enableAlpha();

        float inverseScale = 1.0F / res.getScaleFactor();
        GlStateManager.pushMatrix();
        GlStateManager.scale(inverseScale, inverseScale, 0.0F);

        String title = "\247l" + Nonsense.NAME.charAt(0) + "\247r\247f" + Nonsense.NAME.substring(1);
        this.drawString(titleFont, title, (int) ((this.getWidth() - this.stringWidth(titleFont, title)) / 2.0F), (int) (this.getHeight() / 2.0F - this.stringWidth(font, title) - 10.0F), 0xFFFF5555);

        for (Button button : this.buttons) {
            button.draw(mouseX * res.getScaleFactor(), mouseY * res.getScaleFactor());
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        for (IconButton button : this.iconButtons) {
            button.draw(mouseX * this.res.getScaleFactor(), mouseY * this.res.getScaleFactor());
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        String version = Nonsense.NAME + " \2477- \247f" + Nonsense.VERSION;

        this.drawString(this.font, splashText, (int) ((this.getWidth() - this.stringWidth(this.font, splashText)) / 2.0F), (int) (this.getHeight() / 2.0F - 5.0F), ColorUtil.rainbow(System.currentTimeMillis(), 0, 1.0F, 1.0F));
        this.drawString(this.font, version, 4, (int) (this.getHeight() - this.stringHeight(this.font, version) - 4), 0xFFFF5555);

        if (this.latestVersionInfo != null) {
            this.drawString(this.font, "Changelog", 4, 4, 0xFFFF5555);
            int count = 0;
            for (String change : this.latestVersionInfo.changes) {
                this.drawString(this.changeLogFont, "\247c- \247r" + change, 4, 8 + (int)this.stringHeight(font, "Changelog") + (int)(this.stringHeight(changeLogFont, "I") + 2) * count, -1);
                ++count;
            }
        }

        GlStateManager.popMatrix();

    }

    private void drawPanorama(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        for (int i = 0; i < 64; ++i) {
            GlStateManager.pushMatrix();
            float f = ((float) (i % 8) / 8.0F - 0.5F) / 64.0F;
            float f1 = ((float) (i / 8) / 8.0F - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + partialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float) this.panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int k = 0; k < 6; ++k) {
                GlStateManager.pushMatrix();

                switch (k) {
                    case 1:
                        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                        break;
                    case 2:
                        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                        break;
                    case 3:
                        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                        break;
                    case 4:
                        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                        break;
                    case 5:
                        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                        break;
                }

                this.mc.getTextureManager().bindTexture(PANORAMA[k]);
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                int alpha = 255 / (i + 1);
                worldRenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, alpha).endVertex();
                worldRenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, alpha).endVertex();
                worldRenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, alpha).endVertex();
                worldRenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, alpha).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();

        for (int i = 0; i < 3; ++i) {
            float alpha = 1.0F / (float) (i + 1);
            int width = this.width;
            int height = this.height;
            float u = (float) (i - 3 / 2) / 256.0F;
            worldRenderer.pos(width, height, this.zLevel).tex(u, 1.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            worldRenderer.pos(width, 0.0, this.zLevel).tex(u + 1.0F, 1.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            worldRenderer.pos(0.0, 0.0, this.zLevel).tex(u + 1.0F, 0.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            worldRenderer.pos(0.0, height, this.zLevel).tex(u, 0.0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(float partialTicks) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(partialTicks);
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
        float u = (float) this.height * f / 256.0F;
        float v = (float) this.width * f / 256.0F;
        int width = this.width;
        int height = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, height, this.zLevel).tex(0.5F - u, 0.5F + v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(width, height, this.zLevel).tex(0.5F - u, 0.5F - v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(width, 0.0, this.zLevel).tex(0.5F + u, 0.5F - v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0, 0.0, this.zLevel).tex(0.5F + u, 0.5F + v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    public void selectNewSplashText() {
        String prevSplash = splashText;
        if (prevSplash == null) {
            prevSplash = "";
        }

        do {
            splashText = SPLASHES[ThreadLocalRandom.current().nextInt(0, SPLASHES.length)];
        }
        while (prevSplash.equals(splashText));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 24) {
            this.splashText = "Merry Christmas!";
        } else if (calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            this.splashText = "Happy new year!";
        } else if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }
    }

    void playPressSound(SoundHandler soundHandler) {
        try {
            soundHandler.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        } catch (Exception ignored) {
        }
    }

    private int getWidth() {
        return res.getScaledWidth() * res.getScaleFactor();
    }

    private int getHeight() {
        return res.getScaledHeight() * res.getScaleFactor();
    }

    private void drawString(TTFFontRenderer font, String text, int x, int y, int color) {
        GlStateManager.scale(2.0F, 2.0F, 0.0F);
        font.drawStringWithShadow(text, (int) Math.floor(x / 2.0F), (int) Math.floor(y / 2.0F), color);
        GlStateManager.scale(0.5F, 0.5F, 0.0F);
    }

    private float stringWidth(TTFFontRenderer font, String text) {
        return font.getStringWidth(text) * 2.0F;
    }

    private float stringHeight(TTFFontRenderer font, String text) {
        return font.getHeight(text) * 2.0F;
    }

    public class Button {

        public static final int WIDTH = 200;
        public static final int HEIGHT = 40;

        public int x, y;
        public final String text;
        public final Runnable action;

        public Button(String text, Runnable action) {
            this.text = text;
            this.x = 0;
            this.y = 0;
            this.action = action;
        }

        public void draw(int mouseX, int mouseY) {
            RenderUtil.drawCircleRect(x, y, x + WIDTH, y + HEIGHT, 8, this.mouseIntersecting(mouseX, mouseY) ? 0xAA000000 : 0x55000000);
            NonsenseMainMenu.this.drawString(NonsenseMainMenu.this.buttonFont, text, (int) (this.x + (WIDTH - NonsenseMainMenu.this.stringWidth(NonsenseMainMenu.this.buttonFont, text)) / 2.0F), (int) (this.y + HEIGHT / 2 - NonsenseMainMenu.this.stringHeight(NonsenseMainMenu.this.buttonFont, text) / 2 + 1), this.mouseIntersecting(mouseX, mouseY) ? 0xFFFF5555 : 0xFFFFFFFF);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.0F);
        }

        public boolean mouseIntersecting(int mouseX, int mouseY) {
            return mouseX > x && mouseX < this.x + WIDTH && mouseY > this.y && mouseY < this.y + HEIGHT;
        }
    }

    public class IconButton {
        public static final int SIZE = 64;

        public int x, y;
        public final ResourceLocation resource;
        public final Runnable action;

        public IconButton(String resource, Runnable action) {
            this.resource = new ResourceLocation(resource);
            this.action = action;
            this.x = 0;
            this.y = 0;
        }

        public void draw(int mouseX, int mouseY) {
            NonsenseMainMenu.this.mc.getTextureManager().bindTexture(this.resource);
            if (this.mouseIntersecting(mouseX, mouseY)) {
                GlStateManager.color(1.0F, 1.0F / 3.0F, 1.0F / 3.0F);
            } else {
                GlStateManager.color(1.0F, 1.0F, 1.0F);
            }
            Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, 0.0F, SIZE, SIZE, SIZE, SIZE);
        }

        public boolean mouseIntersecting(int mouseX, int mouseY) {
            return mouseX > this.x && mouseX < this.x + SIZE && mouseY > this.y && mouseY < this.y + SIZE;
        }

    }

}

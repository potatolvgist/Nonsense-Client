package wtf.bhopper.nonsense;

import meteordevelopment.orbit.IEventBus;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import wtf.bhopper.nonsense.command.CommandManager;
import wtf.bhopper.nonsense.config.ConfigManager;
import wtf.bhopper.nonsense.event.NonsenseEventBus;
import wtf.bhopper.nonsense.gui.clickgui.ClickGui;
import wtf.bhopper.nonsense.gui.font.FontManager;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.ModuleManager;
import wtf.bhopper.nonsense.util.minecraft.world.TickRate;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class Nonsense {

    public static final String NAME = "Nonsense";
    public static final String VERSION = "Alpha-241002";

    public static final Logger LOGGER = LogManager.getLogger("Nonsense");

    public static Nonsense INSTANCE = null;

    // Event bus
    public IEventBus eventBus;

    // Managers
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public FontManager fontManager;

    // Utility
    public TickRate tickRate;
    public File dataDir;

    public Nonsense() {
        INSTANCE = this;
    }

    public void init() {
        LOGGER.info("Loading {}:{}", NAME, VERSION);

        this.dataDir = Minecraft.getMinecraft().mcDataDir.toPath().resolve("nonsense").toFile();
        this.dataDir.mkdirs();

        this.eventBus = new NonsenseEventBus();
        this.eventBus.registerLambdaFactory("net.minecraft", (method, clazz) -> (MethodHandles.Lookup)method.invoke(null, clazz, MethodHandles.lookup()));
        this.eventBus.registerLambdaFactory("wtf.bhopper.nonsense", (method, clazz) -> (MethodHandles.Lookup)method.invoke(null, clazz, MethodHandles.lookup()));

        this.fontManager = new FontManager();

        this.moduleManager = new ModuleManager();
        this.moduleManager.addModules();

        this.commandManager = new CommandManager();
        this.commandManager.addCommands();

        this.configManager = new ConfigManager();
        this.configManager.init();

        this.tickRate = new TickRate();

        Hud.init();
        ClickGui.init();

    }

    public void onKeyPress(int keyCode) {
        this.moduleManager.values().forEach(module -> {
            if (module.getBind() == keyCode) {
                module.toggle();
            }
        });
    }


}

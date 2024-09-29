package wtf.bhopper.nonsense;

import meteordevelopment.orbit.IEventBus;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;
import wtf.bhopper.nonsense.command.CommandManager;
import wtf.bhopper.nonsense.event.NonsenseEventBus;
import wtf.bhopper.nonsense.gui.clickgui.ClickGui;
import wtf.bhopper.nonsense.gui.font.FontManager;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.ModuleManager;
import wtf.bhopper.nonsense.util.minecraft.world.TickRate;

import java.lang.invoke.MethodHandles;

public class Nonsense {

    public static final String NAME = "Nonsense";
    public static final String VERSION = "Alpha-240922";

    public static final Logger LOGGER = LogManager.getLogger("Nonsense");

    public static Nonsense INSTANCE = null;

    // Event bus
    public IEventBus eventBus;

    // Managers
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public FontManager fontManager;

    // Utility
    public TickRate tickRate;

    public Nonsense() {
        INSTANCE = this;
    }

    public void init() {
        LOGGER.info("Loading {}:{}", NAME, VERSION);
        Display.setTitle("Minecraft 1.8.9 - " + NAME + " (" + VERSION + ")");
        this.eventBus = new NonsenseEventBus();

        this.eventBus.registerLambdaFactory("net.minecraft", (method, clazz) -> (MethodHandles.Lookup)method.invoke(null, clazz, MethodHandles.lookup()));
        this.eventBus.registerLambdaFactory("wtf.bhopper.nonsense", (method, clazz) -> (MethodHandles.Lookup)method.invoke(null, clazz, MethodHandles.lookup()));

        this.fontManager = new FontManager();

        this.moduleManager = new ModuleManager();
        this.moduleManager.addModules();

        this.commandManager = new CommandManager();
        this.commandManager.addCommands();

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
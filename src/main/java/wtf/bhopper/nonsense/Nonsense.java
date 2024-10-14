package wtf.bhopper.nonsense;

import com.google.gson.Gson;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.lwjglx.opengl.Display;
import wtf.bhopper.nonsense.alt.AltManager;
import wtf.bhopper.nonsense.command.CommandManager;
import wtf.bhopper.nonsense.config.ConfigManager;
import wtf.bhopper.nonsense.event.NonsenseEventBus;
import wtf.bhopper.nonsense.gui.clickgui.dropdown.DropdownClickGui;
import wtf.bhopper.nonsense.gui.font.FontManager;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleManager;
import wtf.bhopper.nonsense.util.minecraft.client.BlinkUtil;
import wtf.bhopper.nonsense.util.minecraft.world.TickRate;
import wtf.bhopper.nonsense.gui.ImGuiHelper;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class Nonsense {

    public static final String NAME = "Nonsense";
    public static final String VERSION = "Alpha-241010";

    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Gson GSON = new Gson();

    public static Nonsense INSTANCE = null;

    // Time at which the game was started (in millis)
    public final long startTime;

    // Event bus
    public IEventBus eventBus;

    // Managers
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public FontManager fontManager;
    public AltManager altManager;

    // Utility
    public TickRate tickRate;
    public File dataDir;

    public Nonsense() {
        INSTANCE = this;
        this.startTime = System.currentTimeMillis();
    }

    public void init() {
        LOGGER.info("Loading {}:{}", NAME, VERSION);

        this.dataDir = new File(Minecraft.getMinecraft().mcDataDir, "nonsense");
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

        this.altManager = new AltManager();

        this.tickRate = new TickRate();

        ImGuiHelper.init(Display.getWindow());

        Hud.init();
        DropdownClickGui.init();
        BlinkUtil.init();

    }

    public void onKeyPress(int keyCode) {
        this.moduleManager.values().forEach(module -> {
            if (module.getBind() == keyCode) {
                module.toggle();
            }
        });
    }

    public static <T extends Module> T module(Class<T> clazz) {
        return INSTANCE.moduleManager.get(clazz);
    }


}

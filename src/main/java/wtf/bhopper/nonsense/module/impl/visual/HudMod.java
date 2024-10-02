package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.setting.impl.*;
import wtf.bhopper.nonsense.module.setting.util.Description;
import wtf.bhopper.nonsense.module.setting.util.DisplayName;

public class HudMod extends Module {

    public final GroupSetting moduleListGroup = new GroupSetting("Module List", "Module list", this);
    public final BooleanSetting moduleListEnabled = new BooleanSetting("Enabled", "Enables the module list", true);
    public final EnumSetting<ModuleListColorMode> moduleListColorMode = new EnumSetting<>("Color Mode", "Color mode", ModuleListColorMode.STATIC);
    public final ColorSetting moduleListColor = new ColorSetting("Color", "Module list color", 0xFFFF5555);
    public final BooleanSetting moduleListDisplayNames = new BooleanSetting("Display Names", "Use display names", true);
    public final EnumSetting<ModuleListSuffix> moduleListSuffixes = new EnumSetting<>("Suffixes", "Suffix mode", ModuleListSuffix.NORMAL);
    public final BooleanSetting moduleListLowerCase = new BooleanSetting("Lower Case", "Converts the module list to lower case", false);
    public final BooleanSetting moduleListAnimated = new BooleanSetting("Animated", "Animates the module list", true);
    public final IntSetting moduleListSpacing = new IntSetting("Spacing", "Module list spacing", 0, 4, 2);
    public final IntSetting moduleListBackground = new IntSetting("Background", "Background transparency", 0, 255, 120);
    public final EnumSetting<ModuleListSorting> moduleListSorting = new EnumSetting<>("Sorting", "Sorting", ModuleListSorting.LENGTH);

    public final GroupSetting watermarkGroup = new GroupSetting("Watermark", "Client watermark", this);
    public final BooleanSetting watermarkEnable = new BooleanSetting("Enable", "Enables watermark", true);
    public final EnumSetting<WatermarkColorMode> watermarkColorMode = new EnumSetting<>("Color Mode", "Color mode", WatermarkColorMode.COLOR);
    public final ColorSetting watermarkColor = new ColorSetting("Color", "Watermark color", 0xFF5555);
    public final StringSetting watermarkText = new StringSetting("Text", "Watermark text", Nonsense.NAME);

    public final GroupSetting infoGroup = new GroupSetting("Information", "Display information", this);
    public final EnumSetting<Coordinates> infoCoordinates = new EnumSetting<>("Coordinates", "Displays your coordinates", Coordinates.NORMAL);
    public final BooleanSetting infoAngles = new BooleanSetting("Angles", "Displays your pitch and yaw", false);
    public final EnumSetting<Speed> infoSpeed = new EnumSetting<>("Speed", "Displays your speed", Speed.MPS);
    public final BooleanSetting infoTps = new BooleanSetting("TPS", "Displays the servers ticks per second", false);
    public final BooleanSetting infoPotions = new BooleanSetting("Potions", "Displays your active potion effects", true);
    public final BooleanSetting infoFps = new BooleanSetting("FPS", "Displays your frames per second", false);
    public final BooleanSetting infoVersion = new BooleanSetting("Version", "Displays the client version", true);

    public final GroupSetting notificationGroup = new GroupSetting("Notifications", "Notifications", this);
    public final BooleanSetting notificationEnabled = new BooleanSetting("Enabled", "Enables notifications", true);
    public final EnumSetting<NotificationSound> notificationSound = new EnumSetting<>("Sound", "Notification sound", NotificationSound.POP);

    public final ColorSetting color = new ColorSetting("Color", "HUD color", 0xFFFF5555);
    public final BooleanSetting customFont = new BooleanSetting("Custom Font", "Uses a custom font", true);
    public final BooleanSetting hidef3 = new BooleanSetting("Hide In F3", "Hide the HUD when the debug/F3 menu is open", true);

    public HudMod() {
        super("HUD", "Heads Up Display", Category.VISUAL);
        this.moduleListGroup.add(
                this.moduleListEnabled,
                this.moduleListColorMode,
                this.moduleListColor,
                this.moduleListDisplayNames,
                this.moduleListSuffixes,
                this.moduleListLowerCase,
                this.moduleListAnimated,
                this.moduleListSpacing,
                this.moduleListBackground,
                this.moduleListSorting
        );
        this.watermarkGroup.add(
                this.watermarkEnable,
                this.watermarkColorMode,
                this.watermarkColor,
                this.watermarkText
        );
        this.infoGroup.add(
                this.infoCoordinates,
                this.infoAngles,
                this.infoSpeed,
                this.infoTps,
                this.infoPotions,
                this.infoFps,
                this.infoVersion
        );
        this.notificationGroup.add(
                this.notificationEnabled,
                this.notificationSound
        );
        this.addSettings(this.moduleListGroup, this.watermarkGroup, this.infoGroup, this.notificationGroup, this.customFont, this.hidef3);

        this.toggle(true);
    }

    public enum ModuleListColorMode {
        STATIC,
        WAVY,
        RAINBOW,
        RAINBOW_2,
        CATEGORY,
        ASTOLFO,
        RISE,
        RANDOM,
        @Description("Commissioned by BrettHax") TRANS
    }

    public enum ModuleListSuffix {
        NONE,
        NORMAL,
        HYPHEN,
        BRACKET,
        SQUARE_BRACKET
    }

    public enum ModuleListSorting {
        LENGTH,
        @DisplayName("ABC") ABC
    }

    public enum WatermarkColorMode {
        WHITE,
        COLOR,
        SOLID,
        RAINBOW,
        RAINBOW_2
    }

    public enum Coordinates {
        NORMAL,
        @Description("Shows Nether coordinates while in the Overworld and vice versa") DIMENSIONS,
        NONE
    }

    public enum Speed {
        @DisplayName("m/s") MPS,
        @DisplayName("km/h") KMPH,
        @DisplayName("mph") MPH,
        RAW,
        NONE

    }

    public enum NotificationSound {
        POP("random.pop", 1),
        DING("random.orb", 1),
        NONE("", 0);

        private final ResourceLocation sound;
        private final int pitch;

        NotificationSound(String sound, int pitch) {
            this.sound = new ResourceLocation(sound);
            this.pitch = pitch;
        }

        public ISound createSoundRecord()  {
            return PositionedSoundRecord.create(this.sound, this.pitch);
        }
    }

}

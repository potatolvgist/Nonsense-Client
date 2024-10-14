package wtf.bhopper.nonsense.gui.clickgui.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.input.Keyboard;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.impl.visual.ClickGuiMod;
import wtf.bhopper.nonsense.module.setting.Setting;
import wtf.bhopper.nonsense.module.setting.impl.*;
import wtf.bhopper.nonsense.gui.ImGuiHelper;

import java.awt.*;
import java.util.List;

public class ImGuiClickGui extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ImGuiHelper.newFrame();

        ImGui.setNextWindowSize(768, 600);
        if (ImGui.begin(Nonsense.NAME, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse)) {

            if (ImGui.beginTabBar("clickgui#categories")) {
                for (Module.Category category : Module.Category.values()) {
                    if (ImGui.beginTabItem(category.name)) {
                        this.drawModules(Nonsense.INSTANCE.moduleManager.getInCategory(category));
                        ImGui.endTabItem();
                    }
                }

                ImGui.endTabBar();
            }

            ImGui.end();
        }

        ImGuiHelper.render();

    }

    private void drawModules(List<Module> modules) {

        for (Module module : modules) {
            ImGui.pushID("clickgui#module#" + module.name);

            if (ImGui.collapsingHeader(module.displayName)) {
                tooltip(module.description);

                if (ImGui.checkbox("Enabled", module.isEnabled())) {
                    module.toggle();
                }
                ImGui.sameLine();

                String keyStr = "Keybind: " + Keyboard.getKeyName(module.getBind());
                if (ImGui.button(keyStr)) {
                    // TODO: listen
                }

                if (!module.getSettings().isEmpty()) {
                    ImGui.separator();
                    ImGui.pushID("clickgui#modulesettings#" + module.name);

                    for (Setting<?> setting : module.getSettings()) {
                        if (setting.isDisplayed() || Nonsense.module(ClickGuiMod.class).showHidden.get()) {
                            this.drawSetting(setting);
                        }
                    }

                    ImGui.popID();
                }

            }

            ImGui.popID();
        }

    }

    private void drawSetting(Setting<?> s) {

        if (s instanceof BooleanSetting) {
            BooleanSetting setting = (BooleanSetting)s;
            if (ImGui.checkbox(setting.displayName, setting.get())) {
                setting.set(!setting.get());
            }
            tooltip(setting.description);

        } else if (s instanceof IntSetting) {
            IntSetting setting = (IntSetting)s;
            ImInt v = new ImInt(setting.get());
            if (ImGui.sliderInt(setting.displayName, v.getData(), setting.min, setting.max, setting.format)) {
                setting.set(v.get());
            }
            tooltip(setting.description);

        } else if (s instanceof FloatSetting) {
            FloatSetting setting = (FloatSetting)s;
            ImFloat v = new ImFloat(setting.get());
            if (ImGui.sliderFloat(setting.displayName, v.getData(), setting.min, setting.max, "%.2f")) {
                setting.set(v.get());
            }
            tooltip(setting.description);

        } else if (s instanceof EnumSetting) {
            EnumSetting.imGuiDraw((EnumSetting<?>)s);

        } else if (s instanceof ColorSetting) {
            ColorSetting setting = (ColorSetting)s;
            float[] color = setting.floatValues();
            if (ImGui.colorEdit4(setting.displayName, color)) {
                setting.set(new Color(color[0], color[1], color[2], color[3]));
            }

            tooltip(setting.description);

        } else if (s instanceof StringSetting) {
            StringSetting setting = (StringSetting)s;
            ImString v = new ImString(setting.get());
            if (ImGui.inputText(setting.displayName, v)) {
                setting.set(v.get());
            }
            tooltip(setting.description);

        } else if (s instanceof GroupSetting) {
            GroupSetting setting = (GroupSetting)s;

            if (ImGui.collapsingHeader(setting.displayName)) {
                ImGui.pushID("clickgui#modulesettings#" + setting.getOwner().name + "#" + setting.name);

                tooltip(setting.description);

                ImGui.indent();
                for (Setting<?> setting1 : setting.get()) {
                    if (setting.isDisplayed() || Nonsense.module(ClickGuiMod.class).showHidden.get()) {
                        this.drawSetting(setting1);
                    }
                }
                ImGui.unindent();

                ImGui.popID();

            } else {
                tooltip(setting.description);
            }

        }

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static void tooltip(String text) {
        if (ImGui.isItemHovered() && Nonsense.module(ClickGuiMod.class).toolTips.get()) {
            ImGui.setTooltip(text);
        }
    }
}

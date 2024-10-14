package wtf.bhopper.nonsense.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiHelper {

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private static ImGuiIO io = null;

    public static void init(long handle) {
        ImGui.createContext();
        loadConfig();
        imGuiGlfw.init(handle, true);
        imGuiGl3.init("#version 330");

    }

    private static void loadConfig() {
        io = ImGui.getIO();

        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
    }

    public static void newFrame() {
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public static void render() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }



}

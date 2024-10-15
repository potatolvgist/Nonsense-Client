package wtf.bhopper.nonsense.util.misc;

import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;
import org.lwjgl.util.nfd.NativeFileDialog;
import org.lwjglx.opengl.Display;

public class NativeUtil {

    public static long getWindowHandle() {
        switch (Util.getOSType()) {
            case WINDOWS:
                return GLFWNativeWin32.glfwGetWin32Window(Display.getWindow());

            case OSX:
                return GLFWNativeCocoa.glfwGetCocoaWindow(Display.getWindow());

            case LINUX:
                return GLFWNativeX11.glfwGetX11Window(Display.getWindow());
        }

        return 0L;
    }

    public static int getNfdHandleType() {
        switch (Util.getOSType()) {
            case WINDOWS:
                return NativeFileDialog.NFD_WINDOW_HANDLE_TYPE_WINDOWS;

            case OSX:
                return NativeFileDialog.NFD_WINDOW_HANDLE_TYPE_COCOA;

            case LINUX:
                return NativeFileDialog.NFD_WINDOW_HANDLE_TYPE_X11;
        }

        return NativeFileDialog.NFD_WINDOW_HANDLE_TYPE_UNSET;
    }

}

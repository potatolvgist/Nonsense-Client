package wtf.bhopper.nonsense.gui.screens;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class GuiChangelog extends GuiScreen {

    public static ChangeLog loadChangeLog() {
        try {
            Gson gson = new Gson();
            InputStream stream = Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(new ResourceLocation("nonsense/changelog.json"))
                    .getInputStream();

            Reader reader = new InputStreamReader(stream);
            ChangeLog changeLog = gson.fromJson(reader, ChangeLog.class);
            for (int i = 0; i < changeLog.changelog.length; i++) {
                for (int j = 0; j < changeLog.changelog[i].changes.length; j++) {
                    changeLog.changelog[i].changes[j] = changeLog.changelog[i].changes[j].replace('$', '\247');
                }
            }

            return changeLog;

        } catch (Exception ignored) {}

        return null;
    }

    public static class ChangeLog {
        public VersionInfo[] changelog;
    }

    public static class VersionInfo {
        public String version;
        public String[] changes;
    }

}

package wtf.bhopper.nonsense.config;

import com.google.gson.JsonObject;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.util.misc.JsonUtil;

import java.io.File;
import java.io.IOException;

public class Config {

    public static final String EXTENSION = ".json";

    private final File file;
    private final String name;

    public Config(File file) {
        this.file = file;
        this.name = file.getName().substring(0, file.getName().length() - EXTENSION.length());
    }

    public Config(String name) {
        this.name = name;
        this.file = Nonsense.INSTANCE.configManager.configDir.toPath().resolve(name + EXTENSION).toFile();
    }

    public void save() throws IOException {
        JsonObject object = new JsonObject();
        for (Module module : Nonsense.INSTANCE.moduleManager.values()) {
            module.serialize(object);
        }

        JsonUtil.writeToFile(object, file);
    }

    public void load() throws IOException {
        JsonObject object = JsonUtil.readFromFile(file);

        for (Module module : Nonsense.INSTANCE.moduleManager.values()) {
            module.deserialize(object);
        }
    }

    public boolean delete() throws IOException {
        return this.file.delete();
    }

    public String getName() {
        return this.name;
    }

}

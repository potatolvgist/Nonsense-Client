package wtf.bhopper.nonsense.config;

import wtf.bhopper.nonsense.Nonsense;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfigManager {

    public final File configDir;
    private final Map<String, Config> configs = new HashMap<>();

    public ConfigManager() {
        this.configDir = Nonsense.INSTANCE.dataDir.toPath().resolve("config").toFile();
    }

    public Collection<Config> getConfigs() {
        return configs.values();
    }

    public void init() {
        if (!configDir.exists() && !configDir.isDirectory()) {
            configDir.delete();
            configDir.mkdirs();
        }

        File[] configFiles = configDir.listFiles();

        if (configFiles != null) {
            for (File configFile : configFiles) {
                Config config = new Config(configFile);
                configs.put(config.getName(), config);
            }
        }

        Config defaultConfig = configs.get("default");
        if (defaultConfig != null) {
            try {
                defaultConfig.load();
            } catch (IOException ignored) { }
        } else {
            Config newConfig = new Config("default");
            configs.put("default", newConfig);
            try {
                newConfig.save();
            } catch (IOException ignored) { }
        }
    }


    public boolean createConfig(String name) {
        if (this.configs.containsKey(name.toLowerCase())) {
            return false;
        }

        Config config = new Config(name.toLowerCase());
        try {
            config.save();
        } catch (IOException ignored) {}
        this.configs.put(name, config);

        return true;
    }

    public boolean deleteConfig(String name) {
        if (!this.configs.containsKey(name.toLowerCase())) {
            return false;
        }

        Config config = this.configs.get(name.toLowerCase());
        try {
            if (config.delete()) {
                this.configs.remove(name.toLowerCase());
                return true;
            }
        } catch (IOException ignored) {
            return false;
        }

        return false;

    }

    public boolean saveConfig(String name) {
        if (this.configs.containsKey(name.toLowerCase())) {
            return false;
        }

        Config config = configs.get(name.toLowerCase());
        try {
            config.save();
        } catch (IOException exception) {
            return false;
        }

        return true;
    }

    public boolean loadConfig(String name) {
        if (!this.configs.containsKey(name.toLowerCase())) {
            return false;
        }

        Config config = configs.get(name.toLowerCase());
        try {
            config.load();
        } catch (IOException exception) {
            return false;
        }

        return true;
    }

}

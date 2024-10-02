package wtf.bhopper.nonsense.config;

import org.apache.logging.log4j.LogManager;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.util.NonsenseException;

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
            } catch (IOException exception) {
                Nonsense.LOGGER.error("Failed to load config", exception);
            }
        } else {
            Config newConfig = new Config("default");
            configs.put("default", newConfig);
            try {
                newConfig.save();
            } catch (IOException exception) {
                Nonsense.LOGGER.error("Failed to save config", exception);
            }
        }
    }


    public void createConfig(String name) {
        if (this.configs.containsKey(name.toLowerCase())) {
            throw new NonsenseException("Config '" + name + "' already exists.");
        }

        Config config = new Config(name.toLowerCase());
        try {
            config.save();
        } catch (IOException exception) {
            throw new NonsenseException("Failed to create config", exception);
        }
        this.configs.put(name, config);
    }

    public void deleteConfig(String name) {
        if (!this.configs.containsKey(name.toLowerCase())) {
            throw new NonsenseException("Config '" + name + "' does not exist.");
        }

        Config config = this.configs.get(name.toLowerCase());
        try {
            if (config.delete()) {
                this.configs.remove(name.toLowerCase());
            }
        } catch (IOException exception) {
            throw new NonsenseException("Failed to delete config", exception);
        }

    }

    public void saveConfig(String name) {
        if (!this.configs.containsKey(name.toLowerCase())) {
            throw new NonsenseException("Config '" + name + "' does not exist.");
        }

        Config config = configs.get(name.toLowerCase());
        try {
            config.save();
        } catch (IOException exception) {
            throw new NonsenseException("Failed to save config", exception);
        }
    }

    public void loadConfig(String name) {
        if (!this.configs.containsKey(name.toLowerCase())) {
            throw new NonsenseException("Config '" + name + "' does not exist.");
        }

        Config config = configs.get(name.toLowerCase());
        try {
            config.load();
        } catch (IOException exception) {
            throw new NonsenseException("Failed to load config", exception);
        }
    }

}

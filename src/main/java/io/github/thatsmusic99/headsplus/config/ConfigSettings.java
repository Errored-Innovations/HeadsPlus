package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class ConfigSettings {

    protected FileConfiguration config;
    protected File configF;
    public String conName = "";

    protected void enable() {
        reloadC();
        load();
    }

    protected void load() {
        getConfig().options().copyDefaults(true);
        save();
    }

    public void reloadC() {
        performFileChecks();
        load();
    }

    public void performFileChecks() {
        if (configF == null || !configF.exists()) {
            configF = new File(HeadsPlus.get().getDataFolder(), conName + ".yml");
            try {
                configF.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = new YamlConfiguration();
            config.load(configF);
        } catch (InvalidConfigurationException ex) {
            Logger logger = HeadsPlus.get().getLogger();
            logger.severe("There is a configuration error in the plugin configuration files! Details below:");
            logger.severe(ex.getMessage());
            logger.severe("We have renamed the faulty configuration to " + conName + "-errored.yml for you to inspect.");
            configF.renameTo(new File(HeadsPlus.get().getDataFolder(), conName + "-errored.yml"));
            logger.severe("When you believe you have fixed the problems, change the file name back to " + conName + ".yml and reload the configuration.");
            logger.severe("If you are unsure, please contact the developer (Thatsmusic99).");
            logger.severe("The default configuration will be loaded in response to this.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (configF == null || config == null) {
            return;
        }
        try {
            config.save(configF);
        } catch (IOException e) {
            HeadsPlus.get().getLogger().severe("Error thrown when saving the config. If there's a second error below, ignore me and look at that instead.");
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getDefaultPath() {
        return "";
    }

    public Double getDouble(String path) {
        Double price = CachedValues.getPrice(path, getConfig());
        if (price != null) {
            return price;
        } else {
            if (!path.equals(getDefaultPath())) {
                return getDouble(getDefaultPath());
            } else {
                return 0.0;
            }
        }
    }

}

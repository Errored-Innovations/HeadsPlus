package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
        if (configF == null) {
            configF = new File(HeadsPlus.getInstance().getDataFolder(), conName + ".yml");
        }
        config = YamlConfiguration.loadConfiguration(configF);
        load();
    }

    public void save() {
        if (configF == null || config == null) {
            return;
        }
        try {
            config.save(configF);
        } catch (IOException e) {
            HeadsPlus.getInstance().getLogger().severe("Error thrown when saving the config. If there's a second error below, ignore me and look at that instead.");
            if (HeadsPlus.getInstance().getConfiguration().getMechanics().getBoolean("debug.print-stacktraces-in-console")) {
                e.printStackTrace();
            }
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

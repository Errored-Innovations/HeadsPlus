package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigSettings {

    protected FileConfiguration config;
    protected File configF;
    public String conName = "";

    protected void enable(boolean nullp) {
        reloadC(nullp);
        load(nullp);
    }

    protected void load(boolean nullp) {
        getConfig().options().copyDefaults(true);
        save();
    }

    public void reloadC(boolean nullp) {
        if (configF == null) {
            configF = new File(HeadsPlus.getInstance().getDataFolder(), conName + ".yml");
        }
        config = YamlConfiguration.loadConfiguration(configF);
        load(nullp);
        save();
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

}

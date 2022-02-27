package io.github.thatsmusic99.headsplus.config.customheads;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigHeads;
import io.github.thatsmusic99.headsplus.config.ConfigHeadsSelector;
import io.github.thatsmusic99.headsplus.config.HPConfig;
import io.github.thatsmusic99.headsplus.config.MainConfig;

import java.io.File;
import java.io.IOException;

@Deprecated
public class ConfigCustomHeads extends HPConfig {

    public static ConfigCustomHeads instance;

    public ConfigCustomHeads() throws IOException {
        super("customheads.yml");
        instance = this;
    }

    @Override
    public void loadDefaults() {
        makeSectionLenient("heads");
        makeSectionLenient("sections");
        makeSectionLenient("options");
    }

    @Override
    public void moveToNew() {
        moveTo("options.update-heads", "update-heads", ConfigHeads.get());
        moveTo("options.version", "version", ConfigHeads.get());
        moveTo("options.default-price", "default-selector-head-price", MainConfig.get());
        moveTo("options.price-per-world", "per-world-prices", MainConfig.get());
        // For each head
        if (contains("heads")) {
            for (String key : getConfigSection("heads").getKeys(false)) {
                moveTo("heads." + key + ".displayname", "heads." + key + ".display-name", ConfigHeads.get());
                moveTo("heads." + key + ".texture", "heads." + key + ".texture", ConfigHeads.get());
                if (get("heads." + key + ".price") instanceof Double) {
                    moveTo("heads." + key + ".price", "heads.HP#" + key + ".price", ConfigHeadsSelector.get());
                }
                moveTo("heads." + key + ".section", "heads.HP#" + key + ".section", ConfigHeadsSelector.get());
            }
        }

        // For each section
        if (contains("sections")) {
            for (String key : getConfigSection("sections").getKeys(false)) {
                moveTo("sections." + key + ".texture", "sections." + key + ".texture", ConfigHeadsSelector.get());
                moveTo("sections." + key + ".display-name", "sections." + key + ".display-name",
                        ConfigHeadsSelector.get());
                ConfigHeadsSelector.get().addDefault("sections." + key + ".enabled", true);
                ConfigHeadsSelector.get().addDefault("sections." + key + ".permission", "headsplus.section." + key);
            }
        }
    }

    @Override
    public void save() {
        File customHeads = new File(HeadsPlus.get().getDataFolder(), "customheads.yml");
        if (!customHeads.exists()) return;
        if (!customHeads.renameTo(new File(HeadsPlus.get().getDataFolder(), "customheads-backup.yml"))) {
            HeadsPlus.get().getLogger().warning("Failed to rename customheads.yml to customheads-backup.yml name! You" +
                    " will need to do this yourself.");
        }
    }

    public static ConfigCustomHeads get() {
        return instance;
    }

}

package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.config.defaults.HeadsXEnums;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXSections;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ConfigHeadsSelector extends FeatureConfig {

    private static ConfigHeadsSelector instance;
    private HashMap<String, SectionInfo> sections = new LinkedHashMap<>();

    public ConfigHeadsSelector() {
        super("heads-selector.yml");
        instance = this;
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().HEADS_SELECTOR;
    }

    public static ConfigHeadsSelector get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        double version = getDouble("version", 0.0);
        if (isNew()) version = 0.0;
        addComment("This is where you can configure where the heads selector (/heads)");

        addDefault("version", 3.5);
        makeSectionLenient("heads");
        if (version < 3.5) {
            for (HeadsXSections section : HeadsXSections.values()) {
                if (section.version > version) {
                    addDefault("sections." + section.id + ".texture", section.texture);
                    addDefault("sections." + section.id + ".display-name", section.displayName);
                    addDefault("sections." + section.id + ".permission", "headsplus.section." + section.id);
                    addDefault("sections." + section.id + ".enabled", true);
                }
            }
            for (HeadsXEnums head : HeadsXEnums.values()) {
                if (head.version > version) {
                    addDefault("heads.HP#" + head.name().toLowerCase() + ".section", head.section);
                }
            }
        }
    }

    @Override
    public void postSave() {
    }

    public static class SectionInfo {

    }
}

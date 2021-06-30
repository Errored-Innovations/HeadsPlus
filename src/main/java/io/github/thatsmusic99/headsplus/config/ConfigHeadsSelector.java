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
        return false;
    }

    public static ConfigHeadsSelector get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        addComment("This is where you can configure where the heads selector (/heads)");

        addDefault("autograb", false);
        addDefault("automatically-enable-grabbed-heads", true);
        addDefault("autograb-section", "players");
        addDefault("allow-favourite-heads", true, "Allow players to right click heads to add them as a favourite.");
        addDefault("version", 3.5);
        makeSectionLenient("heads");
        if (isNew() || getDouble("version") < 3.5) {
            for (HeadsXSections section : HeadsXSections.values()) {
                if (isNew() || section.version > getDouble("version")) {
                    addDefault("sections." + section.id + ".texture", section.texture);
                    addDefault("sections." + section.id + ".display-name", section.displayName);
                    addDefault("sections." + section.id + ".permission", "headsplus.section." + section.id);
                    addDefault("sections." + section.id + ".enabled", true);
                }
            }
            for (HeadsXEnums head : HeadsXEnums.values()) {
                if (isNew() || head.version > getDouble("version")) {
                    addDefault("heads.HP#" + head.name().toLowerCase() + ".section", head.section);
                }
            }
        }
    }

    @Override
    public void postSave() {
        super.postSave();
    }

    public static class SectionInfo {

    }
}

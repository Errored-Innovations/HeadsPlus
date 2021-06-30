package io.github.thatsmusic99.headsplus.config;

public class ConfigSounds extends HPConfig {

    private static ConfigSounds instance;

    public ConfigSounds() {
        super("sounds.yml");
        instance = this;
    }

    @Override
    public void loadDefaults() {
        for (Defaults d : Defaults.values()) {
            addDefault("sounds." + d.name + ".sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
            addDefault("sounds." + d.name + ".volume", 1.0f);
            addDefault("sounds." + d.name + ".pitch", 1.0f);
            addDefault("sounds." + d.name + ".enabled", d.enabled);
        }
    }

    public static ConfigSounds get() {
        return instance;
    }

    private enum Defaults {
        SELLHEAD("on-sell-head", true),
        BUYHEAD("on-buy-head", true),
        CHANGESECTION("on-change-section", true),
        ENTITYHEADDROP("on-entity-head-drop", false),
        PLAYERHEADDROP("on-player-head-drop", false),
        LEVELUP("on-level-up", true),
        HEADCRAFT("on-craft-head", false);

        private final String name;
        private final boolean enabled;

        Defaults(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }
    }
}

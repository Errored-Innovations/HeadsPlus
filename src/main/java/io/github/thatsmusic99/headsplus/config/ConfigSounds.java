package io.github.thatsmusic99.headsplus.config;

<<<<<<< HEAD:src/main/java/io/github/thatsmusic99/headsplus/config/HeadsPlusConfigSounds.java
import io.github.thatsmusic99.headsplus.HeadsPlus;

public class HeadsPlusConfigSounds extends ConfigSettings {

    public HeadsPlusConfigSounds() {
        this.conName = "sounds";
        enable();
=======
public class ConfigSounds extends HPConfig {

    private static ConfigSounds instance;

    public ConfigSounds() {
        super("sounds.yml");
        instance = this;
>>>>>>> configuration-rewrite:src/main/java/io/github/thatsmusic99/headsplus/config/ConfigSounds.java
    }

    @Override
    protected void load() {
        super.load();
        for (Defaults d : Defaults.values()) {
<<<<<<< HEAD:src/main/java/io/github/thatsmusic99/headsplus/config/HeadsPlusConfigSounds.java
            getConfig().addDefault("sounds." + d.name + ".sound", HeadsPlus.getInstance().getNMS().getEXPSound().name());
            getConfig().addDefault("sounds." + d.name + ".volume", 1.0f);
            getConfig().addDefault("sounds." + d.name + ".pitch", 1.0f);
            getConfig().addDefault("sounds." + d.name + ".enabled", d.enabled);
=======
            addDefault("sounds." + d.name + ".sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
            addDefault("sounds." + d.name + ".volume", 1.0f);
            addDefault("sounds." + d.name + ".pitch", 1.0f);
            addDefault("sounds." + d.name + ".enabled", d.enabled);
>>>>>>> configuration-rewrite:src/main/java/io/github/thatsmusic99/headsplus/config/ConfigSounds.java
        }
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

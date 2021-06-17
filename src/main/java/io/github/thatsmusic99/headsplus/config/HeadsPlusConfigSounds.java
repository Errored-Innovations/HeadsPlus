package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;

public class HeadsPlusConfigSounds extends ConfigSettings {

    public HeadsPlusConfigSounds() {
        this.conName = "sounds";
        enable();
    }

    @Override
    protected void load() {
        super.load();
        for (Defaults d : Defaults.values()) {
            getConfig().addDefault("sounds." + d.name + ".sound", HeadsPlus.getInstance().getNMS().getEXPSound().name());
            getConfig().addDefault("sounds." + d.name + ".volume", 1.0f);
            getConfig().addDefault("sounds." + d.name + ".pitch", 1.0f);
            getConfig().addDefault("sounds." + d.name + ".enabled", d.enabled);
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

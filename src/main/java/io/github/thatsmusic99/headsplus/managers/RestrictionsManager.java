package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.config.MainConfig;

import java.util.HashMap;
import java.util.List;

public class RestrictionsManager {

    private HashMap<ActionType, List<String>> deniedOptions;
    private boolean useWhitelist;

    public RestrictionsManager() {
        deniedOptions = new HashMap<>();
        useWhitelist = MainConfig.get().getBoolean("whitelist-worlds");

    }

    public void init() {

    }

    private enum ActionType {
        CRAFTING(""),
        LEVELS(""),
        MASKS(""),
        MOBS(""),
        STATS("");

        private String path;

        ActionType(String path) {
            this.path = path;
        }
    }
}

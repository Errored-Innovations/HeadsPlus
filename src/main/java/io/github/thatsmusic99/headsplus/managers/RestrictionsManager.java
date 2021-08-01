package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.config.MainConfig;

import java.io.IOException;
import java.util.List;

public class RestrictionsManager {

    public static boolean canUse(String key, ActionType type) {
        return MainConfig.get().getStringList(type.path).contains(key)
                ^ !MainConfig.get().getBoolean("whitelist-worlds");
    }

    public static void addRestriction(String key, ActionType type) {
        List<String> list = MainConfig.get().getStringList(type.path);
        list.add(key);
        MainConfig.get().set(type.path, list);
        try {
            MainConfig.get().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum ActionType {
        CRAFTING("crafting-list"),
        HEADS("blocked-heads"),
        XP_GAINS("xp-gain"),
        MASKS("masks-list"),
        MOBS("mob-drops-list"),
        STATS("stats-collection");

        private final String path;

        ActionType(String path) {
            this.path = path;
        }
    }
}

package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class LeaderboardsCache {

    private static final HashMap<String, LinkedHashMap<OfflinePlayer, Integer>> cache = new HashMap<>();
    private static final HeadsPlus hp = HeadsPlus.getInstance();
    private static final boolean enabled = hp.getConfiguration().getMechanics().getBoolean("leaderboards.cache-boards");

    public static void init(String type, LinkedHashMap<OfflinePlayer, Integer> contents) {
        if (enabled) {
            cache.put(type, contents);
            Bukkit.getScheduler().runTaskLater(hp, () -> cache.remove(type), hp.getConfiguration().getMechanics().getInt("leaderboards.cache-lifetime-seconds") * 20);
        }
    }

    public static LinkedHashMap<OfflinePlayer, Integer> getType(String section, String database, boolean async, boolean realtime) {
        if (cache.containsKey(database + "_" + section) && !realtime) {
            return cache.get(database + "_" + section);
        }
        if (!async && !realtime) {
            Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.getInstance(), () -> DataManager.getScores(database, section, false));
            return null;
        } else {
            return DataManager.getScores(database, section, false);
        }


    }

}

package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class LeaderboardsCache {

    private static final HashMap<String, LinkedHashMap<OfflinePlayer, Integer>> cache = new HashMap<>();
    private static final HeadsPlus hp = HeadsPlus.get();

    public static void init(String type, LinkedHashMap<OfflinePlayer, Integer> contents) {
        if (MainConfig.get().getLeaderboards().CACHE_LEADERBOARDS) {
            cache.put(type, contents);
            Bukkit.getScheduler().runTaskLater(hp, () -> cache.remove(type), MainConfig.get().getLeaderboards().CACHE_DURATION * 20L);
        }
    }

    public static LinkedHashMap<OfflinePlayer, Integer> getType(String section, String database, boolean async, boolean realtime) {
        if (cache.containsKey(database + "_" + section) && !realtime) {
            return cache.get(database + "_" + section);
        }
        if (!async && !realtime) {
            Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), () -> DataManager.getScores(database, section, false));
            return null;
        } else {
            return DataManager.getScores(database, section, false);
        }


    }

}

package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class LeaderboardsCache {

    private static final HashMap<String, LinkedHashMap<OfflinePlayer, Integer>> cache = new HashMap<>();
    private static final HeadsPlus hp = HeadsPlus.getInstance();
    private static final boolean enabled = hp.getConfiguration().getMechanics().getBoolean("leaderboards.cache-boards");

    public LeaderboardsCache(String type, LinkedHashMap<OfflinePlayer, Integer> contents) {
        if (enabled) {
            cache.put(type, contents);
            new BukkitRunnable() {
                @Override
                public void run() {
                    cache.remove(type);
                }
            }.runTaskLater(hp, hp.getConfiguration().getMechanics().getInt("leaderboards.cache-lifetime-seconds") * 20);
        }


    }

    public static LinkedHashMap<OfflinePlayer, Integer> getType(String section, String database, boolean async, boolean realtime) {
        if (cache.containsKey(database + "_" + section) && !realtime) {
            return cache.get(database + "_" + section);
        } else {
            if (!async && !realtime) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DataManager.getScores(database, section, false);
                    }
                }.runTaskAsynchronously(HeadsPlus.getInstance());
                return null;
            } else {
                return DataManager.getScores(database, section, false);
            }

        }
    }

}

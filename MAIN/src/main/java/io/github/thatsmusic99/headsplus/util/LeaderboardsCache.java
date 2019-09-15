package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LeaderboardsCache {

    private static HashMap<String, LinkedHashMap<OfflinePlayer, Integer>> cache = new HashMap<>();
    private static HeadsPlus hp = HeadsPlus.getInstance();

    public LeaderboardsCache(String type, LinkedHashMap<OfflinePlayer, Integer> contents) {
        if (hp.getConfiguration().getMechanics().getBoolean("leaderboards.cache-boards")) {
            cache.put(type, contents);

            new BukkitRunnable() {
                @Override
                public void run() {
                    cache.remove(type);
                }
            }.runTaskLater(hp, hp.getConfiguration().getMechanics().getInt("leaderboards.cache-lifetime-seconds") * 20);
        }


    }

    public static LinkedHashMap<OfflinePlayer, Integer> getType(String section, String database) throws SQLException {
        if (cache.containsKey(database + "_" + section)) {
            return cache.get(database + "_" + section);
        } else {
            return hp.getMySQLAPI().getScores(section, database);
        }
    }

}

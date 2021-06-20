package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.storage.PlayerScores;
import io.github.thatsmusic99.headsplus.util.LeaderboardsCache;
import io.github.thatsmusic99.headsplus.util.NewMySQLAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

@Deprecated // Going to be reformatted for SQL
public class DataManager {

    private static final HeadsPlus hp = HeadsPlus.get();

    public static LinkedHashMap<OfflinePlayer, Integer> getScores(String database, String section, boolean transfer) {
        if (hp.isConnectedToMySQLDatabase() && !transfer) {
            return NewMySQLAPI.getScores(section, database);
        } else {
            PlayerScores scores = hp.getScores();
            LinkedHashMap<OfflinePlayer, Integer> hs = new LinkedHashMap<>();
            for (Object cs : scores.getJSON().keySet()) {
                if (String.valueOf(cs).equalsIgnoreCase("server-total")) continue;
                OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(cs)));
                int i = scores.getPlayerTotal(String.valueOf(cs), (section.equalsIgnoreCase("total") ? section : section.toUpperCase()), database);
                hs.put(p, i);
            }
            hs = sortHashMapByValues(hs);
            LeaderboardsCache.init(database + "_" + section, hs);
            return hs;
        }
    }

    public static int getPlayerScore(OfflinePlayer player, String database, String section) {
        if (hp.isConnectedToMySQLDatabase()) {
            return NewMySQLAPI.getScore(player, section, database);
        } else {
            return hp.getScores().getPlayerTotal(player.getUniqueId().toString(), section, database);
        }
    }
    public static void addToTotal(OfflinePlayer player, String section, String database, int amount) {
        if (hp.isConnectedToMySQLDatabase()) {
            NewMySQLAPI.addToTotal(database, amount, section, player.getUniqueId().toString());
            NewMySQLAPI.addToTotal(database, amount, section, "server-total");
        } else {
            PlayerScores scores = hp.getScores();
            try {
                scores.addPlayerTotal(player.getUniqueId().toString(), section.toUpperCase(), database, amount);
                scores.addPlayerTotal("server-total", section.toUpperCase(), database, amount);
            } catch (Exception e) {
                scores.setPlayerTotal(player.getUniqueId().toString(), section.toUpperCase(), database, amount);
            }

        }
    }

    public static LinkedHashMap<OfflinePlayer, Integer> sortHashMapByValues(HashMap<OfflinePlayer, Integer> passedMap) {
        List<OfflinePlayer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.reverse(mapValues);

        LinkedHashMap<OfflinePlayer, Integer> sortedMap =
                new LinkedHashMap<>();

        for (int val : mapValues) {
            Iterator<OfflinePlayer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                OfflinePlayer key = keyIt.next();
                Integer comp1 = passedMap.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}

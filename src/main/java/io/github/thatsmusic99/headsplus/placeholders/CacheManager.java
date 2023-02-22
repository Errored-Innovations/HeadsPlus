package io.github.thatsmusic99.headsplus.placeholders;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import io.github.thatsmusic99.headsplus.sql.ChallengeSQLManager;
import io.github.thatsmusic99.headsplus.sql.PlayerSQLManager;
import io.github.thatsmusic99.headsplus.sql.SQLManager;
import io.github.thatsmusic99.headsplus.sql.StatisticsSQLManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CacheManager {

    private final HashMap<String, List<StatisticsSQLManager.LeaderboardEntry>> cachedEntries;
    private final HashMap<String, Long> cachedXP;
    private final HashMap<String, Integer> cachedLevels;
    private final HashMap<String, Integer> cachedChallengeTotal;
    private final HashMap<String, Integer> cachedStats;
    private final HashMap<String, BukkitTask> cachedRequests;
    private final HashMap<String, BukkitTask> refreshTasks;
    private static CacheManager instance;

    public CacheManager() {
        instance = this;
        cachedEntries = new HashMap<>();
        cachedXP = new HashMap<>();
        cachedLevels = new HashMap<>();
        cachedChallengeTotal = new HashMap<>();
        cachedStats = new HashMap<>();
        cachedRequests = new HashMap<>();
        refreshTasks = new HashMap<>();
    }

    public static CacheManager get() {
        return instance;
    }

    public long getXP(OfflinePlayer player) {
        // Check cache
        if (HPPlayer.getHPPlayer(player.getUniqueId()) != null)
            return HPPlayer.getHPPlayer(player.getUniqueId()).getXp();
        UUID uuid = player.getUniqueId();
        // Update caches, use supplier to avoid running SQL query
        updateCaches("xp_" + player.getName(), uuid.toString(), cachedXP, () -> PlayerSQLManager.get().getXP(uuid, true));
        if (cachedXP.containsKey(uuid.toString())) return cachedXP.get(uuid.toString());
        PlayerSQLManager.get().getXP(player.getUniqueId(), true).thenApply(xp -> cachedXP.put(uuid.toString(), xp));
        return -1;
    }

    public String getLevel(OfflinePlayer player) {

        // Check HPPlayer cache, ez
        HPPlayer hpPlayer = HPPlayer.getHPPlayer(player.getUniqueId());
        if (hpPlayer != null) return hpPlayer.getLevel().getConfigName();

        // Get the UUID
        UUID uuid = player.getUniqueId();
        updateCaches("level_" + player.getName(), uuid.toString(), cachedLevels,
                () -> PlayerSQLManager.get().getLevel(uuid, true));
        int i = -1;
        if (cachedLevels.containsKey(uuid.toString())) {
            i = cachedLevels.get(uuid.toString());
        } else {
            PlayerSQLManager.get().getLevel(player.getUniqueId(), true).thenApply(level ->
                    cachedLevels.put(uuid.toString(), level));
        }
        if (i == -1) return null;
        Level level = LevelsManager.get().getLevel(i);
        if (level == null) return null;
        return level.getDisplayName();
    }

    public int getTotalChallengesComplete(OfflinePlayer player) {
        if (HPPlayer.getHPPlayer(player.getUniqueId()) != null)
            return HPPlayer.getHPPlayer(player.getUniqueId()).getCompleteChallenges().size();
        UUID uuid = player.getUniqueId();
        // Update the caches
        updateCaches("challenges_" + player.getName(), uuid.toString(), cachedChallengeTotal,
                () -> ChallengeSQLManager.get().getTotalChallengesComplete(uuid, true));
        //
        if (cachedChallengeTotal.containsKey(uuid.toString())) return cachedChallengeTotal.get(uuid.toString());
        ChallengeSQLManager.get().getTotalChallengesComplete(player.getUniqueId(), true).thenAccept(total ->
                cachedChallengeTotal.put(uuid.toString(), total));
        return -1;
    }

    public List<StatisticsSQLManager.LeaderboardEntry> getEntries(StatisticsSQLManager.CollectionType type) {
        return getEntries(
                type.name(),
                bool -> StatisticsSQLManager.get().getLeaderboardTotal(type, bool)
        );
    }

    public List<StatisticsSQLManager.LeaderboardEntry> getEntries(StatisticsSQLManager.CollectionType type,
                                                                  String head) {
        return getEntries(
                type.name() + "_" + head,
                bool -> StatisticsSQLManager.get().getLeaderboardTotal(type, head, bool)
        );
    }

    public List<StatisticsSQLManager.LeaderboardEntry> getEntriesMeta(StatisticsSQLManager.CollectionType type,
                                                                      String metadata) {
        return getEntries(
                type.name() + "_" + metadata,
                bool -> StatisticsSQLManager.get().getLeaderboardTotalMetadata(type, metadata, bool)
        );
    }

    public List<StatisticsSQLManager.LeaderboardEntry> getEntries(StatisticsSQLManager.CollectionType type,
                                                                  String head, String metadata) {
        return getEntries(
                type.name() + "_" + head + "_" + metadata,
                bool -> StatisticsSQLManager.get().getLeaderboardTotal(type, head, metadata, bool)
        );
    }

    public int getStat(OfflinePlayer player, StatisticsSQLManager.CollectionType type) {
        return getStat(
                player.getName() + "_" + type.name(),
                bool -> StatisticsSQLManager.get().getStat(player.getUniqueId(), type, bool)
        );
    }

    public int getStat(
            @NotNull OfflinePlayer player,
            @NotNull StatisticsSQLManager.CollectionType type,
            @NotNull String head) {
        return getStat(
                String.join("_", player.getName(), type.name(), head),
                bool -> StatisticsSQLManager.get().getStat(player.getUniqueId(), type, head, bool)
        );
    }

    public int getStatMeta(
            @NotNull OfflinePlayer player,
            @NotNull StatisticsSQLManager.CollectionType type,
            @NotNull String metadata
    ) {
        return getStat(
                String.join("_", player.getName(), type.name(), metadata),
                bool -> StatisticsSQLManager.get().getStatMeta(player.getUniqueId(), type, metadata, bool)
        );
    }

    public int getStat(
            @NotNull OfflinePlayer player,
            @NotNull StatisticsSQLManager.CollectionType type,
            @NotNull String head,
            @NotNull String metadata
    ) {
        return getStat(String.join("_", player.getName(), type.name(), head, metadata),
                bool -> StatisticsSQLManager.get().getStat(player.getUniqueId(), type, head, metadata, bool));
    }

    public int getStat(
            @NotNull String id,
            @NotNull Function<Boolean, CompletableFuture<Integer>> ugh
    ) {

        // Prompt a cache update
        updateCaches("stats_" + id, id, cachedStats, () -> ugh.apply(true));

        // If the statistic is cached, then return it
        if (cachedStats.containsKey(id)) return cachedStats.get(id);

        // If it isn't and we want to force the result:
        if (MainConfig.get().getLeaderboards().FORCE_PLACEHOLDERS) {
            int result = ugh.apply(false).join();
            cachedStats.put(id, result);
            return result;
        }

        // Otherwise, request it to be updated
        ugh.apply(true).thenApply(num -> cachedStats.put(id, num));
        return -1;
    }

    private List<StatisticsSQLManager.LeaderboardEntry> getEntries(
            String id,
            Function<Boolean, CompletableFuture<List<StatisticsSQLManager.LeaderboardEntry>>> ree
    ) {

        // Prompt a cache update
        updateCaches("leaderboards_" + id, id, cachedEntries, () -> ree.apply(true));

        // If the statistic is cached, then return it
        if (cachedEntries.containsKey(id)) return cachedEntries.get(id);

        // If it isn't and we want to force the result:
        if (MainConfig.get().getLeaderboards().FORCE_PLACEHOLDERS) {
            List<StatisticsSQLManager.LeaderboardEntry> entries = ree.apply(false).join();
            cachedEntries.put(id, entries);
            return entries;
        }

        // Otherwise request an update
        ree.apply(true).thenApply(list -> cachedEntries.put(id, list));
        return new ArrayList<>();
    }

    private <T> void updateCaches(String key, String id, HashMap<String, T> hashMap,
                                  SQLManager.SQLSupplier<CompletableFuture<T>> runnable) {
        int duration = MainConfig.get().getLeaderboards().CACHE_DURATION;
        if (cachedRequests.containsKey(key)) {
            cachedRequests.get(key).cancel();
        }
        makeRequest(key);
        refreshTasks.putIfAbsent(key, new BukkitRunnable() {
            @Override
            public void run() {
                if (!cachedRequests.containsKey(key)) {
                    cancel();
                    hashMap.remove(id);
                    return;
                }
                try {
                    runnable.getWithSQL().thenApply(run -> hashMap.put(id, run));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(HeadsPlus.get(), duration, duration));
    }

    private void makeRequest(String key) {
        cachedRequests.put(key, Bukkit.getScheduler().runTaskLater(HeadsPlus.get(), () -> cachedRequests.remove(key),
                MainConfig.get().getLeaderboards().CACHE_DURATION));
    }
}

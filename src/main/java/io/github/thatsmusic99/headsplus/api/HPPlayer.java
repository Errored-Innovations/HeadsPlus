package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.LevelUpEvent;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import io.github.thatsmusic99.headsplus.sql.ChallengeSQLManager;
import io.github.thatsmusic99.headsplus.sql.FavouriteHeadsSQLManager;
import io.github.thatsmusic99.headsplus.sql.PinnedChallengeManager;
import io.github.thatsmusic99.headsplus.sql.PlayerSQLManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HPPlayer {

    private final UUID uuid;
    private long xp;
    private int level;
    private int nextLevel = -1;
    public static final HashMap<UUID, HPPlayer> players = new HashMap<>();
    private final List<String> favouriteHeads;
    private final List<String> pinnedChallenges;
    private final List<String> completeChallenges;

    public HPPlayer(UUID uuid) {
        pinnedChallenges = HPUtils.ifNull(getValue(PinnedChallengeManager.get().getPinnedChallenges(uuid), "pinned challenges"), new ArrayList<>());
        favouriteHeads = HPUtils.ifNull(getValue(FavouriteHeadsSQLManager.get().getFavouriteHeads(uuid), "favourite heads"), new ArrayList<>());
        completeChallenges = HPUtils.ifNull(getValue(ChallengeSQLManager.get().getCompleteChallenges(uuid), "complete challenges"), new ArrayList<>());
        level = HPUtils.ifNull(getValue(PlayerSQLManager.get().getLevel(uuid, false), "level"), 0);
        int max = LevelsManager.get().getLevels().size();
        if (level > -1 && level + 1 < max) {
            this.nextLevel = level + 1;
        }
        PlayerSQLManager.get().getLocale(uuid).thenAccept(result ->
                result.ifPresent(str ->
                        MessagesManager.get().setPlayerLocale((Player) getPlayer(), str)));
        xp = HPUtils.ifNull(getValue(PlayerSQLManager.get().getXP(uuid, true), "XP"), (long) 0);
        this.uuid = uuid;
        players.put(uuid, this);
    }

    public long getXp() {
        return xp;
    }

    public Level getLevel() {
        return LevelsManager.get().getLevel(level);
    }

    public Level getNextLevel() {
        return LevelsManager.get().getLevel(nextLevel);
    }

    public List<String> getCompleteChallenges() {
        return completeChallenges;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public static HPPlayer getHPPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public CompletableFuture<Void> addCompleteChallenge(Challenge c) {
        completeChallenges.add(c.getConfigName());
        return ChallengeSQLManager.get().completeChallenge(uuid, c.getConfigName(), true);
    }

    public void addXp(long xp) {
        setXp(this.xp + xp);
    }

    public void removeXp(long xp) {
        setXp(this.xp - xp);
    }

    public void setXp(long xp) {
        HeadsPlus hp = HeadsPlus.get();
        PlayerSQLManager.get().setXP(uuid, xp);
        this.xp = xp;

        if (MainConfig.get().getMainFeatures().LEVELS) {
            if (xp == 0) {
                resetLevel();
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (nextLevel < 0 || nextLevel >= LevelsManager.get().getLevels().size()) return;
                    Level nextLevelLocal = LevelsManager.get().getLevel(nextLevel);
                    int jumps = 0;
                    while (nextLevelLocal != null && nextLevelLocal.getRequiredXP() <= getXp()) {
                        jumps++;
                        Level level = nextLevelLocal;
                        nextLevelLocal = LevelsManager.get().getNextLevel(nextLevelLocal.getConfigName());
                        if (MainConfig.get().getLevels().MULTIPLE_LEVEL_UPS) initLevelUp(1);
                        if (level.isrEnabled()) level.getReward().rewardPlayer(null, (Player) getPlayer());
                    }
                    if (!MainConfig.get().getLevels().MULTIPLE_LEVEL_UPS && jumps > 0) initLevelUp(jumps);

                    HPUtils.addBossBar(getPlayer());
                }
            }.runTask(hp);
        }
    }

    private void initLevelUp(int jumps) {
        Level nextLevel = LevelsManager.get().getLevel(this.level + jumps);
        LevelUpEvent event = new LevelUpEvent((Player) getPlayer(), LevelsManager.get().getLevel(level), nextLevel);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.level += jumps;
        this.nextLevel += jumps;
        Player player = (Player) getPlayer();
        if (MainConfig.get().getLevels().BROADCAST_LEVEL_UP) {
            final String name = player.isOnline() ? player.getPlayer().getDisplayName() : player.getName();
            for (Player p : Bukkit.getOnlinePlayers()) {
                MessagesManager.get().sendMessage("commands.levels.level-up", p, "{player}", name, "{name}", name,
                        "{level}",
                        ChatColor.translateAlternateColorCodes('&', nextLevel.getDisplayName()));
            }
        }
        PlayerSQLManager.get().setLevel(this.uuid, nextLevel.getConfigName());
    }

    private void resetLevel() {
        PlayerSQLManager.get().setLevel(this.uuid, LevelsManager.get().getLevel(0).getConfigName());
        level = 0;
        nextLevel = 1;
    }

    public boolean hasHeadFavourited(String s) {
        return favouriteHeads.contains(s);
    }

    public CompletableFuture<Void> addFavourite(String s) {
        favouriteHeads.add(s);
        return FavouriteHeadsSQLManager.get().addHead(uuid, s);
    }

    public CompletableFuture<Void> removeFavourite(String s) {
        favouriteHeads.remove(s);
        return FavouriteHeadsSQLManager.get().removeHead(uuid, s);
    }

    public List<String> getPinnedChallenges() {
        return pinnedChallenges;
    }

    public boolean hasChallengePinned(Challenge challenge) {
        return pinnedChallenges.contains(challenge.getConfigName());
    }

    public CompletableFuture<Void> addChallengePin(Challenge challenge) {
        pinnedChallenges.add(challenge.getConfigName());
        return PinnedChallengeManager.get().addChallenge(uuid, challenge.getConfigName());
    }

    public CompletableFuture<Void> removeChallengePin(Challenge challenge) {
        pinnedChallenges.remove(challenge.getConfigName());
        return PinnedChallengeManager.get().removeChallenge(uuid, challenge.getConfigName());
    }

    public List<String> getFavouriteHeads() {
        return favouriteHeads;
    }

    public static void reload() {
        players.clear();
        Bukkit.getOnlinePlayers().forEach(player -> new HPPlayer(player.getUniqueId()));
    }

    public static void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    private <T> T getValue(CompletableFuture<T> task, String object) {
        try {
            return task.get();
        } catch (InterruptedException e) {
            HeadsPlus.get().getLogger().severe("Failed to get data for " + object + ": interrupted thread. Please try" +
                    " again or restart the server. If none of the above works, please consult the necessary support" +
                    " services (e.g. hosting)..");
            e.printStackTrace();
        } catch (ExecutionException e) {
            HeadsPlus.get().getLogger().severe("Failed to get data for " + object + ": execution failed, " +
                    "an internal error occurred. Please send the console error to the developer.");
            e.printStackTrace();
        }
        return null;
    }
}

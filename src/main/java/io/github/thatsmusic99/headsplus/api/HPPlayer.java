package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.LevelUpEvent;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import io.github.thatsmusic99.headsplus.sql.ChallengeSQLManager;
import io.github.thatsmusic99.headsplus.sql.FavouriteHeadsSQLManager;
import io.github.thatsmusic99.headsplus.sql.PinnedChallengeManager;
import io.github.thatsmusic99.headsplus.sql.PlayerSQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HPPlayer {

    private final UUID uuid;
    private long xp;
    private String level = null;
    private String nextLevel = null;
    public static HashMap<UUID, HPPlayer> players = new HashMap<>();
    private final List<String> favouriteHeads;
    private final List<String> pinnedChallenges;
    private final List<String> completeChallenges;

    // TODO - make sure this is never called on the main server thread.
    public HPPlayer(UUID uuid) {
        pinnedChallenges = PinnedChallengeManager.get().getPinnedChallenges(uuid).join();
        favouriteHeads = FavouriteHeadsSQLManager.get().getFavouriteHeads(uuid).join();
        completeChallenges = ChallengeSQLManager.get().getCompleteChallenges(uuid).join();
        int levelIndex = PlayerSQLManager.get().getLevel(uuid).join();
        int max = LevelsManager.get().getLevels().size();
        if (levelIndex > -1 && levelIndex < max) {
            this.level = LevelsManager.get().getLevels().get(levelIndex);
            if (levelIndex + 1 < max) this.nextLevel = LevelsManager.get().getLevels().get(levelIndex + 1);
        }
        PlayerSQLManager.get().getLocale(uuid).thenAccept(result ->
                result.ifPresent(str ->
                        HeadsPlusMessagesManager.get().setPlayerLocale((Player) getPlayer(), str)));
        xp = PlayerSQLManager.get().getXP(uuid).join();
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

    public static HPPlayer getHPPlayer(OfflinePlayer p) {
        return players.get(p.getUniqueId());
    }

    public CompletableFuture<Void> addCompleteChallenge(Challenge c) {
        completeChallenges.add(c.getConfigName());
        return ChallengeSQLManager.get().completeChallenge(uuid, c.getConfigName());
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
            new BukkitRunnable() {
                @Override
                public void run() {
                    Level nextLevelLocal = LevelsManager.get().getLevel(nextLevel);
                    while (nextLevelLocal != null && nextLevelLocal.getRequiredXP() <= getXp()) {
                        nextLevelLocal = LevelsManager.get().getNextLevel(nextLevelLocal.getConfigName());
                        if (MainConfig.get().getLevels().MULTIPLE_LEVEL_UPS) initLevelUp();
                    }
                    if (!MainConfig.get().getLevels().MULTIPLE_LEVEL_UPS) initLevelUp();
                }
            }.runTask(hp);    
        }
    }

    private void initLevelUp() {
        Level nextLevel = LevelsManager.get().getLevel(this.nextLevel);
        LevelUpEvent event = new LevelUpEvent((Player) getPlayer(), LevelsManager.get().getLevel(level), nextLevel);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.level = this.nextLevel;
        this.nextLevel = LevelsManager.get().getNextLevel(this.level).getConfigName();
        Player player = (Player) getPlayer();
        if (MainConfig.get().getLevels().BROADCAST_LEVEL_UP) {
            final String name = player.isOnline() ? player.getPlayer().getDisplayName() : player.getName();
            for (Player p : Bukkit.getOnlinePlayers()) {
                HeadsPlusMessagesManager.get().sendMessage("commands.levels.level-up", p, "{player}", name, "{name}", name, "{level}",
                        ChatColor.translateAlternateColorCodes('&', nextLevel.getDisplayName()));
            }
        }
        if (nextLevel.isrEnabled()) {
            nextLevel.getReward().rewardPlayer(null, player.getPlayer()); // TODO
        }
        PlayerSQLManager.get().setLevel(this.uuid, nextLevel.getConfigName());
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
}

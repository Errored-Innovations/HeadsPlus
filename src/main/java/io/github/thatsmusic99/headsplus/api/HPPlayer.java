package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.LevelUpEvent;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.sql.FavouriteHeadsSQLManager;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import io.github.thatsmusic99.headsplus.sql.PinnedChallengeManager;
import io.github.thatsmusic99.headsplus.sql.PlayerSQLManager;
import io.github.thatsmusic99.headsplus.storage.PlayerScores;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HPPlayer {

    private UUID player;
    private long xp;
    private String level = null;
    private List<String> completeChallenges;
    private String nextLevel = null;
    private String currentMask;
    private List<PotionEffect> activeMask;
    public static HashMap<UUID, HPPlayer> players = new HashMap<>();
    private volatile List<String> favouriteHeads;
    private volatile List<String> pinnedChallenges;
    private String cachedLocale;
    private boolean localeForced;

    // TODO - make sure this is never called on the main server thread.
    public HPPlayer(UUID uuid) {
        HeadsPlus hp = HeadsPlus.get();
        activeMask = new ArrayList<>();
        pinnedChallenges = PinnedChallengeManager.get().getPinnedChallenges(uuid).join();
        favouriteHeads = FavouriteHeadsSQLManager.get().getFavouriteHeads(uuid).join();
        int levelIndex = PlayerSQLManager.get().getLevel(uuid).join();
        if (levelIndex > -1 && levelIndex < LevelsManager.get().getLevels().size()) {
            this.level = LevelsManager.get().getLevels().get(levelIndex);
            this.nextLevel = LevelsManager.get().getLevels().get(levelIndex + 1); // TODO - IOBE check
        }
        xp = PlayerSQLManager.get().getXP(uuid).join();
        this.player = uuid;
        PlayerScores scores = hp.getScores();
        List<String> sc = new ArrayList<>();
        sc.addAll(scores.getCompletedChallenges(uuid.toString()));
        if (MainConfig.get().getLocalisation().SMART_LOCALE && getPlayer().isOnline()) {
            String loc = scores.getLocale(uuid.toString());
            if (loc != null && !loc.isEmpty() && !loc.equalsIgnoreCase("null")) {
                cachedLocale = loc.split(":")[0];
                localeForced = Boolean.parseBoolean(loc.split(":")[1]);
                HeadsPlusMessagesManager.get().setPlayerLocale((Player) getPlayer(), cachedLocale,  false);
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        HeadsPlusMessagesManager.get().setPlayerLocale((Player) getPlayer());
                        cachedLocale = HeadsPlusMessagesManager.get().getSetLocale((Player) getPlayer());
                        localeForced = false;
                        scores.setLocale(uuid.toString(), cachedLocale, false);
                    }
                }.runTaskLaterAsynchronously(hp, 100);
            }
        } else {
            cachedLocale = "";
            localeForced = false;
        }
        this.completeChallenges = sc;
        players.put(uuid, this);
    }

    public void clearMask() {
        try {
            currentMask = "";
            if (!getPlayer().isOnline()) return;
            for (PotionEffect p : activeMask) {
                ((Player) getPlayer()).removePotionEffect(p.getType());
            }
            activeMask.clear();
        } catch (NullPointerException ignored) { // When the mask messes up

        }
    }

    public void tempClearMasks() {
        try {
            if (!getPlayer().isOnline()) return;
            for (PotionEffect p : activeMask) {
                ((Player) getPlayer()).removePotionEffect(p.getType());
            }
        } catch (NullPointerException ignored) {
        }
    }

    public void addMask(String s) {
        if (s.equalsIgnoreCase("WANDERING_TRADER") || s.equalsIgnoreCase("TRADER_LLAMA")) {
            s = s.toLowerCase();
        } else {
            s = s.toLowerCase().replaceAll("_", "");
        }
        ConfigMobs hpch = ConfigMobs.get();
        List<PotionEffect> po = new ArrayList<>();
        // TODO mask rework
        for (int i = 0; i < hpch.getStringList(s + ".mask-effects").size(); i++) {
            String is = hpch.getStringList(s + ".mask-effects").get(i).toUpperCase();
            int amp = (int) hpch.getList(s + ".mask-amplifiers").get(i);
            
            try {
                PotionEffectType type = PotionEffectType.getByName(is);
                if (type == null) {
                    HeadsPlus.get().getLogger().severe("Invalid potion type detected. Please check your masks configuration in heads.yml! (" + is + ", " + s + ")");
                    continue;
                }
                PotionEffect p = new PotionEffect(type, MainConfig.get().getMasks().EFFECT_LENGTH, amp);
                ((Player) getPlayer()).addPotionEffect(p);
                po.add(p);
            } catch (IllegalArgumentException ex) {
                HeadsPlus.get().getLogger().severe("Invalid potion type detected. Please check your masks configuration in heads.yml!");
            }
        }
        activeMask = po;
        currentMask = s;
    }

    public void refreshMasks() {
        for (PotionEffect effect : activeMask) {
            Player player = (Player) getPlayer();
            player.removePotionEffect(effect.getType());
            player.addPotionEffect(new PotionEffect(effect.getType(), MainConfig.get().getMasks().EFFECT_LENGTH, effect.getAmplifier()));
        }
    }

    public void clearAllMasks() {
        for (PotionEffect effect : activeMask) {
            Player player = (Player) getPlayer();
            player.removePotionEffect(effect.getType());
        }
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
        return Bukkit.getOfflinePlayer(player);
    }

    public UUID getUuid() {
        return player;
    }

    public static HPPlayer getHPPlayer(OfflinePlayer p) {
        UUID uuid = p.getUniqueId();
        return players.get(uuid) != null ? players.get(uuid) : new HPPlayer(uuid);
    }

    public List<PotionEffect> getActiveMasks() {
        return activeMask;
    }

    public String getActiveMaskType() {
        return currentMask;
    }

    public String getLocale() {
        return cachedLocale;
    }

    public boolean isLocaleForced() {
        return localeForced;
    }

    public void setLocale(String locale) {
        setLocale(locale, true);
    }

    public void setLocale(String locale, boolean forced) {
        cachedLocale = locale;
        localeForced = forced;
        HeadsPlus.get().getScores().setLocale(getPlayer().getUniqueId().toString(), locale, forced);
    }

    public void addCompleteChallenge(Challenge c) {
        PlayerScores scores = HeadsPlus.get().getScores();
        scores.completeChallenge(player.toString(), c);
        completeChallenges.add(c.getConfigName());
    }

    public void addXp(long xp) {
        setXp(this.xp + xp);
    }

    public void removeXp(long xp) {
        setXp(this.xp - xp);
    }

    public void setXp(long xp) {
        HeadsPlus hp = HeadsPlus.get();
        PlayerSQLManager.get().setXP(player, xp);
        this.xp = xp;
        if (MainConfig.get().getMainFeatures().LEVELS) {
            new BukkitRunnable() {
                @Override
                public void run() {
	            Level nextLevelLocal = LevelsManager.get().getLevel(nextLevel);
		    // TODO - multiple or just a single level up? Config option is needed
                    while (nextLevelLocal != null && nextLevelLocal.getRequiredXP() <= getXp()) {
                        LevelUpEvent event = new LevelUpEvent((Player) getPlayer(), LevelsManager.get().getLevel(level), nextLevelLocal);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return;
                        level = nextLevel;
                        nextLevel = nextLevelLocal.getConfigName();
                        Player player = (Player) getPlayer();
                        if (MainConfig.get().getLevels().BROADCAST_LEVEL_UP) {
                            final String name = player.isOnline() ? player.getPlayer().getDisplayName() : player.getName();
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                HeadsPlusMessagesManager.get().sendMessage("commands.levels.level-up", p, "{player}", name, "{name}", name, "{level}", ChatColor.translateAlternateColorCodes('&', nextLevelLocal.getDisplayName()));
                            }
                        }
                        if (nextLevelLocal.isrEnabled()) {
                            nextLevelLocal.getReward().rewardPlayer(null, player.getPlayer()); // TODO
                        }
                        PlayerSQLManager.get().setLevel(player.getUniqueId(), level);
                    }
                }
            }.runTask(hp);    
        }
    }

    public boolean hasHeadFavourited(String s) {
        return favouriteHeads.contains(s);
    }

    public CompletableFuture<Void> addFavourite(String s) {
        favouriteHeads.add(s);
        return FavouriteHeadsSQLManager.get().addHead(player, s);
    }

    public CompletableFuture<Void> removeFavourite(String s) {
        favouriteHeads.remove(s);
        return FavouriteHeadsSQLManager.get().removeHead(player, s);
    }

    public CompletableFuture<List<String>> getPinnedChallenges() {
        if (pinnedChallenges != null) return CompletableFuture.completedFuture(pinnedChallenges);
        return PinnedChallengeManager.get().getPinnedChallenges(player).thenApply(list -> {
            pinnedChallenges = list;
            return pinnedChallenges;
        });
    }

    public boolean hasChallengePinned(Challenge challenge) {
        return pinnedChallenges.contains(challenge.getConfigName());
    }

    public CompletableFuture<Void> addChallengePin(Challenge challenge) {
        pinnedChallenges.add(challenge.getConfigName());
        return PinnedChallengeManager.get().addChallenge(player, challenge.getConfigName());
    }

    public CompletableFuture<Void> removeChallengePin(Challenge challenge) {
        pinnedChallenges.remove(challenge.getConfigName());
        return PinnedChallengeManager.get().removeChallenge(player, challenge.getConfigName());
    }

    public CompletableFuture<List<String>> getFavouriteHeads() {
        if (favouriteHeads != null) return CompletableFuture.completedFuture(favouriteHeads);
        return FavouriteHeadsSQLManager.get().getFavouriteHeads(player).thenApply(heads -> {
            favouriteHeads = heads;
            return favouriteHeads;
        });
    }
}

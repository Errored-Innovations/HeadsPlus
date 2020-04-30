package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.LevelUpEvent;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.storage.PlayerScores;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HPPlayer {

    // M
    private UUID player;
    private int xp;
    private Level level = null;
    private List<String> completeChallenges;
    private Level nextLevel = null;
    private HashMap<String, List<PotionEffect>> activeMasks;
    public static HashMap<UUID, HPPlayer> players = new HashMap<>();
    private List<String> favouriteHeads;
    private List<String> pinnedChallenges;
    private boolean ignoreFallDamage;
    private String cachedLocale;
    private boolean localeForced;

    public HPPlayer(OfflinePlayer p) {
        if (Bukkit.getPlayer(p.getUniqueId()) == null) return;
        HeadsPlus hp = HeadsPlus.getInstance();
        activeMasks = new HashMap<>();
        favouriteHeads = new ArrayList<>();
        pinnedChallenges = new ArrayList<>();
        ignoreFallDamage = false;
        this.player = p.getUniqueId();
        try {
            for (Object o : (JSONArray) hp.getFavourites().getJSON().get(p.getUniqueId().toString())) {
                favouriteHeads.add(String.valueOf(o));
            }
            for (Object o : (JSONArray) hp.getPinned().getJSON().get(player.toString())) {
                pinnedChallenges.add(String.valueOf(o));
            }
        } catch (NullPointerException ignored) {
        }
        PlayerScores scores = hp.getScores();
        HashMap<Integer, Level> levels = hp.getLevels();
        this.xp = scores.getXp(p.getUniqueId().toString());
        List<String> sc = new ArrayList<>();
        sc.addAll(scores.getCompletedChallenges(p.getUniqueId().toString()));
        if (hp.getConfiguration().getConfig().getBoolean("smart-locale")) {
            String loc = scores.getLocale(p.getUniqueId().toString());
            if (loc != null && !loc.isEmpty() && !loc.equalsIgnoreCase("null")) {
                cachedLocale = loc.split(":")[0];
                localeForced = Boolean.parseBoolean(loc.split(":")[1]);
                hp.getMessagesConfig().setPlayerLocale(getPlayer().getPlayer(), cachedLocale,  false);
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        hp.getMessagesConfig().setPlayerLocale(getPlayer());
                        cachedLocale = hp.getMessagesConfig().getSetLocale(getPlayer());
                        localeForced = false;
                        scores.setLocale(player.toString(), cachedLocale, false);
                    }
                }.runTaskLaterAsynchronously(hp, 100);
            }
        } else {
            cachedLocale = "";
            localeForced = false;
        }
        if (hp.usingLevels()) {
            if (scores.getLevel(p.getUniqueId().toString()).isEmpty()) {
                for (int i = levels.size(); i > 0; i--) {
                    try {
                        if (levels.get(i).getRequiredXP() <= xp) {
                            level = levels.get(i);
                            scores.setLevel(p.getUniqueId().toString(), level.getConfigName());
                            try {
                                nextLevel = levels.get(i + 1);
                            } catch (IndexOutOfBoundsException e) { // End of levels
                                nextLevel = null;
                            }
                            break;
                        }
                    } catch (NullPointerException ignored) { // Continue
                    }

                }
            } else {
                for (int i = levels.size(); i > 0; i--) {
                    try {
                        if (levels.get(i).getConfigName().equals(scores.getLevel(p.getUniqueId().toString()))) {
                            level = levels.get(i);
                            try {
                                nextLevel = levels.get(i + 1);
                            } catch (IndexOutOfBoundsException e) { // End of levels
                                nextLevel = null;
                            }
                            break;
                        }
                    } catch (NullPointerException ignored) {

                    }

                }
            }
        }
        this.completeChallenges = sc;
        players.put(getPlayer().getUniqueId(), this);
    }

    public void clearMask(String type) {
        try {
            for (PotionEffect p : getActiveMasks(type)) {
                ((Player) getPlayer()).removePotionEffect(p.getType());
            }
            ignoreFallDamage = false;
            activeMasks.remove(type);
        } catch (NullPointerException ignored) { // When the mask messes up

        }

    }

    public void addMask(String s) {
        HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
        List<PotionEffect> po = new ArrayList<>();
        for (int i = 0; i < hpch.getConfig().getStringList(s + ".mask-effects").size(); i++) {
            String is = hpch.getConfig().getStringList(s + ".mask-effects").get(i).toUpperCase();
            if (is.equalsIgnoreCase("ignore-fall-damage")) {
                ignoreFallDamage = true;
                continue;
            }
            int amp;
            if (ignoreFallDamage) {
                amp = hpch.getConfig().getIntegerList(s + ".mask-amplifiers").get(i - 1);
            } else {
                amp = hpch.getConfig().getIntegerList(s + ".mask-amplifiers").get(i);
            }

            try {
                PotionEffectType type = PotionEffectType.getByName(is);
                if (type == null) {
                    HeadsPlus.getInstance().getLogger().severe("Invalid potion type detected. Please check your masks configuration in heads.yml! (" + is + ", " + s + ")");
                    continue;
                }
                PotionEffect p = new PotionEffect(type, 200, amp);
                getPlayer().addPotionEffect(p);
                po.add(p);
            } catch (IllegalArgumentException ex) {
                HeadsPlus.getInstance().getLogger().severe("Invalid potion type detected. Please check your masks configuration in heads.yml!");
            }
        }
        activeMasks.put(s, po);
    }

    public void refreshMasks() {
        for (String key : getActiveMaskTypes()) {
            if (getActiveMasks(key) != null) {
                for (PotionEffect effect : getActiveMasks(key)) {
                    Player player = getPlayer();
                    player.removePotionEffect(effect.getType());
                    player.addPotionEffect(new PotionEffect(effect.getType(), 200, effect.getAmplifier()));
                }
            }
        }
    }

    public void clearAllMasks() {
        for (String key : getActiveMaskTypes()) {
            if (getActiveMasks(key) != null) {
                for (PotionEffect effect : getActiveMasks(key)) {
                    Player player = getPlayer();
                    player.removePotionEffect(effect.getType());
                }
            }
        }
    }

    public int getXp() {
        return xp;
    }

    public Level getLevel() {
        return level;
    }

    public Level getNextLevel() {
        return nextLevel;
    }

    public List<String> getCompleteChallenges() {
        return completeChallenges;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public static HPPlayer getHPPlayer(OfflinePlayer p) {
        UUID uuid = p.getUniqueId();
        return players.get(uuid) != null ? players.get(uuid) : new HPPlayer(p);
    }

    public List<PotionEffect> getActiveMasks(String type) {
        return activeMasks.get(type);
    }

    public List<String> getActiveMaskTypes() {
        return new ArrayList<>(activeMasks.keySet());
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
        HeadsPlus.getInstance().getScores().setLocale(getPlayer().getUniqueId().toString(), locale, forced);
    }

    public void addCompleteChallenge(Challenge c) {
        PlayerScores scores = HeadsPlus.getInstance().getScores();
        scores.completeChallenge(player.toString(), c);
        completeChallenges.add(c.getConfigName());
    }

    public void addXp(int xp) {
        setXp(this.xp + xp);
    }

    public void removeXp(int xp) {
        setXp(this.xp - xp);
    }

    public void setXp(int xp) {
        HeadsPlus hp = HeadsPlus.getInstance();
        PlayerScores scores = HeadsPlus.getInstance().getScores();
        scores.setXp(player.toString(), xp);
        this.xp = xp;
        if (hp.usingLevels()) {
            if (nextLevel != null) {
                if (nextLevel.getRequiredXP() <= getXp()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            LevelUpEvent event = new LevelUpEvent(getPlayer(), level, nextLevel);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                level = nextLevel;
                                Player player = getPlayer();
                                if (hp.getConfiguration().getMechanics().getBoolean("broadcasts.level-up")) {
                                    final String name = player.isOnline() ? player.getPlayer().getDisplayName() : player.getName();
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        p.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("commands.levels.level-up", p)
                                                .replaceAll("\\{player}", name)
                                                .replaceAll("\\{name}", name)
                                                .replaceAll("\\{level}", ChatColor.translateAlternateColorCodes('&', level.getDisplayName())));
                                    }
                                }
                                HashMap<Integer, Level> levels = HeadsPlus.getInstance().getLevels();
                                scores.setLevel(player.getUniqueId().toString(), level.getConfigName());
                                if (level.isrEnabled()) {
                                    level.reward(player.getPlayer());
                                }
                                for (int i = 1; i < levels.size(); i++) {
                                    if (levels.get(i) == level) {
                                        try {
                                            nextLevel = levels.get(i + 1);
                                        } catch (IndexOutOfBoundsException e) { // End of levels
                                            nextLevel = null;
                                        }
                                    }
                                }
                            }
                        }
                    }.runTask(hp);

                } else if (level.getRequiredXP() > getXp()) {
                    HashMap<Integer, Level> levels = hp.getLevels();
                    for (int i = 1; i < levels.size(); i++) {
                        if (levels.get(i).getRequiredXP() <= getXp()) {
                            try {
                                level = levels.get(i);
                                nextLevel = levels.get(i + 1);
                                return;
                            } catch (IndexOutOfBoundsException e) { // End of levels
                                nextLevel = null;
                            }
                        }
                    }
                    level = null;
                    nextLevel = levels.get(1);
                }
            }
        }
    }

    public boolean hasHeadFavourited(String s) {
        return favouriteHeads.contains(s);
    }

    public void addFavourite(String s) {
        favouriteHeads.add(s);
        HeadsPlus.getInstance().getFavourites().writeData(getPlayer(), s);
    }

    public void removeFavourite(String s) {
        favouriteHeads.remove(s);
        HeadsPlus.getInstance().getFavourites().removeHead(getPlayer(), s);
    }

    public boolean hasChallengePinned(Challenge challenge) {
        return pinnedChallenges.contains(challenge.getConfigName());
    }

    public void addChallengePin(Challenge challenge) {
        String s = challenge.getConfigName();
        pinnedChallenges.add(s);
        HeadsPlus.getInstance().getPinned().writeData(getPlayer(), s);
    }

    public void removeChallengePin(Challenge challenge) {
        String s = challenge.getConfigName();
        pinnedChallenges.remove(s);
        HeadsPlus.getInstance().getPinned().removeChallenge(getPlayer(), s);
    }

    public List<String> getPinnedChallenges() {
        return pinnedChallenges;
    }

    public boolean isIgnoringFallDamage() {
        return ignoreFallDamage;
    }

    public List<String> getFavouriteHeads() {
        return favouriteHeads;
    }
}

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
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HPPlayer {

    // M
    private OfflinePlayer player;
    private int xp;
    private RLevel level = null;
    private List<Challenge> completeChallenges;
    private RLevel nextLevel = null;
    private List<PotionEffect> activeMasks;
    public static List<HPPlayer> players = new ArrayList<>();
    private List<String> favouriteHeads;
    private boolean ignoreFallDamage;

    public HPPlayer(OfflinePlayer p) {
        activeMasks = new ArrayList<>();
        favouriteHeads = new ArrayList<>();
        ignoreFallDamage = false;
        this.player = p;
        try {
            for (Object o : (JSONArray) HeadsPlus.getInstance().getFavourites().getJSON().get(p.getUniqueId().toString())) {
                favouriteHeads.add(String.valueOf(o));
            }
        } catch (NullPointerException ignored) {
        }
        HeadsPlus hp = HeadsPlus.getInstance();
        PlayerScores scores = hp.getScores();
        HeadsPlusAPI hapi = hp.getAPI();
        HashMap<Integer, RLevel> levels = hp.getLevels();
        this.xp = scores.getXp(p.getUniqueId().toString());
        List<Challenge> sc = new ArrayList<>();
        for (String str : scores.getCompletedChallenges(p.getUniqueId().toString())) {
            sc.add(hapi.getChallengeByConfigName(str));
        }
        if (hp.usingLevels()) {
            if (scores.getLevel(p.getUniqueId().toString()).isEmpty()) {
                for (int i = levels.size() - 1; i > 0; i--) {
                    if (levels.get(i).getRequiredXP() <= getXp()) {
                        level = levels.get(i);
                        scores.setLevel(p.getUniqueId().toString(), level.getConfigName());
                        try {
                            nextLevel = levels.get(i + 1);
                        } catch (IndexOutOfBoundsException e) { // End of levels
                            nextLevel = null;
                        }
                        break;
                    }
                }
            } else {
                for (int i = levels.size() - 1; i > 0; i--) {
                    if (levels.get(i).getConfigName().equals(scores.getLevel(p.getUniqueId().toString()))) {
                        level = levels.get(i);
                        try {
                            nextLevel = levels.get(i + 1);
                        } catch (IndexOutOfBoundsException e) { // End of levels
                            nextLevel = null;
                        }
                        break;
                    }
                }
            }
        }
        this.completeChallenges = sc;
    }

    public void clearMask() {
        for (PotionEffect p : getActiveMasks()) {
            ((Player) getPlayer()).removePotionEffect(p.getType());
        }
        ignoreFallDamage = false;
        activeMasks.clear();
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
                PotionEffect p = new PotionEffect(PotionEffectType.getByName(is), 1000000, amp);
                p.apply((Player) this.getPlayer());
                po.add(p);
            } catch (IllegalArgumentException ex) {
                HeadsPlus.getInstance().getLogger().severe("Invalid potion type detected. Please check your masks configuration in headsa.yml!");
            }
        }
        activeMasks.addAll(po);
    }

    public int getXp() {
        return xp;
    }

    public RLevel getLevel() {
        return level;
    }

    public RLevel getNextLevel() {
        return nextLevel;
    }

    public List<Challenge> getCompleteChallenges() {
        return completeChallenges;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public static HPPlayer getHPPlayer(OfflinePlayer p) {
        for (HPPlayer hp : players) {
            if (hp.player.equals(p)) {
                return hp;
            }
        }
        HPPlayer pl = new HPPlayer(p);
        players.add(pl);
        return pl;
    }

    public List<PotionEffect> getActiveMasks() {
        return activeMasks;
    }

    public void addCompleteChallenge(Challenge c) {
        PlayerScores scores = HeadsPlus.getInstance().getScores();
        scores.completeChallenge(player.getUniqueId().toString(), c);
        completeChallenges.add(c);
    }

    public void addXp(int xp) {
        HeadsPlus hp = HeadsPlus.getInstance();
        PlayerScores scores = HeadsPlus.getInstance().getScores();
        scores.addXp(player.getUniqueId().toString(), xp);
        this.xp += xp;
        if (hp.usingLevels()) {
            if (nextLevel != null) {
                if (nextLevel.getRequiredXP() <= getXp()) {
                    LevelUpEvent event = new LevelUpEvent(player.getPlayer(), level, nextLevel);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        level = nextLevel;
                        if (hp.getConfiguration().getMechanics().getBoolean("broadcasts.level-up")) {
							final String name = player.isOnline() ? player.getPlayer().getDisplayName() : player.getName();
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(HeadsPlus.getInstance().getMessagesConfig().getString("level-up")
                                        .replaceAll("\\{name}", name)
                                        .replaceAll("\\{level}", ChatColor.translateAlternateColorCodes('&', level.getDisplayName())));
                            }
                        }
                        HashMap<Integer, RLevel> levels = HeadsPlus.getInstance().getLevels();
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
            }
        }
    }

    public boolean hasHeadFavourited(String s) {
        return favouriteHeads.contains(s);
    }

    public void addFavourite(String s) {
        favouriteHeads.add(s);
        HeadsPlus.getInstance().getFavourites().writeData(player, s);
    }

    public void removeFavourite(String s) {
        favouriteHeads.remove(s);
        HeadsPlus.getInstance().getFavourites().removeHead(player, s);
    }

    public boolean isIgnoringFallDamage() {
        return ignoreFallDamage;
    }

    public List<String> getFavouriteHeads() {
        return favouriteHeads;
    }
}

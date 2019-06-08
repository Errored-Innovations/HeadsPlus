package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class HPUtils {

    private static HashMap<Player, BossBar> bossBars = new HashMap<>();
    public static boolean switchBoolean(boolean b) {
        return !b;
    }

    public static void addBossBar(OfflinePlayer pl) {
        HPPlayer p = HPPlayer.getHPPlayer(pl);
        ConfigurationSection c = HeadsPlus.getInstance().getConfiguration().getMechanics();
        if (c.getBoolean("boss-bar.enabled")) {
            if (p.getNextLevel() != null) {
                try {
                    if (!bossBars.containsKey(pl.getPlayer())) {
                        String s = ChatColor.translateAlternateColorCodes('&', c.getString("boss-bar.title"));
                        BossBar bossBar = Bukkit.getServer().createBossBar(s, BarColor.valueOf(c.getString("boss-bar.color")), BarStyle.SEGMENTED_6);
                        bossBar.addPlayer(pl.getPlayer());
                        Double d = (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
                        d = 1 - d;
                        bossBar.setProgress(d);
                        bossBar.setVisible(true);
                        bossBars.put(pl.getPlayer(), bossBar);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                bossBar.setVisible(false);
                                bossBar.removePlayer(pl.getPlayer());
                                bossBars.remove(pl.getPlayer());
                            }
                        }.runTaskLater(HeadsPlus.getInstance(), c.getInt("boss-bar.lifetime") * 20);
                    } else {
                        Double d = (double) (p.getNextLevel().getRequiredXP() - p.getXp()) / (double) (p.getNextLevel().getRequiredXP() - p.getLevel().getRequiredXP());
                        d = 1 - d;
                        bossBars.get(pl.getPlayer()).setProgress(d);
                    }
                } catch (NoClassDefFoundError ignored) {

                }

            }
        }
    }
}

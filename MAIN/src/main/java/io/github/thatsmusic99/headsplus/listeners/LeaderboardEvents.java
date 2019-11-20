package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.HeadCraftEvent;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.SellHeadEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.util.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardEvents implements Listener {

    private final HeadsPlus hp = HeadsPlus.getInstance();

    @EventHandler
    public void onHeadDrop(EntityHeadDropEvent e) {
        try {
            if (!e.isCancelled()) {
                if (e.getPlayer() != null) {
                    if (hp.isUsingLeaderboards()) {
                        if (hp.getConfiguration().getPerks().smite_on_head) {
                            for (int i = 0; i < 5; ++i) {
                                e.getLocation().getWorld().strikeLightning(e.getPlayer().getLocation());
                            }
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                DataManager.addToTotal(e.getPlayer(), e.getEntityType().name(), "headspluslb", 1);
                            }
                        }.runTaskAsynchronously(HeadsPlus.getInstance());

                    }
                }
            }
        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (LeaderboardEvents)", false, null);
        }

    }

    @EventHandler
    public void onPHeadDrop(PlayerHeadDropEvent e) {
        try {
            if (!e.isCancelled()) {
                if (e.getKiller() != null) {
                    if (hp.isUsingLeaderboards()) {
                        if (hp.getConfiguration().getPerks().smite_on_head) {
                            for (int i = 0; i < 5; ++i) {
                                e.getLocation().getWorld().strikeLightning(e.getKiller().getLocation());
                            }
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                DataManager.addToTotal(e.getKiller(), "player", "headspluslb", 1);
                            }
                        }.runTaskAsynchronously(HeadsPlus.getInstance());

                    }
                }
            }
        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (LeaderboardEvents)", false, null);
        }

    }

    @EventHandler
    public void onHeadSold(SellHeadEvent e) {
        try {
            if (!e.isCancelled()) {
                if (hp.hasChallengesEnabled()) {
                    for (int is : e.getEntityAmounts().values()) {
                        HPPlayer.getHPPlayer(e.getPlayer()).addXp(20 * is);
                    }
                    for (String s : e.getEntityAmounts().keySet()) {
                        for (int i : e.getEntityAmounts().values()) {
                            if (e.getEntityAmounts().get(s) == i) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        DataManager.addToTotal(e.getPlayer(), HeadsPlus.getInstance().getAPI().strToEntityType(s).name(), "headsplussh", i);
                                    }
                                }.runTaskAsynchronously(HeadsPlus.getInstance());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (LeaderboardEvents)", false, null);
        }
    }

    @EventHandler
    public void onHeadCraft(HeadCraftEvent e) {
        try {
            if (!e.isCancelled()) {
                if (hp.hasChallengesEnabled()) {
                    if (e.getEntityType() != null) {
                        if (!(e.getEntityType().equalsIgnoreCase("invalid") || e.getEntityType().isEmpty())) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    HPPlayer.getHPPlayer(e.getPlayer()).addXp(30 * e.getHeadsCrafted());
                                    DataManager.addToTotal(e.getPlayer(), HeadsPlus.getInstance().getAPI().strToEntityType(e.getEntityType()).name(), "headspluscraft", e.getHeadsCrafted());
                                }
                            }.runTaskAsynchronously(HeadsPlus.getInstance());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (LeaderboardEvents)", false, null);
        }
    }
}

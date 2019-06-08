package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.HeadCraftEvent;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.SellHeadEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
                        hp.getMySQLAPI().addOntoValue(e.getPlayer(), e.getEntityType().name(), "headspluslb", 1);
                    }
                }
            }
        } catch (Exception ex) {
            new DebugPrint(ex, "Event (LeaderboardEvents)", false, null);
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
                        hp.getMySQLAPI().addOntoValue(e.getKiller(), "player", "headspluslb", 1);
                    }
                }
            }
        } catch (Exception ex) {
            new DebugPrint(ex, "Event (LeaderboardEvents)", false, null);
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
                                hp.getMySQLAPI().addOntoValue(e.getPlayer(), HeadsPlus.getInstance().getAPI().strToEntityType(s).name(), "headsplussh", i);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            new DebugPrint(ex, "Event (LeaderboardEvents)", false, null);
        }
    }

    @EventHandler
    public void onHeadCraft(HeadCraftEvent e) {
        try {
            if (!e.isCancelled()) {
                if (hp.hasChallengesEnabled()) {
                    if (e.getEntityType() != null) {
                        if (!(e.getEntityType().equalsIgnoreCase("invalid") || e.getEntityType().isEmpty())) {
                            HPPlayer.getHPPlayer(e.getPlayer()).addXp(30 * e.getHeadsCrafted());
                            hp.getMySQLAPI().addOntoValue(e.getPlayer(), HeadsPlus.getInstance().getAPI().strToEntityType(e.getEntityType()).name(), "headspluscraft", e.getHeadsCrafted());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            new DebugPrint(ex, "Event (LeaderboardEvents)", false, null);
        }
    }
}

package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.HeadCraftEvent;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.SellHeadEvent;
import io.github.thatsmusic99.headsplus.managers.DataManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardEvents implements Listener {

    public LeaderboardEvents() {
        super();
        HeadsPlus hp = HeadsPlus.getInstance();
        HeadsPlusListener<?> listener;
        Bukkit.getPluginManager().registerEvent(EntityHeadDropEvent.class, listener = new HeadsPlusListener<EntityHeadDropEvent>() {
            @Override
            public void onEvent(EntityHeadDropEvent event) {
                if (!event.isCancelled()) {
                    if (event.getPlayer() != null) {
                        Player player = event.getPlayer();
                        if (hp.isUsingLeaderboards()) {
                            if (hp.getConfiguration().getPerks().smite_on_head) {
                                for (int i = 0; i < 5; ++i) {
                                    event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                                }
                            }
                            HPPlayer.getHPPlayer(player).addXp(hp.getConfiguration().getMechanics().getInt("xp.head-drops") * event.getAmount());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    DataManager.addToTotal(player, event.getEntityType().name(), "headspluslb", event.getAmount());
                                }
                            }.runTaskAsynchronously(hp);

                        }
                    }
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(EntityHeadDropEvent.class, "EntityHeadDropEvent", listener), hp);

        Bukkit.getPluginManager().registerEvent(PlayerHeadDropEvent.class, listener = new HeadsPlusListener<PlayerHeadDropEvent>() {
            @Override
            public void onEvent(PlayerHeadDropEvent event) {
                if (!event.isCancelled()) {
                    if (event.getPlayer() != null) {
                        Player player = event.getPlayer();
                        if (hp.isUsingLeaderboards()) {
                            if (hp.getConfiguration().getPerks().smite_on_head) {
                                for (int i = 0; i < 5; ++i) {
                                    event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                                }
                            }
                            HPPlayer.getHPPlayer(player).addXp(hp.getConfiguration().getMechanics().getInt("xp.head-drops") * event.getAmount());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    DataManager.addToTotal(player, "player", "headspluslb", event.getAmount());
                                }
                            }.runTaskAsynchronously(hp);

                        }
                    }
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(PlayerHeadDropEvent.class, "PlayerHeadDropEvent", listener), hp);

        Bukkit.getPluginManager().registerEvent(SellHeadEvent.class, listener = new HeadsPlusListener<SellHeadEvent>() {
            @Override
            public void onEvent(SellHeadEvent event) {
                if (!event.isCancelled()) {
                    if (hp.isUsingLeaderboards()) {
                        for (int is : event.getEntityAmounts().values()) {
                            HPPlayer.getHPPlayer(event.getPlayer()).addXp(hp.getConfiguration().getMechanics().getInt("xp.selling") * is);
                        }
                        for (String s : event.getEntityAmounts().keySet()) {
                            for (int i : event.getEntityAmounts().values()) {
                                if (event.getEntityAmounts().get(s) == i) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            DataManager.addToTotal(event.getPlayer(), s, "headsplussh", i);
                                        }
                                    }.runTaskAsynchronously(hp);
                                }
                            }
                        }
                    }
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(SellHeadEvent.class, "SellHeadEvent", listener), hp);

        Bukkit.getPluginManager().registerEvent(HeadCraftEvent.class, listener = new HeadsPlusListener<HeadCraftEvent>() {
            @Override
            public void onEvent(HeadCraftEvent event) {
                if (!event.isCancelled()) {
                    if (hp.isUsingLeaderboards()) {
                        if (event.getEntityType() != null) {
                            if (!(event.getEntityType().equalsIgnoreCase("invalid") || event.getEntityType().isEmpty())) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        HPPlayer.getHPPlayer(event.getPlayer()).addXp(hp.getConfiguration().getMechanics().getInt("xp.crafting") * event.getHeadsCrafted());
                                        DataManager.addToTotal(event.getPlayer(), event.getEntityType(), "headspluscraft", event.getHeadsCrafted());
                                    }
                                }.runTaskAsynchronously(hp);
                            }
                        }
                    }
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(HeadCraftEvent.class, "HeadCraftEvent", listener), hp);
    }
}

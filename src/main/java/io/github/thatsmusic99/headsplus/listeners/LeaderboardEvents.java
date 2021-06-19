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

public class LeaderboardEvents implements Listener {



    public LeaderboardEvents() {
        super();
        HeadsPlus hp = HeadsPlus.getInstance();
        HeadsPlusListener<?> listener;
        Bukkit.getPluginManager().registerEvent(EntityHeadDropEvent.class, listener = new HeadsPlusListener<EntityHeadDropEvent>() {
            @Override
            public void onEvent(EntityHeadDropEvent event) {
                if (event.getPlayer() == null) return;
                Player player = event.getPlayer();
                if (!hp.isUsingLeaderboards()) return;
                if (hp.getConfiguration().getPerks().smite_on_head) {
                    for (int i = 0; i < 5; ++i) {
                        event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                    }
                }
                HPPlayer.getHPPlayer(player).addXp(hp.getConfiguration().getMechanics().getInt("xp.head-drops") * event.getAmount());
                Bukkit.getScheduler().runTaskAsynchronously(hp, () -> DataManager.addToTotal(player, event.getEntityType().name(), "headspluslb", event.getAmount()));
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(EntityHeadDropEvent.class, "EntityHeadDropEvent", listener), hp, true);

        Bukkit.getPluginManager().registerEvent(PlayerHeadDropEvent.class, listener = new HeadsPlusListener<PlayerHeadDropEvent>() {
            @Override
            public void onEvent(PlayerHeadDropEvent event) {
                    if (event.getPlayer() == null) return;
                    Player player = event.getPlayer();
                    if (!hp.isUsingLeaderboards()) return;
                    if (hp.getConfiguration().getPerks().smite_on_head) {
                        for (int i = 0; i < 5; ++i) {
                            event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                        }
                    }
                    HPPlayer.getHPPlayer(player).addXp(hp.getConfiguration().getMechanics().getInt("xp.head-drops") * event.getAmount());
                    Bukkit.getScheduler().runTaskAsynchronously(hp, () -> DataManager.addToTotal(player, "player", "headspluslb", event.getAmount()));
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(PlayerHeadDropEvent.class, "PlayerHeadDropEvent", listener), hp, true);

        Bukkit.getPluginManager().registerEvent(SellHeadEvent.class, listener = new HeadsPlusListener<SellHeadEvent>() {
            @Override
            public void onEvent(SellHeadEvent event) {
                for (int is : event.getEntityAmounts().values()) {
                    HPPlayer.getHPPlayer(event.getPlayer()).addXp(hp.getConfiguration().getMechanics().getInt("xp.selling") * is);
                }
                if (!hp.isUsingLeaderboards()) return;
                for (String s : event.getEntityAmounts().keySet()) {
                    for (int i : event.getEntityAmounts().values()) {
                        if (event.getEntityAmounts().get(s) == i) {
                            Bukkit.getScheduler().runTaskAsynchronously(hp, () -> DataManager.addToTotal(event.getPlayer(), s, "headsplussh", i));
                        }
                    }
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(SellHeadEvent.class, "SellHeadEvent", listener), hp, true);

        Bukkit.getPluginManager().registerEvent(HeadCraftEvent.class, listener = new HeadsPlusListener<HeadCraftEvent>() {
            @Override
            public void onEvent(HeadCraftEvent event) {
                if (!hp.isUsingLeaderboards() || event.getEntityType() == null) return;
                if (!(event.getEntityType().equalsIgnoreCase("invalid") || event.getEntityType().isEmpty())) {
                    Bukkit.getScheduler().runTaskAsynchronously(hp, () -> {
                        HPPlayer.getHPPlayer(event.getPlayer()).addXp(hp.getConfiguration().getMechanics().getInt("xp.crafting") * event.getHeadsCrafted());
                        DataManager.addToTotal(event.getPlayer(), event.getEntityType(), "headspluscraft", event.getHeadsCrafted());
                    });
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(HeadCraftEvent.class, "HeadCraftEvent", listener), hp, true);
    }
}

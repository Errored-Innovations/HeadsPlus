package io.github.thatsmusic99.headsplus.listeners;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.HeadCraftEvent;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.SellHeadEvent;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.DataManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LeaderboardListeners implements Listener {

    public LeaderboardListeners() {

        for (HeadsPlusListener<?> listener : Lists.newArrayList(new EntityDropHeadListener(),
                new SellHeadListener(),
                new PlayerHeadListener(),
                new HeadCraftListener())) {
            if (listener.shouldEnable()) {
                listener.init();
            }
        }
    }

    private static class EntityDropHeadListener extends HeadsPlusListener<EntityHeadDropEvent> {

        @Override
        public void onEvent(EntityHeadDropEvent event) {
            if (event.getPlayer() == null) return;
            Player player = event.getPlayer();
            HPPlayer.getHPPlayer(player).addXp(0 * event.getAmount());
            if (MainConfig.get().getMiscellaneous().SMITE_PLAYER) {
                for (int i = 0; i < 5; ++i) {
                    event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                }
            }
            if (!MainConfig.get().getMainFeatures().LEADERBOARDS) return;
            Bukkit.getScheduler().runTaskAsynchronously(hp, () -> DataManager.addToTotal(player, event.getEntityType().name(), "headspluslb", event.getAmount()));
        }

        @Override
        public void init() {
            Bukkit.getPluginManager().registerEvent(EntityHeadDropEvent.class, this, EventPriority.MONITOR,
                    new HeadsPlusEventExecutor(EntityHeadDropEvent.class, "EntityHeadDropEvent", this), hp, true);

        }

        @Override
        public boolean shouldEnable() {
            return MainConfig.get().getMainFeatures().MOB_DROPS;
        }
    }

    private static class SellHeadListener extends HeadsPlusListener<SellHeadEvent> {

        @Override
        public void onEvent(SellHeadEvent event) {
            for (int is : event.getEntityAmounts().values()) {
                HPPlayer.getHPPlayer(event.getPlayer()).addXp(0 * is);
            }
            if (!MainConfig.get().getMainFeatures().LEADERBOARDS) return;
            for (String s : event.getEntityAmounts().keySet()) {
                for (int i : event.getEntityAmounts().values()) {
                    if (event.getEntityAmounts().get(s) != i) continue;
                    Bukkit.getScheduler().runTaskAsynchronously(hp, () -> DataManager.addToTotal(event.getPlayer(), s, "headsplussh", i));
                }
            }
        }

        @Override
        public void init() {
            Bukkit.getPluginManager().registerEvent(SellHeadEvent.class, this, EventPriority.MONITOR,
                    new HeadsPlusEventExecutor(SellHeadEvent.class, "SellHeadEvent", this), hp, true);
        }

        @Override
        public boolean shouldEnable() {
            return MainConfig.get().getMainFeatures().SELL_HEADS;
        }
    }

    private static class PlayerHeadListener extends HeadsPlusListener<PlayerHeadDropEvent> {

        @Override
        public void onEvent(PlayerHeadDropEvent event) {
            if (event.getPlayer() == null) return;
            Player player = event.getPlayer();
            HPPlayer.getHPPlayer(player).addXp(0 * event.getAmount());
            if (MainConfig.get().getMiscellaneous().SMITE_PLAYER) {
                for (int i = 0; i < 5; ++i) {
                    event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                }
            }
            if (!MainConfig.get().getMainFeatures().LEADERBOARDS) return;
            Bukkit.getScheduler().runTaskAsynchronously(hp, () -> DataManager.addToTotal(player, "player", "headspluslb", event.getAmount()));

        }

        @Override
        public void init() {
            Bukkit.getPluginManager().registerEvent(PlayerHeadDropEvent.class, this, EventPriority.MONITOR,
                    new HeadsPlusEventExecutor(PlayerHeadDropEvent.class, "PlayerHeadDropEvent", this), hp, true);
        }

        @Override
        public boolean shouldEnable() {
            return MainConfig.get().getMainFeatures().MOB_DROPS;
        }
    }

    private static class HeadCraftListener extends HeadsPlusListener<HeadCraftEvent> {

        @Override
        public void onEvent(HeadCraftEvent event) {
            HPPlayer.getHPPlayer(event.getPlayer()).addXp(0 * event.getHeadsCrafted());
            if (!MainConfig.get().getMainFeatures().LEADERBOARDS || event.getEntityType() == null) return;
            if (!(event.getEntityType().equalsIgnoreCase("invalid") || event.getEntityType().isEmpty())) {
                Bukkit.getScheduler().runTaskAsynchronously(hp, () -> {
                    DataManager.addToTotal(event.getPlayer(), event.getEntityType(), "headspluscraft", event.getHeadsCrafted());
                });
            }
        }

        @Override
        public void init() {
            Bukkit.getPluginManager().registerEvent(HeadCraftEvent.class, this, EventPriority.MONITOR,
                    new HeadsPlusEventExecutor(HeadCraftEvent.class, "HeadCraftEvent", this), hp, true);

        }
    }
}

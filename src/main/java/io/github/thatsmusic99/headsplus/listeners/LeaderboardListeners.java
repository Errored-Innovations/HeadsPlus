package io.github.thatsmusic99.headsplus.listeners;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.events.HeadCraftEvent;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.sql.StatisticsSQLManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LeaderboardListeners implements Listener {

    public LeaderboardListeners() {

        for (HeadsPlusListener<?> listener : Lists.newArrayList(new EntityDropHeadListener(),
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
            HPPlayer.getHPPlayer(player.getUniqueId()).addXp((long) event.getHeadInfo().getXp() * event.getAmount());
            if (MainConfig.get().getMiscellaneous().SMITE_PLAYER) {
                for (int i = 0; i < 5; ++i) {
                    event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                }
            }
            if (!MainConfig.get().getMainFeatures().LEADERBOARDS) return;
            StatisticsSQLManager.get().addToTotal(player.getUniqueId(), StatisticsSQLManager.CollectionType.HUNTING,
                    event.getHeadInfo().getId(), "entity=" + event.getEntityType().name(), event.getAmount());
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

    private static class PlayerHeadListener extends HeadsPlusListener<PlayerHeadDropEvent> {

        @Override
        public void onEvent(PlayerHeadDropEvent event) {
            if (event.getPlayer() == null) return;
            Player player = event.getPlayer();
            HPPlayer.getHPPlayer(player.getUniqueId()).addXp(0 * event.getAmount());
            if (MainConfig.get().getMiscellaneous().SMITE_PLAYER) {
                for (int i = 0; i < 5; ++i) {
                    event.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                }
            }
            if (!MainConfig.get().getMainFeatures().LEADERBOARDS) return;
            StatisticsSQLManager.get().addToTotal(player.getUniqueId(), StatisticsSQLManager.CollectionType.HUNTING,
                    event.getDeadPlayer().getName(), "entity=PLAYER", event.getAmount());

        }

        @Override
        public void init() {
            Bukkit.getPluginManager().registerEvent(PlayerHeadDropEvent.class, this, EventPriority.MONITOR,
                    new HeadsPlusEventExecutor(PlayerHeadDropEvent.class, "PlayerHeadDropEvent", this), hp, true);
        }

        @Override
        public boolean shouldEnable() {
            return MainConfig.get().getMainFeatures().PLAYER_DROPS;
        }
    }

    private static class HeadCraftListener extends HeadsPlusListener<HeadCraftEvent> {

        @Override
        public void onEvent(HeadCraftEvent event) {
            HPPlayer.getHPPlayer(event.getPlayer().getUniqueId()).addXp(0 * event.getHeadsCrafted());
            if (!MainConfig.get().getMainFeatures().LEADERBOARDS || event.getType() == null) return;
            if (event.getType().equalsIgnoreCase("invalid") || event.getType().isEmpty()) return;
            StatisticsSQLManager.get().addToTotal(event.getPlayer().getUniqueId(), StatisticsSQLManager.CollectionType.CRAFTING,
                    event.getType(), "", event.getHeadsCrafted());
        }

        @Override
        public void init() {
            Bukkit.getPluginManager().registerEvent(HeadCraftEvent.class, this, EventPriority.MONITOR,
                    new HeadsPlusEventExecutor(HeadCraftEvent.class, "HeadCraftEvent", this), hp, true);

        }
    }
}

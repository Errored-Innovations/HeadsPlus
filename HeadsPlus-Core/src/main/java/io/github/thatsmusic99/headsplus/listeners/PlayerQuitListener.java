package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.MaskManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener extends HeadsPlusListener<PlayerQuitEvent> {

    @Override
    public void onEvent(PlayerQuitEvent event) {
        HPPlayer.removePlayer(event.getPlayer().getUniqueId());
        if (!MainConfig.get().getMainFeatures().MASKS) return;
        MaskManager.get().resetMask(event.getPlayer());
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerQuitEvent.class, "PlayerQuitEvent", this), HeadsPlus.get());

    }
}

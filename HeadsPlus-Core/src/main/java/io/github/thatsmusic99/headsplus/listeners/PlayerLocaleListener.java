package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

public class PlayerLocaleListener extends HeadsPlusListener<PlayerLocaleChangeEvent> {

    @Override
    public void onEvent(PlayerLocaleChangeEvent event) {
        MessagesManager.get().setPlayerLocale(event.getPlayer(), event.getLocale());
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(PlayerLocaleChangeEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerLocaleChangeEvent.class, "PlayerLocaleChangeEvent", this), HeadsPlus.get());

    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getLocalisation().SMART_LOCALE;
    }
}

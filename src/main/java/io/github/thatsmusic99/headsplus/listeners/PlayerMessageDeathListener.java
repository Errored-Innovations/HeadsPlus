package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Random;

public class PlayerMessageDeathListener extends HeadsPlusListener<PlayerHeadDropEvent> {

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(PlayerHeadDropEvent.class, this, EventPriority.MONITOR,
                new HeadsPlusEventExecutor(PlayerHeadDropEvent.class, "PlayerHeadDropEvent", this), HeadsPlus.get(), true);
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getPlayerDrops().ENABLE_PLAYER_DEATH_MESSAGES;
    }

    public void onEvent(PlayerHeadDropEvent event) {
        if (event.getPlayer() == null) return;
        Random r = new Random();
        List<String> s = MainConfig.get().getPlayerDrops().PLAYER_HEAD_DEATH_MESSAGES;
        int thing = r.nextInt(s.size());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("headsplus.death.ignore")) continue;
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    s.get(thing).replaceAll("\\{header}", HeadsPlusMessagesManager.get().getString("prefix", p)).replaceAll("\\{killer}", event.getPlayer().getName()).replaceAll("\\{player}", event.getDeadPlayer().getName())));

        }
    }

}

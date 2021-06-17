package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;

public class HPPlayerMessageDeathEvent extends HeadsPlusListener<PlayerHeadDropEvent> {

    public HPPlayerMessageDeathEvent() {
        super();
        Bukkit.getPluginManager().registerEvent(PlayerHeadDropEvent.class, this, EventPriority.MONITOR,
                new HeadsPlusEventExecutor(PlayerHeadDropEvent.class, "PlayerHeadDropEvent", this), HeadsPlus.getInstance());
    }

    public void onEvent(PlayerHeadDropEvent event) {
        try {
            if (!event.isCancelled()) {
                HeadsPlus hp = HeadsPlus.getInstance();
                if (hp.isDeathMessagesEnabled()) {
                    if (event.getPlayer() != null) {
                        Random r = new Random();
                        List<String> s = hp.getConfiguration().getPerks().death_messages;
                        int thing = r.nextInt(s.size());
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!p.hasPermission("headsplus.death.ignore")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        s.get(thing).replaceAll("\\{header}", hp.getMessagesConfig().getString("prefix", p)).replaceAll("\\{killer}", event.getPlayer().getName()).replaceAll("\\{player}", event.getDeadPlayer().getName())));
                            }
                        }
                    }
                }
            }

        } catch (IllegalArgumentException ignored) {

        }
    }
}

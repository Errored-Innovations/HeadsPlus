package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;

public class PlayerDeathEvent implements Listener {

    @EventHandler
    public void onDeath(PlayerHeadDropEvent e) {
        try {
            HeadsPlus hp = HeadsPlus.getInstance();
            if (hp.isDeathMessagesEnabled()) {
                if (e.getKiller() != null) {
                    Random r = new Random();
                    List<String> s = hp.getConfiguration().getPerks().death_messages;
                    int thing = r.nextInt(s.size());
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.hasPermission("headsplus.death.ignore")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    s.get(thing).replaceAll("\\{header}", hp.getMessagesConfig().getString("prefix")).replaceAll("\\{killer}", e.getKiller().getName()).replaceAll("\\{player}", e.getDeadPlayer().getName())));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            new DebugPrint(ex, "Event (PlayerDeathEvent, PlayerHeadDropEvent)", false, null);
        }
    }
}

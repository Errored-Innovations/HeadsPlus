package io.github.thatsmusic99.headsplus.listeners.tabcompleting;

import io.github.thatsmusic99.headsplus.commands.SellHead;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteSellhead implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender cs, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> headIdList = new ArrayList<>(SellHead.getRegisteredIDs());
            headIdList.add("all");
            List<String> suggestions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], headIdList, suggestions);
            return suggestions;
        }
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isVanished(player)) players.add(player.getName());
        }
        return players;
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}

package io.github.thatsmusic99.headsplus.listeners;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabCompleteLB implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            List<String> f = new ArrayList<>();
            List<String> c = new ArrayList<>();
            for (EntityType e : new DeathEvents().ableEntities) {
                c.add(WordUtils.capitalize(e.name()));
            }
            c.add("Total");
            c.add("Player");
            StringUtil.copyPartialMatches(args[0], c, f);
            Collections.sort(f);
            return f;
        }
        return new ArrayList<>();
    }
}

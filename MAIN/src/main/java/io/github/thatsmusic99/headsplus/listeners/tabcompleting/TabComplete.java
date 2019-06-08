package io.github.thatsmusic99.headsplus.listeners.tabcompleting;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender cs, Command command, String s, String[] args) {
        if (args.length == 1) {
            List<String> f = new ArrayList<>();
            List<String> c = new ArrayList<>();
            for (IHeadsPlusCommand key : HeadsPlus.getInstance().getCommands()) {
                CommandInfo cmd = key.getClass().getAnnotation(CommandInfo.class);
                if (cs.hasPermission(cmd.permission())) {
                    if (cmd.maincommand()) {
                        c.add(cmd.subcommand());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[0], c, f);
            Collections.sort(f);
            return f;
        } else if (args.length >= 2) {
            List<String> f = new ArrayList<>();
            switch (args[0].toLowerCase()) {
                case "blacklistadd":
                case "blacklistdel":
                case "whitelistadd":
                case "whitelistdel":
                    StringUtil.copyPartialMatches(args[1], players(), f);
                    break;
                case "blacklist":
                case "blacklistw":
                case "whitelist":
                case "whitelistw":
                    StringUtil.copyPartialMatches(args[1], new ArrayList<>(Arrays.asList("on", "off")), f);
                    break;
                case "blacklistwadd":
                case "blacklistwdel":
                case "whitelistwadd":
                case "whitelistwdel":
                    StringUtil.copyPartialMatches(args[1], worlds(), f);
                    break;
                case "debug":
                    StringUtil.copyPartialMatches(args[1], new ArrayList<>(Arrays.asList("dump", "head", "player")), f);
                    break;
                case "head":
                    StringUtil.copyPartialMatches(args[1], new ArrayList<>(Collections.singletonList("view")), f);
                    break;
            }
            Collections.sort(f);
            return f;
        }
        return players();
    }

    private static List<String> players() {
        List<String> p = new ArrayList<>();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            p.add(pl.getName());
        }
        return p;
    }
    private static List<String> worlds() {
        List<String> w = new ArrayList<>();
        for (World wo : Bukkit.getWorlds()) {
            w.add(wo.getName());
        }
        return w;
    }
}

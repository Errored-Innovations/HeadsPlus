package io.github.thatsmusic99.headsplus.listeners.tabcompleting;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String s, String[] args) {
        if (args.length == 1) {
            List<String> f = new ArrayList<>();
            List<String> c = new ArrayList<>();
            for (IHeadsPlusCommand key : HeadsPlus.getInstance().getCommands().values()) {
                CommandInfo command = key.getClass().getAnnotation(CommandInfo.class);
                if (cs.hasPermission(command.permission())) {
                    if (command.maincommand()) {
                        c.add(command.subcommand());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[0], c, f);
            Collections.sort(f);
            return f;
        } else if (args.length >= 2) {
            IHeadsPlusCommand command = getCommandByName(args[0]);

            if (command != null) {
                CommandInfo commandInfo = command.getClass().getAnnotation(CommandInfo.class);
                if (cs.hasPermission(commandInfo.permission())) {
                    List<String> results = command.onTabComplete(cs, cmd, s, args);
                    Collections.sort(results);
                    return results;
                }
                return IHeadsPlusCommand.getPlayers(cs);
            }
            return IHeadsPlusCommand.getPlayers(cs);
        }
        return IHeadsPlusCommand.getPlayers(cs);
    }

    private IHeadsPlusCommand getCommandByName(String name) {
        for (IHeadsPlusCommand hpc : HeadsPlus.getInstance().getCommands().values()) {
            CommandInfo c = hpc.getClass().getAnnotation(CommandInfo.class);
            if (c.commandname().equalsIgnoreCase(name)) {
                if (c.maincommand()){
                    return hpc;
                }
            }
        }
        return null;
    }
}

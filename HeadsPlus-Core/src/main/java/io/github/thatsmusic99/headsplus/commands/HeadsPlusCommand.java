package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeadsPlusCommand implements CommandExecutor, TabCompleter {

    private final MessagesManager hpc = MessagesManager.get();

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        try {
            if (args.length == 0) {
                HeadsPlus.get().getCommands().get("help").onCommand(sender, cmd, label, args);
                return true;
            }
            IHeadsPlusCommand command = HeadsPlus.get().getCommands().get(args[0].toLowerCase());
            if (command == null) {
                HeadsPlus.get().getCommands().get("help").onCommand(sender, cmd, label, args);
                return true;
            }
            CommandInfo c = command.getClass().getAnnotation(CommandInfo.class);
            if (!sender.hasPermission(c.permission())) {
                hpc.sendMessage("commands.errors.no-perm", sender);
                return true;
            }

            if (!c.maincommand()) {
                HeadsPlus.get().getCommands().get("help").onCommand(sender, cmd, label, args);
                return true;
            }
            try {
                if (command.onCommand(sender, cmd, label, args)) {
                    return true;
                }
                // TODO - should be a translatable
                sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + c.usage());
                if (command.advancedUsages().length == 0) return true;
                sender.sendMessage(ChatColor.DARK_RED + "Further usages:");
                for (String s : command.advancedUsages()) {
                    sender.sendMessage(ChatColor.RED + s);
                }

            } catch (NumberFormatException numberEx) {
                hpc.sendMessage("commands.errors.invalid-input-int", sender);
            } catch (Exception ex) {
                DebugPrint.createReport(ex, "Subcommand (" + c.commandname() + ")", true, sender);
            }
            return true;
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (headsplus)", true, sender);
        }
		return false;
	}

    @Override
    public List<String> onTabComplete(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length == 1) {
            List<String> f = new ArrayList<>();
            List<String> c = new ArrayList<>();
            for (IHeadsPlusCommand key : HeadsPlus.get().getCommands().values()) {
                CommandInfo command = key.getClass().getAnnotation(CommandInfo.class);
                if (cs.hasPermission(command.permission())) {
                    if (command.maincommand()) {
                        c.add(command.commandname());
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
	    return HeadsPlus.get().getCommands().get(name.toLowerCase());
    }
}
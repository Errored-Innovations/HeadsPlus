package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.commands.maincommand.HelpMenu;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeadsPlusCommand implements CommandExecutor, TabCompleter {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if ((cmd.getName().equalsIgnoreCase("headsplus")) || (cmd.getName().equalsIgnoreCase("hp"))) {
                if (args.length > 0) {
                    IHeadsPlusCommand command = HeadsPlus.get().getCommands().get(args[0].toLowerCase());
                    if (command != null) {
                        CommandInfo c = command.getClass().getAnnotation(CommandInfo.class);
                        if (sender.hasPermission(c.permission())) {
                            if (c.maincommand()) {
                                try {
                                    if (command.fire(args, sender)) {
                                        return true;
                                    } else {
                                        sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + c.usage());
                                        if (command.advancedUsages().length != 0) {
                                            sender.sendMessage(ChatColor.DARK_RED + "Further usages:");
                                            for (String s : command.advancedUsages()) {
                                                sender.sendMessage(ChatColor.RED + s);
                                            }
                                        }
                                    }
                                } catch (NumberFormatException numberEx) {
                                    hpc.sendMessage("commands.errors.invalid-input-int", sender);
                                } catch (Exception ex) {
                                    DebugPrint.createReport(ex, "Subcommand (" + c.commandname() + ")", true, sender);
                                }

                            } else {
                                new HelpMenu().fire(args, sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.no-perm", sender);
                        }
                    } else {
                        new HelpMenu().fire(args, sender);
                    }
                } else {
                    new HelpMenu().fire(args, sender);
                }

            }

            return false;
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (headsplus)", true, sender);
        }
		return false;
	}

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String s, String[] args) {
        if (args.length == 1) {
            List<String> f = new ArrayList<>();
            List<String> c = new ArrayList<>();
            for (IHeadsPlusCommand key : HeadsPlus.get().getCommands().values()) {
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
        for (IHeadsPlusCommand hpc : HeadsPlus.get().getCommands().values()) {
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
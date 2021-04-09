package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.commands.maincommand.HelpMenu;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HeadsPlusCommand implements CommandExecutor {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if ((cmd.getName().equalsIgnoreCase("headsplus")) || (cmd.getName().equalsIgnoreCase("hp"))) {
                if (args.length > 0) {
                    IHeadsPlusCommand command = HeadsPlus.getInstance().getCommands().get(args[0].toLowerCase());
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
}
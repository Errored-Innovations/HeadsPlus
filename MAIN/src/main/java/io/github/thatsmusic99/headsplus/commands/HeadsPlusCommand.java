package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.commands.maincommand.HelpMenu;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class HeadsPlusCommand implements CommandExecutor {

    private final HashMap<String, Boolean> tests = new HashMap<>();

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	    tests.clear();
        try {
            if ((cmd.getName().equalsIgnoreCase("headsplus")) || (cmd.getName().equalsIgnoreCase("hp"))) {
                tests.put("Subcommand", args.length > 0);

                if (args.length > 0) {
                    tests.put("Valid subcommand", getCommandByName(args[0]) != null);
                    if (getCommandByName(args[0]) != null) {
                        IHeadsPlusCommand command = getCommandByName(args[0]);
                        assert command != null;
                        CommandInfo c = command.getClass().getAnnotation(CommandInfo.class);
                        String correctUsage = command.isCorrectUsage(args, sender);
                        tests.put("No Permission", !sender.hasPermission(c.permission()));
                        tests.put("Main command", c.maincommand());
                        tests.put("Correct Usage", correctUsage.isEmpty());
                        if (sender.hasPermission(c.permission())) {
                            if (c.maincommand()) {
                                if (correctUsage.isEmpty()) {
                                    command.printDebugResults(tests, true);
                                    return command.fire(args, sender);
                                } else {
                                    sender.sendMessage(correctUsage);
                                    sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + c.usage());
                                    if (command.advancedUsages().length != 0) {
                                        sender.sendMessage(ChatColor.DARK_RED + "Further usages:");
                                        for (String s : command.advancedUsages()) {
                                            sender.sendMessage(ChatColor.RED + s);
                                        }
                                    }
                                }
                            } else {
                                new HelpMenu().fire(args, sender);
                            }
                        } else {
                            sender.sendMessage(hpc.getString("commands.errors.no-perm", sender instanceof Player ? (Player) sender : null));
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

	private IHeadsPlusCommand getCommandByName(String name) {
	    for (IHeadsPlusCommand hpc : HeadsPlus.getInstance().getCommands()) {
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